package org.feup.cmov.acmeclient.data

import org.feup.cmov.acmeclient.Utils
import org.feup.cmov.acmeclient.data.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import javax.inject.Inject

class UserRepository @Inject constructor(
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
    
    fun signUp(name: String, username: String, password: String) {

        val user = User(
            name = name,
            username = username,
            password = password,
            certificate = Utils.generateCertificate()
        )

        webService.signUp(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    user.id = response.body()!!.id
                }

                println(user.certificate)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                println(t.toString())
            }
        })
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