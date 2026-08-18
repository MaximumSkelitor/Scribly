package com.weberpackage.scribly.data.repo.impl

import com.weberpackage.scribly.common.data.repo.RemoteConfigRepository
import com.weberpackage.scribly.core.remote_config.RemoteConfigManager
import com.weberpackage.scribly.core.utils.AppUpdateConfigData
import com.weberpackage.scribly.core.utils.UpdateType
import kotlinx.coroutines.suspendCancellableCoroutine
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
                val config = AppUpdateConfigData(
                    appVersionCode = remoteConfigManager.getMinVersion(),
                    type = if (remoteConfigManager.isForceUpdateEnabled()) UpdateType.IMMEDIATE else UpdateType.FLEXIBLE
                )
                continuation.resume(config)
            } else {
                continuation.resume(null)
            }
        }
    }
}
