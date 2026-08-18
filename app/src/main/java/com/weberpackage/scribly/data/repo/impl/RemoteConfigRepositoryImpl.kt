package com.weberpackage.scribly.data.repo.impl

import com.weberpackage.scribly.BuildConfig
import com.weberpackage.scribly.common.data.repo.RemoteConfigRepository
import com.weberpackage.scribly.core.remote_config.RemoteConfigManager
import com.weberpackage.scribly.core.utils.AppUpdateConfigData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class RemoteConfigRepositoryImpl @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager
) : RemoteConfigRepository {

    override suspend fun fetchAppUpdateConfig(): AppUpdateConfigData? = suspendCancellableCoroutine { continuation ->
        remoteConfigManager.fetchAndActivate { success ->
            if (success) {
                try {
                    val jsonString = remoteConfigManager.getUpdateConfigJson()
                    val configList = Json.decodeFromString<List<AppUpdateConfigData>>(jsonString)
                    
                    // Filter based on Alpha status and get the one with the highest version code
                    val matchingConfig = configList
                        .filter { it.isAlpha == BuildConfig.ALPHA_BUILD }
                        .maxByOrNull { it.appVersionCode }
                    
                    continuation.resume(matchingConfig)
                } catch (e: Exception) {
                    android.util.Log.e("RemoteConfig", "Error parsing JSON: ${e.message}")
                    continuation.resume(null)
                }
            } else {
                continuation.resume(null)
            }
        }
    }
}
