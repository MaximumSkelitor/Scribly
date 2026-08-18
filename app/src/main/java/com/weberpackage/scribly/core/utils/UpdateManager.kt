package com.weberpackage.scribly.core.utils

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.weberpackage.scribly.BuildConfig
import com.weberpackage.scribly.R
import com.weberpackage.scribly.common.data.repo.RemoteConfigRepository
import com.weberpackage.scribly.common.presentation.utils.DialogAction
import com.weberpackage.scribly.common.presentation.utils.DialogController
import com.weberpackage.scribly.common.presentation.utils.DialogEvent
import com.weberpackage.scribly.common.presentation.utils.UiText
import com.weberpackage.scribly.core.constants.Constants
import com.weberpackage.scribly.core.prefs.Pref
import com.weberpackage.scribly.core.prefs.Prefs
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

class UpdateManager @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val prefs: Prefs
) {
    private var currentUpdate: AppUpdateConfigData? = null

    suspend fun checkForUpdate(activity: ComponentActivity) {
        android.util.Log.d("UpdateManager", "Checking for updates...")
        remoteConfigRepository.fetchAppUpdateConfig()?.also { config ->
            android.util.Log.d("UpdateManager", "Config received: version=${config.appVersionCode}, type=${config.type}")
            if (BuildConfig.VERSION_CODE >= config.appVersionCode) {
                android.util.Log.d("UpdateManager", "App is up to date (Current: ${BuildConfig.VERSION_CODE})")
                return@also
            }
            
            currentUpdate = config
            when (config.type) {
                UpdateType.IMMEDIATE -> {
                    DialogController.sendEvent(
                        event = DialogEvent(
                            title = UiText(
                                R.string.inapp_update_available_title
                            ),
                            message = if (config.message.isNotBlank())
                                UiText(config.message) else UiText(
                                R.string.inapp_update_available_message_immediate
                            ),
                            dismissible = false,
                            positiveAction = DialogAction(
                                buttonText = UiText(R.string.update),
                                action = {
                                    activity.lifecycleScope.launch {
                                        openGithubLink(activity = activity)
                                    }
                                }
                            )
                        )
                    )
                }

                UpdateType.FLEXIBLE -> {
                    if (prefs.get(Pref.UpdatePostponeTime) < System.currentTimeMillis()) {
                        DialogController.sendEvent(
                            event = DialogEvent(
                                title = UiText(
                                    R.string.inapp_update_available_title
                                ),
                                message = if (config.message.isNotBlank())
                                    UiText(config.message) else UiText(
                                    R.string.inapp_update_available_message_flexible
                                ),
                                dismissible = false,
                                positiveAction = DialogAction(
                                    buttonText = UiText(R.string.update),
                                    action = {
                                        openGithubLink(activity = activity)
                                    },
                                ),
                                negativeAction = DialogAction(
                                    buttonText = UiText(R.string.postpone),
                                    action = {
                                        postponeUpdate()
                                    }
                                )
                            )
                        )
                    }
                }
            }
        }
    }


    private fun postponeUpdate() {
        val delay = 24 * 60 * 60 * 1000 // 24 hours
        val postponedTime = System.currentTimeMillis() + delay
        prefs.set(Pref.UpdatePostponeTime, postponedTime)
    }

    private fun openGithubLink(activity: Activity) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Constants.GITHUB_RELEASE_LINK.toUri()
            activity.startActivity(this)
            activity.finish()
        }
    }
}

@Serializable
data class AppUpdateConfigData(
    val appVersionCode: Int,
    val type: UpdateType,
    val message: String = "",
    val isAlpha: Boolean = false
)

@Serializable
enum class UpdateType {
    IMMEDIATE, FLEXIBLE
}
