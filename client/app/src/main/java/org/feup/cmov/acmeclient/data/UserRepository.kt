package org.feup.cmov.acmeclient.data

import android.telecom.Call
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.feup.cmov.acmeclient.data.model.User
import javax.inject.Inject

//interface WebService {
//
//    @GET("/users/{user}")
//    fun getUser(@Path("user") userId : String): Call<User>
//}

class UserRepository @Inject constructor() {
    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
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