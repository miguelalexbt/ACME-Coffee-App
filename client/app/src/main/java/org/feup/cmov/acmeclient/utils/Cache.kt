package org.feup.cmov.acmeclient.utils

import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.feup.cmov.acmeclient.MainApplication
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

                if (userId == "" || username == "")
                    return@map null

                CachedUser(userId, username)
            }

        val cachedOrder: Flow<Map<String, Int>> = dataStore.data
            .catch { throw it }
            .map {
                orderToMap(it[ORDER])
            }

        suspend fun cacheUser(user: User?) {
            dataStore.edit {
                it[USER_ID] = user?.id ?: ""
                it[USERNAME] = user?.username ?: ""
            }
        }

        suspend fun cacheOrder(key: String, value: Int) {
            dataStore.edit {
                val order = orderToMap(it[ORDER]).toMutableMap()
                    .apply {
                        compute(key) { _, v -> if (v == null) value else null }
                    }

                it[ORDER] = orderToString(order)

                println("ORDER ${it[ORDER]}")
            }
        }

        private fun orderToMap(order: String?): Map<String, Int> {
            if (order.isNullOrEmpty()) return emptyMap()

            val mapType = object: TypeToken<Map<String, Int>>(){}.type
            return Gson().fromJson(order, mapType)
        }

        private fun orderToString(order: Map<String, Int>): String {
            return Gson().toJson(order)
        }
    }
}