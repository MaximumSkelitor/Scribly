package com.weberpackage.scribly.common.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.weberpackage.scribly.common.presentation.base.BaseViewModel
import com.weberpackage.scribly.common.presentation.contract.MainContract
import com.weberpackage.scribly.common.presentation.theme.AppTheme
import com.weberpackage.scribly.core.prefs.Pref
import com.weberpackage.scribly.core.prefs.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefs: Prefs
) : BaseViewModel<MainContract.Event, MainContract.State, MainContract.Effect>() {

    init {
        observeTheme()
        observeBiometric()
    }

    override fun setInitialState() = MainContract.State()

    override fun handleEvents(event: MainContract.Event) {
        when (event) {
            MainContract.Event.OnCheckForUpdates -> {
                setEffect { MainContract.Effect.CheckForAppUpdates }
            }
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.AppTheme).collect { themeName ->
                setState {
                    copy(appTheme = AppTheme.fromName(themeName))
                }
            }
        }
    }

    private fun observeBiometric() {
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.BiometricLockEnabled).collect { enabled ->
                setState {
                    copy(biometricEnabled = enabled)
                }
            }
        }
    }
}
