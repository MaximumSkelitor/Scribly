package com.weberpackage.scribly

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.weberpackage.scribly.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberpackage.scribly.common.presentation.components.EventAlertDialog
import com.weberpackage.scribly.common.presentation.contract.MainContract
import com.weberpackage.scribly.common.presentation.navigation.RootNavGraph
import com.weberpackage.scribly.common.presentation.state.EventDialogState
import com.weberpackage.scribly.common.presentation.state.rememberEventDialogState
import com.weberpackage.scribly.common.presentation.theme.ScriblyTheme
import com.weberpackage.scribly.common.presentation.utils.DialogController
import com.weberpackage.scribly.common.presentation.utils.ObserveAsEvents
import com.weberpackage.scribly.common.presentation.utils.showAlerter
import com.weberpackage.scribly.common.presentation.viewmodel.MainViewModel
import com.weberpackage.scribly.core.remote_config.RemoteConfigManager
import com.weberpackage.scribly.core.security.SecurityHelper
import com.weberpackage.scribly.core.utils.UpdateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appUpdateManager: UpdateManager

    @Inject
    lateinit var securityHelper: SecurityHelper

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    private val viewModel by viewModels<MainViewModel>()

    private var isAuthenticated by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAndRequestNotificationPermission()

        setContent {
            MainContent()
        }
    }

    @Composable
    private fun MainContent() {
        val state = viewModel.viewState.value
        val effectFlow = viewModel.effect
        val eventDialog = rememberEventDialogState()
        val unlockTitle = stringResource(R.string.unlock_scribly)

        val darkTheme = when (state.appTheme) {
            com.weberpackage.scribly.common.presentation.theme.AppTheme.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            com.weberpackage.scribly.common.presentation.theme.AppTheme.LIGHT -> false
            com.weberpackage.scribly.common.presentation.theme.AppTheme.DARK -> true
        }

        DisposableEffect(darkTheme) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT,
                ) { darkTheme },
                navigationBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT,
                ) { darkTheme }
            )
            onDispose {}
        }

        // Handle initial authentication if needed
        LaunchedEffect(state.biometricEnabled) {
            if (state.biometricEnabled && !isAuthenticated) {
                securityHelper.showBiometricPrompt(
                    activity = this@MainActivity,
                    title = unlockTitle,
                    onSuccess = { isAuthenticated = true },
                    onError = { /* Handle error / exit? */ }
                )
            } else {
                isAuthenticated = true
            }
        }

        ObserveDialogEvents(eventDialog = eventDialog)

        HandleSideEffects(effectFlow = effectFlow)

        LaunchedEffect(Unit) {
            viewModel.setEvent(MainContract.Event.OnCheckForUpdates)
        }

        ScriblyTheme(appTheme = state.appTheme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                EventAlertDialog(eventDialogState = eventDialog)

                if (isAuthenticated) {
                    RootNavGraph(
                        startWithAddScreen = intent?.action == "com.weberpackage.scribly.ACTION_ADD_SUBSCRIPTION"
                    )
                } else {
                    // Blank screen or splash while authenticating
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
                }
            }
        }
    }

    @Composable
    private fun ObserveDialogEvents(eventDialog: EventDialogState) {
        ObserveAsEvents(
            flow = DialogController.events
        ) { event ->
            eventDialog.show(dialogEvent = event)
        }
    }

    @Composable
    private fun HandleSideEffects(
        effectFlow: Flow<MainContract.Effect>,
    ) {
        LaunchedEffect(SIDE_EFFECTS_KEY) {
            effectFlow.onEach { effect ->
                when (effect) {
                    is MainContract.Effect.CheckForAppUpdates -> checkForUpdates()
                    is MainContract.Effect.Notification -> {
                        this@MainActivity.showAlerter(
                            message = effect.text,
                            isError = effect.error
                        )
                    }
                }
            }.collect()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkForUpdates() {
        lifecycleScope.launch {
            appUpdateManager.checkForUpdate(this@MainActivity)
        }
    }
}
