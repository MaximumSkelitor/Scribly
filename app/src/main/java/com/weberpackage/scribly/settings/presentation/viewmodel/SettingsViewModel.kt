package com.weberpackage.scribly.settings.presentation.viewmodel

import android.text.format.DateFormat
import androidx.lifecycle.viewModelScope
import com.weberpackage.scribly.BuildConfig
import com.weberpackage.scribly.R
import com.weberpackage.scribly.common.presentation.base.BaseViewModel
import com.weberpackage.scribly.common.presentation.theme.AppTheme
import com.weberpackage.scribly.common.presentation.utils.DialogAction
import com.weberpackage.scribly.common.presentation.utils.DialogController
import com.weberpackage.scribly.common.presentation.utils.DialogEvent
import com.weberpackage.scribly.common.presentation.utils.UiText
import com.weberpackage.scribly.common.presentation.utils.uiTextArgsOf
import com.weberpackage.scribly.core.data.DataHelper
import com.weberpackage.scribly.core.prefs.Pref
import com.weberpackage.scribly.core.prefs.Prefs
import com.weberpackage.scribly.core.security.SecurityHelper
import com.weberpackage.scribly.settings.presentation.contract.SettingsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: Prefs,
    private val securityHelper: SecurityHelper,
    private val dataHelper: DataHelper
) : BaseViewModel<SettingsContract.Event, SettingsContract.State, SettingsContract.Effect>() {

    init {
        initializeInfo()
        observePrefs()
        setState { copy(canUseBiometrics = securityHelper.canAuthenticate()) }
    }

    override fun setInitialState() = SettingsContract.State()

    override fun handleEvents(event: SettingsContract.Event) {
        when (event) {
            SettingsContract.Event.OnVersionClick -> showAboutDialog()
            SettingsContract.Event.OnThemeClick -> setEffect { SettingsContract.Effect.ShowThemeSelector }
            SettingsContract.Event.OnBiometricClick -> setEffect { SettingsContract.Effect.ShowBiometricSelector }
            is SettingsContract.Event.OnThemeChange -> updateTheme(event.theme)
            is SettingsContract.Event.OnRemindersToggle -> updateReminders(event.enabled)
            is SettingsContract.Event.OnBiometricToggle -> updateBiometric(event.enabled)
            SettingsContract.Event.OnExportClick -> setEffect { SettingsContract.Effect.TriggerExport }
            SettingsContract.Event.OnImportClick -> setEffect { SettingsContract.Effect.TriggerImport }
            is SettingsContract.Event.OnExportResult -> event.uri?.let { exportData(it) }
            is SettingsContract.Event.OnImportResult -> event.uri?.let { importData(it) }
        }
    }

    private fun observePrefs() {
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.AppTheme).collect { themeName ->
                setState { copy(currentTheme = AppTheme.fromName(themeName)) }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.RenewalRemindersEnabled).collect { enabled ->
                setState { copy(remindersEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.BiometricLockEnabled).collect { enabled ->
                setState { copy(biometricEnabled = enabled) }
            }
        }
    }

    private fun exportData(uri: android.net.Uri) {
        viewModelScope.launch {
            dataHelper.exportData(uri)
                .onSuccess {
                    setEffect { SettingsContract.Effect.ShowMessage(UiText(R.string.backup_saved_success)) }
                }
                .onFailure {
                    setEffect { SettingsContract.Effect.ShowMessage(UiText(R.string.failed_to_save_backup, uiTextArgsOf(it.message ?: R.string.data_error_unknown)), isError = true) }
                }
        }
    }

    private fun importData(uri: android.net.Uri) {
        viewModelScope.launch {
            dataHelper.importData(uri)
                .onSuccess {
                    setEffect { SettingsContract.Effect.ShowMessage(UiText(R.string.subscriptions_restored_success)) }
                }
                .onFailure {
                    setEffect { SettingsContract.Effect.ShowMessage(UiText(R.string.failed_to_restore_data, uiTextArgsOf(it.message ?: R.string.data_error_unknown)), isError = true) }
                }
        }
    }

    private fun updateTheme(theme: AppTheme) {
        prefs.set(Pref.AppTheme, theme.name)
    }

    private fun updateReminders(enabled: Boolean) {
        prefs.set(Pref.RenewalRemindersEnabled, enabled)
    }

    private fun updateBiometric(enabled: Boolean) {
        prefs.set(Pref.BiometricLockEnabled, enabled)
    }

    private fun showAboutDialog() {
        viewModelScope.launch {
            DialogController.sendEvent(
                DialogEvent(
                    title = UiText(R.string.dialog_app_info_title),
                    message = UiText(
                        R.string.dialog_app_info_message,
                        uiTextArgsOf(
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE.toString(),
                            BuildConfig.BUILD_TYPE,
                            viewState.value.buildDate
                        )
                    ),
                    positiveAction = DialogAction(
                        buttonText = UiText(R.string.close),
                        action = { }
                    )
                )
            )
        }
    }

    private fun initializeInfo() {
        val dateStr = try {
            DateFormat.format("MM-dd-yy HH:mm", BuildConfig.BUILD_TIME.toLong()).toString()
        } catch (e: Exception) {
            "Unknown" // BuildConfig time is usually present, keeping as fallback for now
        }

        setState {
            copy(
                appVersion = BuildConfig.VERSION_NAME,
                appVersionCode = BuildConfig.VERSION_CODE.toString(),
                buildType = BuildConfig.BUILD_TYPE,
                buildDate = dateStr
            )
        }
    }
}
