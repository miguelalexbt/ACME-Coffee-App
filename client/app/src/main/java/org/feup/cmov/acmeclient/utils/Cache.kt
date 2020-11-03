package org.feup.cmov.acmeclient.utils

import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.data.cache.CachedOrder
import org.feup.cmov.acmeclient.data.cache.CachedUser
import org.feup.cmov.acmeclient.data.model.User

class Cache {
    companion object {
        private val dataStore = MainApplication.context.createDataStore(name = CACHE_NAME)

        private val USER = preferencesKey<String>("user")
        private val ORDER = preferencesKey<String>("order")

        val cachedUser: Flow<CachedUser?> = dataStore.data
            .catch { throw it }
            .map {
                val user = it[USER] ?: ""

                if (user == "")
                    return@map null
                else
                    Gson().fromJson(user, CachedUser::class.java)
            }

        val cachedOrder: Flow<CachedOrder> = dataStore.data
            .catch { throw it }
            .map {
                val order = it[ORDER] ?: ""

                if (order == "")
                    return@map CachedOrder()
                else
                    Gson().fromJson(order, CachedOrder::class.java)
            }

        suspend fun cacheUser(user: User?) {
            dataStore.edit {
                val cache = if (user == null) null else CachedUser(user.id, user.username)
                it[USER] = Gson().toJson(cache ?: "")
            }
        }

        suspend fun cacheOrder(order: CachedOrder) {
            dataStore.edit {
                it[ORDER] = Gson().toJson(order)
            }
        }
    }
}