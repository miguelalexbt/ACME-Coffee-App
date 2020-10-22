package org.feup.cmov.acmeclient.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.feup.cmov.acmeclient.Utils
import org.feup.cmov.acmeclient.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import javax.inject.Inject
import kotlin.Result

class DataRepository @Inject constructor(
    private val webService: WebService
) {
    
    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

//    fun signIn(username: String, password: String) {
//        webService.signIn(username, password).enqueue(object: Callback<User> {
//            override fun onResponse(call: Call<User>, response: Response<User>) {
//                if (response.isSuccessful) {
//                    setUser(response.body()!!)
//                }
//            }
//
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                println(t.toString())
//            }
//        })
//    }

    fun signOut() {

    }

    suspend fun signUp(name: String, username: String, password: String): Result<User> {

        val result = runCatching {

            // Generate certificate
            val cert = Utils.generateCertificate()

            // Call web service
            webService.signUp(name, username, password, cert)
        }

        result.onSuccess {
            // Update state (loggedin user)
        }

        return result
    }
    
//    suspend fun signUp(name: String, username: String, password: String): Result<LiveData<User>> {
//
//         val result = MutableLiveData<User>()
//
//        // Generate certificate
//        val cert = Utils.generateCertificate()
//
//        webService.signUp(name, username, password, cert).enqueue(object : Callback<User> {
//            override fun onResponse(call: Call<User>, response: Response<User>) {
//                if (response.isSuccessful) {
//                    setUser(response.body()!!)
//                }
//            }
//
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                println(t.toString())
//            }
//        })
//    }

    private fun setUser(user: User) {
        this.user = user

        // CACHE
    }

//    fun login(username : String, password : String): LiveData<User> {
//        // This isn't an optimal implementation. We'll fix it later.
//        val data = MutableLiveData<User>()
//        webservice.getUser(userId).enqueue(object : Callback<User> {
//            override fun onResponse(call: Call<User>, response: Response<User>) {
//                data.value = response.body()
//            }
//            // Error case is left out for brevity.
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                TODO()
//            }
//        })
//        return data
//    }

//    fun login(username: String, password: String): Result<User> {
//        val result = null
//
////        return result
//    }

//    fun logout() {
//        user = null
//    }
//
//    private fun setUser(user: User) {
//        this.user = user
//        // If user credentials will be cached in local storage, it is recommended it be encrypted
//        // @see https://developer.android.com/training/articles/keystore
//    }
}