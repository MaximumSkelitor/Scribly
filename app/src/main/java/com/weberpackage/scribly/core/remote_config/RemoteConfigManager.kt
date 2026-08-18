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
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600 // 1 hour for prod
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(
            MIN_VERSION_KEY to 1,
            FORCE_UPDATE_ENABLED_KEY to false
        ))
    }

    fun fetchAndActivate(onComplete: (Boolean) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getMinVersion(): Int = remoteConfig.getLong(MIN_VERSION_KEY).toInt()

    fun isForceUpdateEnabled(): Boolean = remoteConfig.getBoolean(FORCE_UPDATE_ENABLED_KEY)

    fun isUpdateRequired(): Boolean {
        val currentVersion = BuildConfig.VERSION_CODE
        val minVersion = getMinVersion()
        return isForceUpdateEnabled() && currentVersion < minVersion
    }

    companion object {
        private const val MIN_VERSION_KEY = "min_version"
        private const val FORCE_UPDATE_ENABLED_KEY = "force_update_enabled"
    }
}
