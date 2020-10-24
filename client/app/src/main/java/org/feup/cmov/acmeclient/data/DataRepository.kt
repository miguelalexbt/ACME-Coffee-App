package org.feup.cmov.acmeclient.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.Utils
import org.feup.cmov.acmeclient.data.db.ItemDao
import org.feup.cmov.acmeclient.data.db.UserDao
import org.feup.cmov.acmeclient.data.model.User
import org.feup.cmov.acmeclient.data.request.SignInRequest
import org.feup.cmov.acmeclient.data.request.SignUpRequest
import retrofit2.Response
import java.io.IOException
import java.security.KeyStore
import java.util.concurrent.Executor

import javax.inject.Inject

class DataRepository @Inject constructor(
    private val webService: WebService,
    private val userDao: UserDao,
    private val itemDao: ItemDao
) {
    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
//        user = loadFromCache()
        user = null
    }

    suspend fun signIn(username: String, password: String): User {

        val result = kotlin.runCatching {
            // Call web service
            webService.signIn(SignInRequest(username, password))
        }

        result.onSuccess {
            println("Success! Logged in")
//            setUser(result.getOrNull()!!)
        }

        return result.getOrNull()!!
    }

    suspend fun signUp(name: String, username: String, password: String): User {

        val result = runCatching {
            val user = User(id="", name=name, username=username, password=password)

            // Generate certificate
            val cert = Utils.generateCertificate(alias=username)

            // Call web service
           webService.signUp(SignUpRequest(user, cert))
        }

        result.onSuccess {
            println("Success! Saving to DB")
            setUser(result.getOrNull()!!)
        }

        return result.getOrNull()!!
    }

    fun getItems() = itemDao.getAll()


    private suspend fun setUser(user: User) {
        this.user = user
//        saveToCache(user)
        userDao.save(user)
    }

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