package com.weberpackage.blackjack.common.data.repo

import com.weberpackage.blackjack.core.utils.AppUpdateConfigData

interface RemoteConfigRepository {
    suspend fun fetchAppUpdateConfig(): AppUpdateConfigData?

}
