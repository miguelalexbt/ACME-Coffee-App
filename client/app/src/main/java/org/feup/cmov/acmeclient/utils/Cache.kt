package org.feup.cmov.acmeclient.utils

import android.content.Context
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.data.model.User

class Cache {
    data class CachedUser(val userId: String, val username: String)

    companion object {
        private const val CACHE = "app_cache"
        private const val USER_ID = "user_id"
        private const val USERNAME = "username"

        var cachedUser: CachedUser? = null
            get() {
                val userId = load(USER_ID)
                val username = load(USERNAME)

                return if (userId != null && username != null)
                    CachedUser(userId, username)
                else
                    null
            }
            private set(user) {
                if (user == null) {
                    clear(USER_ID)
                    clear(USERNAME)
                } else {
                    store(USER_ID, user.userId)
                    store(USERNAME, user.username)
                }

                field = user
            }

        fun cacheUser(user: User) {
            cachedUser = CachedUser(user.id, user.username)
        }

        private fun store(key: String, value: String) {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                CACHE, Context.MODE_PRIVATE
            ) ?: return

            with(sharedPref.edit()) {
                putString(key, value)
                apply()
            }
        }

        private fun load(key: String): String? {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                CACHE, Context.MODE_PRIVATE
            ) ?: return null

            return sharedPref.getString(key, null)
        }

        private fun clear(key: String) {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                CACHE, Context.MODE_PRIVATE
            ) ?: return

            with(sharedPref.edit()) {
                remove(key)
                apply()
            }
        }
    }
}