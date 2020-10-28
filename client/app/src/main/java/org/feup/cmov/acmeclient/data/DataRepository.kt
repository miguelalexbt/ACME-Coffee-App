package org.feup.cmov.acmeclient.data

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
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
    suspend fun signIn(
        username: String,
        password: String
    ): ApiResponse<User> {

        // Create request
        val request = SignInRequest(username, password)
        val response = webService.signIn(request)

        // Cache user
        if (response is ApiResponse.Success)
            Cache.cachedUser = Cache.create(response.data)

        return response
    }

    suspend fun signUp(
        name: String,
        nif: String, ccNumber: String, ccExpiration: String, ccCVV: String,
        username: String, password: String
    ): ApiResponse<User> {

        // Generate RSA key pair
        val publicKey = Crypto.generateRSAKeyPair(username)

        // Create request
        val request = SignUpRequest(name, nif, ccNumber, ccExpiration, ccCVV, username, password, publicKey)
        val response = webService.signUp(request)

        // Cache user
        if (response is ApiResponse.Success)
            Cache.cachedUser = Cache.create(response.data)

        return response
    }

    suspend fun fetchItems() {
        while (true) {
            println("Requesting API")
            val response = webService.getItems()

            if (response is ApiResponse.Success) {
                println("Fetched items ${response.data.size} from API")
                itemDao.insertAll(response.data)
            }

            delay(10000)
        }
    }

    fun getItems(): Flow<List<Item>> = itemDao.getAll().distinctUntilChanged()

//    private fun setUser(user: User) {
////        this.user = user
//        Preferences.cachedUser = user
////        userDao.save(user)
//    }
}