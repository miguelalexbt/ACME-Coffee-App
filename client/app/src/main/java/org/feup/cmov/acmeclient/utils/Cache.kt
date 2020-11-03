package org.feup.cmov.acmeclient.utils

import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.data.model.Order
import org.feup.cmov.acmeclient.data.model.User

class Cache {
    data class CachedUser(val userId: String, val username: String)

    companion object {
        private val dataStore = MainApplication.context.createDataStore(name = CACHE_NAME)

        private val USER_ID = preferencesKey<String>("user_id")
        private val USERNAME = preferencesKey<String>("username")
        private val ORDER = preferencesKey<String>("order")

        val cachedUser: Flow<CachedUser?> = dataStore.data
            .catch { throw it }
            .map {
                val userId = it[USER_ID] ?: ""
                val username = it[USERNAME] ?: ""

                if (userId == "" || username == "") return@map null

                CachedUser(userId, username)
            }

        val cachedOrder: Flow<Order> = dataStore.data
            .catch { throw it }
            .map {
                val order = it[ORDER] ?: ""

                if (order == "") return@map Order()

                Gson().fromJson(it[ORDER], Order::class.java)
            }

        suspend fun cacheUser(user: User?) {
            dataStore.edit {
                it[USER_ID] = user?.id ?: ""
                it[USERNAME] = user?.username ?: ""
            }
        }

        suspend fun cacheOrder(order: Order) {
            dataStore.edit {
                it[ORDER] = Gson().toJson(order)
            }
        }
    }
}