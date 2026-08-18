package com.weberpackage.blackjack.core.utils

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.weberpackage.blackjack.R
import com.weberpackage.blackjack.common.data.repo.RemoteConfigRepository
import com.weberpackage.blackjack.common.presentation.utils.DialogAction
import com.weberpackage.blackjack.common.presentation.utils.DialogController
import com.weberpackage.blackjack.common.presentation.utils.DialogEvent
import com.weberpackage.blackjack.common.presentation.utils.UiText
import com.weberpackage.blackjack.core.constants.Constants
import com.weberpackage.blackjack.core.prefs.Pref
import com.weberpackage.blackjack.core.prefs.Prefs
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

class UpdateManager @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val prefs: Prefs
) {
    private var currentUpdate: AppUpdateConfigData? = null

    suspend fun checkForUpdate(activity: ComponentActivity) {
        remoteConfigRepository.fetchAppUpdateConfig()?.also { config ->
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
                    if (prefs.get(Pref.updatePostponeTime) < System.currentTimeMillis()) {
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
        prefs.set(Pref.updatePostponeTime, postponedTime)
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
