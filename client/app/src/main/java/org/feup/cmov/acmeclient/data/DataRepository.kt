package org.feup.cmov.acmeclient.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.feup.cmov.acmeclient.utils.Crypto
import org.feup.cmov.acmeclient.data.api.*
import org.feup.cmov.acmeclient.data.db.ItemDao
import org.feup.cmov.acmeclient.data.db.UserDao
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User
import org.feup.cmov.acmeclient.utils.Cache

import javax.inject.Inject

class DataRepository @Inject constructor(
    private val webService: WebService,
    private val userDao: UserDao,
    private val itemDao: ItemDao
) {

    val isLoggedIn = Cache.cachedUser.map { it != null }

    suspend fun signIn(username: String, password: String): Resource<Nothing> {
        val user = userDao.get(username).first()

        if (user != null && Crypto.checkPassword(password, user.password)) {
            Cache.cacheUser(user)
            return Resource.success(null)
        }

        return Resource.error("wrong_credentials", null)
    }

    suspend fun signUp(
        name: String,
        nif: String, ccNumber: String, ccExpiration: String, ccCVV: String,
        username: String, password: String
    ): Resource<*> {

        // Generate RSA key pair
        val publicKey = Crypto.generateRSAKeyPair(username)

        // Create request
        val request = SignUpRequest(name, nif, ccNumber, ccExpiration, ccCVV, publicKey)
        val response = webService.signUp(request)

        if (response is ApiResponse.Success) {
            val id = response.data!!.userId

            // Hash password and create user
            val hashedPassword = Crypto.hashPassword(password)
            val user = User(id, name, nif, ccNumber, ccExpiration, ccCVV, username, hashedPassword)

            userDao.insert(user)
            Cache.cacheUser(user)
        }

        return mapToResource(response)
    }

    // VERSION 1.0
    // TODO wrap in Resource for LOADING / ERROR STATE
    fun getItems(): Flow<List<Item>> = itemDao.getAll()
            .distinctUntilChanged()
            .onStart {
                // Check if menu is updated
                val lastItem = itemDao.getLastAddedItem().first()
                val response = webService.getItems(lastItem?.addedAt)

                // Update menu if needed
                if (response is ApiResponse.Success)
                    itemDao.insertAll(response.data!!)

                val res: Resource<List<Item>> = Resource.loading(null)
            }
//            .map {
//                Resource.success()
//            }.flowPm

    private fun <T> mapToResource(apiResponse: ApiResponse<T>): Resource<T> {
        return when (apiResponse) {
            is ApiResponse.Success -> Resource.success(apiResponse.data)
            is ApiResponse.ApiError -> Resource.error(apiResponse.error, null)
            is ApiResponse.NetworkError -> Resource.error(apiResponse.error, null)
        }
    }
}