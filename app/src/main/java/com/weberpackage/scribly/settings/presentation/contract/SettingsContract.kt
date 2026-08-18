package com.weberpackage.scribly.settings.presentation.contract

import com.weberpackage.scribly.common.presentation.base.ViewEvent
import com.weberpackage.scribly.common.presentation.base.ViewSideEffect
import com.weberpackage.scribly.common.presentation.base.ViewState
import com.weberpackage.scribly.common.presentation.theme.AppTheme
import com.weberpackage.scribly.common.presentation.utils.UiText

class SettingsContract {
    sealed class Event : ViewEvent {
        data object OnVersionClick : Event()
        data object OnThemeClick : Event()
        data object OnBiometricClick : Event()
        data object OnExportClick : Event()
        data object OnImportClick : Event()
        data class OnThemeChange(val theme: AppTheme) : Event()
        data class OnRemindersToggle(val enabled: Boolean) : Event()
        data class OnBiometricToggle(val enabled: Boolean) : Event()
        data class OnExportResult(val uri: android.net.Uri?) : Event()
        data class OnImportResult(val uri: android.net.Uri?) : Event()
    }

    data class State(
        val appVersion: String = "1.0.0 (Rough Draft)",
        val appVersionCode: String = "",
        val buildType: String = "",
        val buildDate: String = "",
        val currentTheme: AppTheme = AppTheme.SYSTEM,
        val remindersEnabled: Boolean = true,
        val biometricEnabled: Boolean = false,
        val canUseBiometrics: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object ShowAppInfoDialog : Effect()
        data object ShowThemeSelector : Effect()
        data object ShowBiometricSelector : Effect()
        data object TriggerExport : Effect()
        data object TriggerImport : Effect()
        data class ShowMessage(val uiText: UiText, val isError: Boolean = false) : Effect()
    }
}
