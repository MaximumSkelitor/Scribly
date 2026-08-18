package com.weberpackage.scribly

import android.Manifest
import android.content.Intent
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.weberpackage.scribly.common.presentation.navigation.RootNavGraph
import com.weberpackage.scribly.common.presentation.theme.ScriblyTheme
import com.weberpackage.scribly.common.presentation.viewmodel.MainViewModel
import com.weberpackage.scribly.core.remote_config.RemoteConfigManager
import com.weberpackage.scribly.core.security.SecurityHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
            val state = viewModel.viewState.value
            val unlockTitle = stringResource(R.string.unlock_scribly)

            var isUpdateRequired by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                remoteConfigManager.fetchAndActivate {
                    if (remoteConfigManager.isUpdateRequired()) {
                        isUpdateRequired = true
                    }
                }
            }

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

            ScriblyTheme(appTheme = state.appTheme) {
                if (isUpdateRequired) {
                    ForceUpdateDialog()
                } else if (isAuthenticated) {
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
    private fun ForceUpdateDialog() {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.update_required_title)) },
            text = { Text(stringResource(R.string.update_required_msg)) },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("market://details?id=$packageName")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                }) {
                    Text(stringResource(R.string.update_now))
                }
            },
            dismissButton = null,
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
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
}
