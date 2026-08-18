package com.weberpackage.scribly.common.data.repo

import com.weberpackage.scribly.core.utils.AppUpdateConfigData

interface RemoteConfigRepository {
    suspend fun fetchAppUpdateConfig(): AppUpdateConfigData?

}
