package com.weberpackage.scribly.notification_center.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weberpackage.scribly.R
import com.weberpackage.scribly.common.presentation.components.ScriblyCard
import com.weberpackage.scribly.common.presentation.utils.ObserveAsEvents
import com.weberpackage.scribly.notification_center.presentation.contract.NotificationCenterContract
import com.weberpackage.scribly.notification_center.presentation.viewmodel.NotificationCenterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    viewModel: NotificationCenterViewModel,
    onBack: () -> Unit
) {
    val state = viewModel.viewState.value

    ObserveAsEvents(flow = viewModel.effect) { effect ->
        when (effect) {
            NotificationCenterContract.Effect.NavigateBack -> onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(NotificationCenterContract.Event.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.notification_center_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.notification_center_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            item {
                NotificationSection(title = stringResource(R.string.payment_reminders)) {
                    NotificationItem(
                        icon = Icons.Default.Payments,
                        title = stringResource(R.string.upcoming_payments),
                        desc = stringResource(R.string.upcoming_payments_desc),
                        checked = state.upcomingPaymentsEnabled,
                        onCheckedChange = { viewModel.setEvent(NotificationCenterContract.Event.OnUpcomingPaymentsToggle(it)) }
                    )
                    Spacer(Modifier.height(16.dp))
                    NotificationItem(
                        icon = Icons.Default.DateRange,
                        title = stringResource(R.string.subscription_ending),
                        desc = stringResource(R.string.subscription_ending_desc),
                        checked = state.subscriptionEndingEnabled,
                        onCheckedChange = { viewModel.setEvent(NotificationCenterContract.Event.OnSubscriptionEndingToggle(it)) }
                    )
                    Spacer(Modifier.height(16.dp))
                    NotificationItem(
                        icon = Icons.Default.Alarm,
                        title = stringResource(R.string.free_trial_ending),
                        desc = stringResource(R.string.free_trial_ending_desc),
                        checked = state.freeTrialEndingEnabled,
                        onCheckedChange = { viewModel.setEvent(NotificationCenterContract.Event.OnFreeTrialEndingToggle(it)) }
                    )
                    Spacer(Modifier.height(16.dp))
                    NotificationItem(
                        icon = Icons.Default.Alarm,
                        title = stringResource(R.string.free_trial_ended),
                        desc = stringResource(R.string.free_trial_ended_desc),
                        checked = state.freeTrialEndedEnabled,
                        onCheckedChange = { viewModel.setEvent(NotificationCenterContract.Event.OnFreeTrialEndedToggle(it)) }
                    )
                }
            }

            item {
                NotificationSection(title = stringResource(R.string.insights_reports)) {
                    NotificationItem(
                        icon = Icons.Default.Assignment,
                        title = stringResource(R.string.weekly_digest),
                        desc = stringResource(R.string.weekly_digest_desc),
                        checked = state.weeklyDigestEnabled,
                        onCheckedChange = { viewModel.setEvent(NotificationCenterContract.Event.OnWeeklyDigestToggle(it)) }
                    )
                }
            }

            item {
                NotificationSection(title = stringResource(R.string.smart_integrations)) {
                    NotificationItem(
                        icon = Icons.Default.CalendarMonth,
                        title = stringResource(R.string.sync_to_calendar),
                        desc = stringResource(R.string.sync_to_calendar_desc),
                        checked = state.calendarSyncEnabled,
                        onCheckedChange = { viewModel.setEvent(NotificationCenterContract.Event.OnCalendarSyncToggle(it)) }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun NotificationSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun NotificationItem(
    icon: ImageVector,
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ScriblyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
