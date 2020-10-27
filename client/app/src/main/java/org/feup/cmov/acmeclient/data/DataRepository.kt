package org.feup.cmov.acmeclient.data

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.Utils
import org.feup.cmov.acmeclient.data.api.*
import org.feup.cmov.acmeclient.data.db.ItemDao
import org.feup.cmov.acmeclient.data.db.UserDao
import org.feup.cmov.acmeclient.data.model.Item
import org.feup.cmov.acmeclient.data.model.User

import javax.inject.Inject

class DataRepository @Inject constructor(
    private val webService: WebService,
    private val userDao: UserDao,
    private val itemDao: ItemDao
) {
//    var user: User? = null
//        private set
//
//    val isLoggedIn: Boolean
//        get() = user != null

//    init {
////        user = loadFromCache()
//        user = null
//    }

    suspend fun signIn(
        username: String,
        password: String
    ): ApiResponse<User> {

        // Create request
        val request = SignInRequest(username, password)
        val response = webService.signIn(request)

        if (response is ApiResponse.Success) {
            // Cache user
        }

        return response
    }

    suspend fun signUp(
        name: String,
        nif: String, ccNumber: String, ccExpiration: String, ccCVV: String,
        username: String, password: String
    ): ApiResponse<User> {

        // Generate certificate
        val certificate = Utils.generateCertificate(username)

        // Create request
        val request = SignUpRequest(name, nif, ccNumber, ccExpiration, ccCVV, username, password, certificate)
        val response = webService.signUp(request)

        if (response is ApiResponse.Success) {
            // Cache user
        }

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

    // TODO
//    suspend fun signUp(name: String, username: String, password: String): User {
//
//        val result = runCatching {
//            val user = User(id="", name=name, username=username, password=password)
//
//            // Generate certificate
//            val cert = Utils.generateCertificate(alias=username)
//
//            // Call web service
//           webService.signUp(SignUpRequest(user, cert))
//        }
//
//        result.onSuccess {
//            println("Success! Saving to DB")
//            setUser(result.getOrNull()!!)
//        }
//
//        return result.getOrNull()!!
//    }

//    suspend fun fetchItems() {
//        println("fetchItems")
//        val result = runCatching {
//            webService.getItems()
//        }
//        result.onFailure {
//            println(result.exceptionOrNull())
//            println("onFailure")
//        }
//        result.onSuccess {
//            println(result)
//            itemDao.save(result.getOrNull()!!)
//            println("fetchItems onSuccess")
//        }
//    }
//
//    fun getItemsCached(): Flow<List<Item>> = itemDao.getAll()

//    private suspend fun setUser(user: User) {
//        this.user = user
////        saveToCache(user)
//        userDao.save(user)
//    }

    private fun saveToCache(user: User) {
        val sharedPref = MainApplication.instance.getSharedPreferences(
            "APP_PREFS", Context.MODE_PRIVATE
        ) ?: return

        with (sharedPref.edit()) {
            putString("loggedIn", Gson().toJson(user))
            apply()
        }
    }

    private fun loadFromCache(): User? {
        val sharedPref = MainApplication.instance.getSharedPreferences(
            "APP_PREFS", Context.MODE_PRIVATE
        ) ?: return null

        with (sharedPref.edit()){
            clear()
            apply()
        }

        val user = sharedPref.getString("loggedIn", null) ?: return null

        return Gson().fromJson(user, User::class.java)
    }
}