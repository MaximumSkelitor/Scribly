package com.weberpackage.scribly.core.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Prefs @Inject constructor(
    @ApplicationContext appContext: Context
) {

    private var sharedPrefs: SharedPreferences = appContext.getSharedPreferences(
        "scribly_prefs",
        Context.MODE_PRIVATE
    )

    fun <T> collectPrefsFlow(pref: Pref<T>): Flow<T> {
        return sharedPrefs.prefsFlow(pref)
    }

    fun <T> get(pref: Pref<T>): T {
        @Suppress("UNCHECKED_CAST")
        return when (val defValue = pref.defaultValue) {
            is Boolean -> sharedPrefs.getBoolean(pref.key, defValue) as T
            is Int -> sharedPrefs.getInt(pref.key, defValue) as T
            is Long -> sharedPrefs.getLong(pref.key, defValue) as T
            is String -> sharedPrefs.getString(pref.key, defValue) as T
            else -> defValue
        }
    }

    fun <T> set(pref: Pref<T>, value: T?) {
        sharedPrefs.edit {
            when (value) {
                is Boolean -> putBoolean(pref.key, value)
                is Int -> putInt(pref.key, value)
                is Long -> putLong(pref.key, value)
                is String -> putString(pref.key, value)
            }
        }
    }

    private fun <T> SharedPreferences.prefsFlow(pref: Pref<T>): Flow<T> =
        callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
                if (k == pref.key) {
                    trySend(get(pref))
                }
            }
            registerOnSharedPreferenceChangeListener(listener)
            awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
        }.onStart {
            emit(get(pref))
        }
}
