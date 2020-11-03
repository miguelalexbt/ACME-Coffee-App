package org.feup.cmov.acmeclient.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeclient.utils.Crypto
import org.feup.cmov.acmeclient.data.api.*
import org.feup.cmov.acmeclient.data.db.ItemDao
import org.feup.cmov.acmeclient.data.db.UserDao
import org.feup.cmov.acmeclient.data.db.VoucherDao
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.cache.CachedOrder
import org.feup.cmov.acmeclient.data.cache.CachedUser
import org.feup.cmov.acmeclient.data.model.User
import org.feup.cmov.acmeclient.data.model.Voucher
import org.feup.cmov.acmeclient.utils.Cache
import java.io.IOException

import javax.inject.Inject

class DataRepository @Inject constructor(
    private val webService: WebService,
    private val userDao: UserDao,
    private val itemDao: ItemDao,
    private val voucherDao: VoucherDao
) {
    // Auth
    val loggedInUser: CachedUser?
        get() = runBlocking { Cache.cachedUser.first() }

    val isLoggedIn = Cache.cachedUser.map { it != null }

    suspend fun signIn(username: String, password: String): Resource<Nothing> {
        val user = userDao.get(username).first()

        if (user != null && Crypto.checkPassword(password, user.password)) {
            Cache.cacheUser(user)
            return Resource.success(null)
        }

        return Resource.error("wrong_credentials")
    }

    suspend fun signUp(
        name: String,
        nif: String, ccNumber: String, ccCVV: String, ccExpiration: String,
        username: String, password: String
    ): Resource<Any> {

        // Generate RSA key pair
        val publicKey = Crypto.generateRSAKeyPair(username)

        // Create request
        val request = SignUpRequest(name, nif, ccNumber, ccCVV, ccExpiration, publicKey)
        val response = webService.signUp(request)

        if (response is ApiResponse.Success) {
            val id = response.data!!.userId

            // Hash password and create user
            val hashedPassword = Crypto.hashPassword(password)
            val user = User(id, name, nif, ccNumber, ccCVV, ccExpiration, username, hashedPassword)

            userDao.insert(user)
            Cache.cacheUser(user)
        }

        return mapToResource(response)
    }

    // Items

    fun getItems(): Flow<Resource<List<Item>>> = itemDao.getAll()
        .distinctUntilChanged()
        .map { Resource.success(it) }
        .onStart {
            emit(Resource.loading(null))
            fetchItems()
        }
        .catch { emit(Resource.error(it.message!!)) }
        .retry(3) { e -> (e is IOException).also { if (it) delay(1000) } }
        .flowOn(Dispatchers.IO)

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

    fun getVouchers(): Flow<Resource<List<Voucher>>> = voucherDao.getAll(loggedInUser!!.userId)
        .distinctUntilChanged()
        .map { Resource.success(it) }
        .onStart {
            emit(Resource.loading(null))
            fetchVouchers()
        }
        .catch { emit(Resource.error(it.message!!)) }
        .retry(3) { e -> (e is IOException).also { if (it) delay(1000) } }
        .flowOn(Dispatchers.IO)

    suspend fun fetchVouchers() {
        withContext(Dispatchers.IO) {
            val response = webService.getVouchers(Cache.cachedUser.first()!!.userId)

            // Update vouchers if needed
            if (response is ApiResponse.Success) {
                voucherDao.deleteAll(loggedInUser!!.userId)
                voucherDao.insertAll(response.data!!)
            }
        }
    }

    // Order

    fun getOrder(): Flow<CachedOrder> = Cache.cachedOrder.flowOn(Dispatchers.IO)

    suspend fun addItemToOrder(item: Item, quantity: Int) {
        val order: CachedOrder = Cache.cachedOrder.first()
        val items = order.items.toMutableMap()

        items.compute(item.id) { _, v ->
            v ?: return@compute quantity
            if (v + quantity == 0) null else v + quantity
        }

        Cache.cacheOrder(order.copy(items = items))
    }

    suspend fun addVoucherToOrder(voucher: Voucher) {
        val order: CachedOrder = Cache.cachedOrder.first()

        if (voucher.type == 'o')  {
            val offerVouchers = order.offerVouchers.toMutableSet()

            if (!offerVouchers.removeIf { id -> id == voucher.id })
                offerVouchers.add(voucher.id)

            Cache.cacheOrder(order.copy(offerVouchers = offerVouchers))
        } else {
            val discountVoucher = if (order.discountVoucher == voucher.id) null else voucher.id
            Cache.cacheOrder(order.copy(discountVoucher = discountVoucher))
        }
    }

    // Utils

    private fun <T> mapToResource(apiResponse: ApiResponse<T>): Resource<T> {
        return when (apiResponse) {
            is ApiResponse.Success -> Resource.success(apiResponse.data)
            is ApiResponse.ApiError -> Resource.error(apiResponse.error)
            is ApiResponse.NetworkError -> Resource.error(apiResponse.error)
        }
    }
}