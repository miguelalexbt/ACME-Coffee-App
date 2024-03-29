package org.feup.cmov.acmeclient.utils

import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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
                it[USER] =
                    if (user == null) "" else Gson().toJson(CachedUser(user.id, user.username))
            }
        }

        suspend fun cacheOrder(order: CachedOrder?) {
            dataStore.edit {
                it[ORDER] = if (order == null) "" else Gson().toJson(order)
            }
        }
    }
}