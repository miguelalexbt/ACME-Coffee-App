package org.feup.cmov.acmeclient.utils

import android.content.Context
import com.google.gson.Gson
import org.feup.cmov.acmeclient.MainApplication
import org.feup.cmov.acmeclient.data.model.User

class Preferences {
    companion object {
        private const val PREFS = "AppPrefs"
        private const val CACHED_USER = "CachedUser"

        var cachedUser: User?
            get() {
                val cached = load(CACHED_USER) ?: return null
                return Gson().fromJson(cached, User::class.java)
            }
            set(user) {
                if (user == null) clear(CACHED_USER) else store(CACHED_USER, Gson().toJson(user))
            }

        private fun store(key: String, value: String) {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE
            ) ?: return

            with (sharedPref.edit()) {
                putString(key, value)
                apply()
            }
        }

        private fun load(key: String): String? {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE
            ) ?: return null

            return sharedPref.getString(key, null)
        }

        private fun clear(key: String) {
            val sharedPref = MainApplication.instance.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE
            ) ?: return

            with (sharedPref.edit()){
                remove(key)
                apply()
            }
        }

    }
}