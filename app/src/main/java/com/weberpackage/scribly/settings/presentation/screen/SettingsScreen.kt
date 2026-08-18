package com.weberpackage.scribly.settings.presentation.screen

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.weberpackage.scribly.R
import com.weberpackage.scribly.common.presentation.components.EventAlertDialog
import com.weberpackage.scribly.common.presentation.state.rememberEventDialogState
import com.weberpackage.scribly.common.presentation.theme.AppTheme
import com.weberpackage.scribly.common.presentation.utils.DialogController
import com.weberpackage.scribly.common.presentation.utils.ObserveAsEvents
import com.weberpackage.scribly.common.presentation.utils.UiText
import com.weberpackage.scribly.common.presentation.utils.showAlerter
import com.weberpackage.scribly.settings.presentation.components.BiometricSelectorSheet
import com.weberpackage.scribly.settings.presentation.components.SettingsOption
import com.weberpackage.scribly.settings.presentation.components.ThemeSelectorSheet
import com.weberpackage.scribly.settings.presentation.contract.SettingsContract
import com.weberpackage.scribly.settings.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    contentPadding: PaddingValues,
    onNavigateToNotificationCenter: () -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.viewState.value
    val eventDialog = rememberEventDialogState()
    var showThemeSheet by remember { mutableStateOf(false) }
    var showBiometricSheet by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri -> viewModel.setEvent(SettingsContract.Event.OnExportResult(uri)) }
    )

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> viewModel.setEvent(SettingsContract.Event.OnImportResult(uri)) }
    )

    ObserveAsEvents(flow = DialogController.events) { event ->
        eventDialog.show(dialogEvent = event)
    }

    ObserveAsEvents(flow = viewModel.effect) { effect ->
        when (effect) {
            SettingsContract.Effect.ShowThemeSelector -> showThemeSheet = true
            SettingsContract.Effect.ShowBiometricSelector -> showBiometricSheet = true
            SettingsContract.Effect.TriggerExport -> exportLauncher.launch("scribly_backup.json")
            SettingsContract.Effect.TriggerImport -> importLauncher.launch(arrayOf("application/json"))
            is SettingsContract.Effect.ShowMessage -> {
                (context as? Activity)?.showAlerter(
                    message = effect.uiText,
                    isError = effect.isError
                )
            }
            else -> {}
        }
    }

    EventAlertDialog(eventDialogState = eventDialog)

    if (showThemeSheet) {
        ThemeSelectorSheet(
            currentTheme = state.currentTheme,
            onThemeSelected = {
                viewModel.setEvent(SettingsContract.Event.OnThemeChange(it))
            },
            onDismiss = { showThemeSheet = false }
        )
    }

    if (showBiometricSheet) {
        BiometricSelectorSheet(
            isEnabled = state.biometricEnabled,
            onEnabledSelected = {
                viewModel.setEvent(SettingsContract.Event.OnBiometricToggle(it))
            },
            onDismiss = { showBiometricSheet = false }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SettingsSection(title = stringResource(R.string.appearance)) {
                SettingsOption(
                    title = R.string.theme,
                    desc = R.string.appearance_desc,
                    description = when (state.currentTheme) {
                        AppTheme.SYSTEM -> stringResource(R.string.system)
                        AppTheme.LIGHT -> stringResource(R.string.light)
                        AppTheme.DARK -> stringResource(R.string.dark)
                    },
                    onClick = {
                        viewModel.setEvent(SettingsContract.Event.OnThemeClick)
                    },
                    icon = Icons.Default.Palette
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.security)) {
                SettingsOption(
                    title = R.string.biometric_lock,
                    desc = R.string.biometric_lock_desc,
                    description = if (state.biometricEnabled) stringResource(R.string.on) else stringResource(R.string.off),
                    onClick = {
                        if (state.canUseBiometrics) {
                            viewModel.setEvent(SettingsContract.Event.OnBiometricClick)
                        }
                    },
                    icon = Icons.Default.Fingerprint
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.notifications)) {
                SettingsOption(
                    title = R.string.renewal_reminders,
                    desc = R.string.notifications_desc,
                    description = "",
                    onClick = onNavigateToNotificationCenter,
                    icon = Icons.Default.Notifications
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.data_management)) {
                SettingsOption(
                    title = R.string.export_backup,
                    desc = R.string.export_backup_desc,
                    description = "",
                    onClick = { viewModel.setEvent(SettingsContract.Event.OnExportClick) },
                    icon = Icons.Default.Save
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsOption(
                    title = R.string.import_backup,
                    desc = R.string.import_backup_desc,
                    description = "",
                    onClick = { viewModel.setEvent(SettingsContract.Event.OnImportClick) },
                    icon = Icons.Default.Restore
                )
            }
        }

        item {
            SettingsSection(title = stringResource(R.string.about)) {
                SettingsOption(
                    title = R.string.scribly_version_title,
                    desc = R.string.about_desc,
                    description = state.appVersion,
                    onClick = { viewModel.setEvent(SettingsContract.Event.OnVersionClick) },
                    icon = Icons.Default.Info
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
        )
        content()
    }
}
