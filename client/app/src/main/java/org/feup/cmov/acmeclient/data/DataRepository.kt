package org.feup.cmov.acmeclient.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.ByteString
import org.feup.cmov.acmeclient.data.api.ApiResponse
import org.feup.cmov.acmeclient.data.api.SignUpRequest
import org.feup.cmov.acmeclient.data.cache.CachedOrder
import org.feup.cmov.acmeclient.data.cache.CachedUser
import org.feup.cmov.acmeclient.data.db.*
import org.feup.cmov.acmeclient.data.db.details.ItemUpdate
import org.feup.cmov.acmeclient.data.model.*
import org.feup.cmov.acmeclient.ui.main.home.ItemView
import org.feup.cmov.acmeclient.utils.Cache
import org.feup.cmov.acmeclient.utils.Crypto
import java.io.IOException
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val webService: WebService,
    private val userDao: UserDao,
    private val itemDao: ItemDao,
    private val voucherDao: VoucherDao,
    private val pastOrderDao: PastOrderDao,
    private val receiptDao: ReceiptDao
) {
    // Auth
    val loggedInUser: CachedUser?
        get() = runBlocking { Cache.cachedUser.first() }

    val isLoggedIn = Cache.cachedUser.map { it != null }

    suspend fun signIn(username: String, password: String): Resource<Nothing> {
        return withContext(Dispatchers.IO) {
            val user = userDao.get(username).first()

            user ?: return@withContext Resource.error("unknown_user")

            if (Crypto.checkPassword(password, user.password)) {
                Cache.cacheUser(user)
                return@withContext Resource.success(null)
            }

            Resource.error("wrong_credentials")
        }
    }

    suspend fun signUp(
        name: String,
        nif: String, ccNumber: String, ccCVV: String, ccExpiration: String,
        username: String, password: String
    ): Resource<Any> {
        return withContext(Dispatchers.IO) {
            if (userDao.get(username).first() != null)
                return@withContext Resource.error("username_taken")

            val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$".toRegex()
            if (!passwordRegex.matches(password))
                return@withContext Resource.error("weak_password")

            // Generate RSA key pair
            val publicKey = Crypto.generateRSAKeyPair(username)

            // Create request
            val request = SignUpRequest(name, nif, ccNumber, ccCVV, ccExpiration, publicKey)
            val response = webService.signUp(request)

            if (response is ApiResponse.Success) {
                val id = response.data!!.userId

                // Hash password and create user
                val hashedPassword = Crypto.hashPassword(password)
                val user =
                    User(id, name, nif, ccNumber, ccCVV, ccExpiration, username, hashedPassword)

                userDao.insert(user)
                Cache.cacheUser(user)
            }

            when (response) {
                is ApiResponse.Success -> Resource.success(response.data)
                is ApiResponse.ApiError -> Resource.error(response.error)
                is ApiResponse.NetworkError -> Resource.error(response.error)
            }
        }
    }

    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            Cache.cacheUser(null)
        }
    }

    // Items

    fun getItems(fetch: Boolean = true): Flow<Resource<List<Item>>> = itemDao.getAll()
        .distinctUntilChanged()
        .map { Resource.success(it) }
        .onStart {
            if (fetch) {
                emit(Resource.loading(null))
                fetchItems()
            }
        }
        .catch { emit(Resource.error(it.message!!)) }
//        .retry(3) { e -> (e is IOException).also { if (it) delay(1000) } }
        .flowOn(Dispatchers.IO)

    fun getItemsAsMap(): Flow<Resource<Map<String, Item>>> = getItems(fetch = false)
        .map { items ->
            when (items.status) {
                Status.LOADING -> Resource.loading(null)
                Status.SUCCESS -> Resource.success(items.data!!.associateBy({ it.id }, { it }))
                Status.ERROR -> Resource.error(items.message!!)
            }
        }

    suspend fun toggleFavoriteItem(item: ItemView) {
        withContext(Dispatchers.IO) {
            val usersFavorite = item.usersFavorite.toMutableSet()

            if (!usersFavorite.removeIf { id -> id == loggedInUser!!.userId })
                usersFavorite.add(loggedInUser!!.userId)

            itemDao.setFavoriteItems(ItemUpdate(item.id, usersFavorite))
        }
    }

    suspend fun fetchItems() {
        withContext(Dispatchers.IO) {
            // Check if menu is updated
            val lastItem = itemDao.getLastAdded().first()
            val response = webService.getItems(lastItem?.addedAt)

            // Update menu if needed
            if (response is ApiResponse.Success)
                itemDao.insertAll(response.data!!)
        }
    }

    // Vouchers

    fun getVouchers(fetch: Boolean = true): Flow<Resource<List<Voucher>>> =
        voucherDao.getAll(loggedInUser!!.userId)
            .distinctUntilChanged()
            .map { Resource.success(it) }
            .onStart {
                if (fetch) {
                    emit(Resource.loading(null))
                    fetchVouchers()
                }
            }
            .catch { emit(Resource.error(it.message!!)) }
            .retry(3) { e -> (e is IOException).also { if (it) delay(1000) } }
            .flowOn(Dispatchers.IO)

    fun getVouchersAsMap(): Flow<Resource<Map<String, Voucher>>> = getVouchers(fetch = false)
        .map { vouchers ->
            when (vouchers.status) {
                Status.LOADING -> Resource.loading(null)
                Status.SUCCESS -> Resource.success(vouchers.data!!.associateBy({ it.id }, { it }))
                Status.ERROR -> Resource.error(vouchers.message!!)
            }
        }

    suspend fun fetchVouchers() {
        withContext(Dispatchers.IO) {
            val response = webService.getVouchers(loggedInUser!!.userId)

            // Update vouchers if needed
            if (response is ApiResponse.Success)
                voucherDao.deleteAndInsert(loggedInUser!!.userId, response.data!!)
        }
    }

    // Past orders

    fun getPastOrders(fetch: Boolean = true): Flow<Resource<List<PastOrder>>> =
        pastOrderDao.getAll(loggedInUser!!.userId)
            .distinctUntilChanged()
            .map { Resource.success(it.sortedByDescending { po -> po.number }) }
            .onStart {
                if (fetch) {
                    emit(Resource.loading(null))
                    fetchPastOrders()
                }
            }
            .catch { emit(Resource.error(it.message!!)) }
