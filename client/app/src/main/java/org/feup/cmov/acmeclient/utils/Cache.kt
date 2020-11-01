package org.feup.cmov.acmeclient.utils

import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
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

        val cachedUser: Flow<CachedUser?> = dataStore.data
            .catch { throw it }
            .map {
                val userId = it[USER_ID] ?: ""
                val username = it[USERNAME] ?: ""

                if (userId == "" || username == "")
                    return@map null

                CachedUser(userId, username)
            }

        suspend fun cacheUser(user: User?) {
            dataStore.edit {
                it[USER_ID] = user?.id ?: ""
                it[USERNAME] = user?.username ?: ""
            }
        }
    }
}