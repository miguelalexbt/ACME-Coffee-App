package org.feup.cmov.acmeclient.utils

import android.content.Context
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.data.model.User

class Cache {
    companion object {
        data class CachedUser(val userId: String, val username: String)

        private const val USER_ID = "user_id"
        private const val USERNAME = "username"

        var cachedUser: CachedUser? = null
            get() {
                // Load from memory
                if (field != null)
                    return field

                // Load from cache
                val userId = load(USER_ID)
                val username = load(USERNAME)

                return if (userId != null && username != null)
                    CachedUser(userId, username)
                else
                    null
            }
            set(user) {
                if (user == null) {
                    clear(USER_ID)
                    clear(USERNAME)
                } else {
                    store(USER_ID, user.userId)
                    store(USERNAME, user.username)
                }

                field = user
            }

        fun create(user: User) = CachedUser(user.id, user.username)

        private fun store(key: String, value: String) {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                "app_cache", Context.MODE_PRIVATE
            ) ?: return

            with (sharedPref.edit()) {
                putString(key, value)
                apply()
            }
        }

        private fun load(key: String): String? {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                "app_cache", Context.MODE_PRIVATE
            ) ?: return null

            return sharedPref.getString(key, null)
        }

        private fun clear(key: String) {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                "app_cache", Context.MODE_PRIVATE
            ) ?: return

            with (sharedPref.edit()){
                remove(key)
                apply()
            }
        }
    }
}