//        .retry(3) { e -> (e is IOException).also { if (it) delay(1000) } }
            .flowOn(Dispatchers.IO)

    fun getPastOrdersAsMap(): Flow<Resource<Map<String, PastOrder>>> = getPastOrders(fetch = false)
        .map { orders ->
            when (orders.status) {
                Status.LOADING -> Resource.loading(null)
                Status.SUCCESS -> Resource.success(orders.data!!.associateBy({ it.id }, { it }))
                Status.ERROR -> Resource.error(orders.message!!)
            }
        }

    suspend fun fetchPastOrders() {
        withContext(Dispatchers.IO) {
            val response = webService.getPastOrders(loggedInUser!!.userId)

            // Update vouchers if needed
            if (response is ApiResponse.Success)
                pastOrderDao.insertAll(response.data!!)
        }
    }

    // Order

    fun getOrder(): Flow<CachedOrder> = Cache.cachedOrder
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    suspend fun saveItemToOrder(itemId: String, quantity: Int) {
        withContext(Dispatchers.IO) {
            val order: CachedOrder = Cache.cachedOrder.first()
            val items = order.items.toMutableMap()

            items.compute(itemId) { _, _ ->
                if (quantity == 0) null else quantity
            }

            Cache.cacheOrder(order.copy(items = items))
        }
    }

    suspend fun saveVoucherToOrder(voucherId: String, voucherType: Char) {
        withContext(Dispatchers.IO) {
            val order: CachedOrder = Cache.cachedOrder.first()

            if (voucherType == 'o') {
                val offerVouchers = order.offerVouchers.toMutableSet()

                if (!offerVouchers.removeIf { id -> id == voucherId })
                    offerVouchers.add(voucherId)

                Cache.cacheOrder(order.copy(offerVouchers = offerVouchers))
            } else {
                val discountVoucher = if (order.discountVoucher == voucherId) null else voucherId
                Cache.cacheOrder(order.copy(discountVoucher = discountVoucher))
            }
        }
    }

    fun generateOrderString(): Flow<ByteString> = Cache.cachedOrder
        .distinctUntilChanged()
        .combine(Cache.cachedUser) { order, user -> orderToString(order, user!!) }
        .filter { it != ByteString.EMPTY }
        .flowOn(Dispatchers.IO)

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun orderToString(order: CachedOrder, user: CachedUser): ByteString {
        return withContext(Dispatchers.IO) {
            if (order.items.isEmpty()) return@withContext ByteString.EMPTY

            val dataBuffer = Buffer()

            var firstItem = true
            order.items.forEach { (k, v) ->
                val content = (if (firstItem.also { firstItem = false }) "" else ";") + "$k:$v"
                dataBuffer.write(content.toByteArray())
            }

            order.offerVouchers.forEach { v ->
                dataBuffer.write(";$v".toByteArray())
            }

            if (order.discountVoucher != null)
                dataBuffer.write(";${order.discountVoucher}".toByteArray())

            dataBuffer.write("#${user.userId}".toByteArray())

            val signBuffer = dataBuffer.clone()

            Crypto.sign(user.username, signBuffer).also {
                signBuffer.close()
                dataBuffer.write(":$it".toByteArray())
            }

            dataBuffer.readByteString().also {
                dataBuffer.close()
            }
        }
    }

    suspend fun clearOrder() {
        withContext(Dispatchers.IO) {
            Cache.cacheOrder(null)
        }
    }

    // Receipt

    fun getReceipt(orderId: String, fetch: Boolean = true): Flow<Resource<Receipt>> =
        receiptDao.get(orderId)
            .distinctUntilChanged()
            .map { Resource.success(it) }
            .onStart {
                if (fetch) {
                    emit(Resource.loading(null))
                    fetchReceipt(orderId)
                }
            }
            .catch { emit(Resource.error(it.message!!)) }
            .retry(3) { e -> (e is IOException).also { if (it) delay(1000) } }
            .flowOn(Dispatchers.IO)

    suspend fun fetchReceipt(orderId: String) {
        withContext(Dispatchers.IO) {
            val response = webService.getOrderReceipt(orderId)

            // Update vouchers if needed
            if (response is ApiResponse.Success) {
                receiptDao.insert(response.data!!.copy(orderId = orderId))
            }
        }
    }
}