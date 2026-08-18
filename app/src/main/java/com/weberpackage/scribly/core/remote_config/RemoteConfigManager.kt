package com.weberpackage.scribly.core.remote_config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.weberpackage.scribly.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor() {

    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(
            UPDATE_CONFIG_KEY to "[]"
        ))
    }

    fun fetchAndActivate(onComplete: (Boolean) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getUpdateConfigJson(): String = remoteConfig.getString(UPDATE_CONFIG_KEY)

    companion object {
        const val UPDATE_CONFIG_KEY = "app_update_config"
    }
}
