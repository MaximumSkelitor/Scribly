package com.weberpackage.scribly.common.presentation.contract

import com.weberpackage.scribly.common.presentation.base.ViewEvent
import com.weberpackage.scribly.common.presentation.base.ViewSideEffect
import com.weberpackage.scribly.common.presentation.base.ViewState
import com.weberpackage.scribly.common.presentation.theme.AppTheme

class MainContract {
    sealed class Event : ViewEvent

    data class State(
        val appTheme: AppTheme = AppTheme.SYSTEM,
        val biometricEnabled: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect
}
