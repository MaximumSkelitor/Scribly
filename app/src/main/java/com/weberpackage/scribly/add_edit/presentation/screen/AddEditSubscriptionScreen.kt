package com.weberpackage.scribly.add_edit.presentation.screen

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberpackage.scribly.R
import com.weberpackage.scribly.add_edit.presentation.component.BillingCycleSelectorSheet
import com.weberpackage.scribly.add_edit.presentation.component.CategorySelectorSheet
import com.weberpackage.scribly.add_edit.presentation.component.CurrencySelectorSheet
import com.weberpackage.scribly.add_edit.presentation.component.EmojiSelectorSheet
import com.weberpackage.scribly.add_edit.presentation.component.ScriblyDatePicker
import com.weberpackage.scribly.add_edit.presentation.component.ScriblyGradientButton
import com.weberpackage.scribly.add_edit.presentation.component.ScriblyInputLabel
import com.weberpackage.scribly.add_edit.presentation.component.ScriblyTextField
import com.weberpackage.scribly.add_edit.presentation.component.TemplateSelectorSheet
import com.weberpackage.scribly.add_edit.presentation.component.TrialDurationPickerSheet
import com.weberpackage.scribly.add_edit.presentation.component.supportedCurrencies
import com.weberpackage.scribly.add_edit.presentation.contract.AddEditContract
import com.weberpackage.scribly.add_edit.presentation.viewmodel.AddEditViewModel
import com.weberpackage.scribly.common.presentation.utils.ObserveAsEvents
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    viewModel: AddEditViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val state = viewModel.viewState.value
    var showPaymentDatePicker by rememberSaveable { mutableStateOf(false) }
    var showStartDatePicker by rememberSaveable { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showBillingCycleSheet by remember { mutableStateOf(false) }
    var showEmojiSheet by remember { mutableStateOf(false) }
    var showTrialPicker by remember { mutableStateOf(false) }
    var showTemplateSheet by remember { mutableStateOf(false) }
    var showCurrencySheet by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    var isScrollingUp by remember { mutableStateOf(true) }
    var lastScrollOffset by remember { mutableIntStateOf(0) }
    var shouldShowBottomBar by remember { mutableStateOf(true) }

    // Bottom Bar Visibility Logic
    LaunchedEffect(scrollState.value, scrollState.maxValue) {
        val isAtBottom =
            scrollState.value >= (scrollState.maxValue - 10) && scrollState.maxValue > 0
        val isAtTop = scrollState.value <= 10

        if (scrollState.value != lastScrollOffset) {
            isScrollingUp = scrollState.value < lastScrollOffset
            lastScrollOffset = scrollState.value
        }

        if (isAtTop) {
            shouldShowBottomBar = true
        } else if (isAtBottom) {
            delay(200)
            if (scrollState.value >= (scrollState.maxValue - 10)) {
                shouldShowBottomBar = true
            }
        } else {
            shouldShowBottomBar = isScrollingUp
        }
    }

    ObserveAsEvents(flow = viewModel.effect) { effect ->
        when (effect) {
            is AddEditContract.Effect.NavigateBack -> onNavigateBack()
            is AddEditContract.Effect.ShowCategorySelector -> showCategorySheet = true
            is AddEditContract.Effect.ShowBillingCycleSelector -> showBillingCycleSheet = true
            is AddEditContract.Effect.ShowEmojiSelector -> showEmojiSheet = true
            is AddEditContract.Effect.ShowTrialDurationPicker -> showTrialPicker = true
            is AddEditContract.Effect.ShowCurrencySelector -> showCurrencySheet = true
            is AddEditContract.Effect.AddToCalendar -> {
                val priceText = context.getString(R.string.price_format, String.format(Locale.US, "%.2f", effect.price))
                val title = context.getString(R.string.calendar_event_title, effect.name)
                val description = context.getString(R.string.calendar_event_description, effect.name, priceText, effect.notes ?: "")

                val intent = Intent(Intent.ACTION_INSERT).apply {
                    data = CalendarContract.Events.CONTENT_URI
                    putExtra(CalendarContract.Events.TITLE, title)
                    putExtra(CalendarContract.Events.DESCRIPTION, description)
                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, effect.date)
                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, effect.date + 30 * 60 * 1000)
                    putExtra(CalendarContract.Events.ALL_DAY, false)
                    putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                }
                context.startActivity(intent)
            }
        }
    }

    if (showCategorySheet) {
        CategorySelectorSheet(
            onCategorySelected = { category ->
                viewModel.setEvent(AddEditContract.Event.OnCategoryChange(category.name))
            },
            onDismiss = { showCategorySheet = false }
        )
    }

    if (showBillingCycleSheet) {
        BillingCycleSelectorSheet(
            selectedCycle = state.billingCycle,
            onCycleSelected = { cycle ->
                viewModel.setEvent(AddEditContract.Event.OnBillingCycleChange(cycle))
            },
            onDismiss = { showBillingCycleSheet = false }
        )
    }

    if (showEmojiSheet) {
        EmojiSelectorSheet(
            onEmojiSelected = { emoji ->
                viewModel.setEvent(AddEditContract.Event.OnIconChange(emoji))
            },
            onDismiss = { showEmojiSheet = false }
        )
    }

    if (showTrialPicker) {
        TrialDurationPickerSheet(
            initialDays = state.trialDays,
            onConfirm = { days ->
                viewModel.setEvent(AddEditContract.Event.OnTrialDaysConfirm(days))
                showTrialPicker = false
            },
            onDismiss = { showTrialPicker = false }
        )
    }

    if (showCurrencySheet) {
        CurrencySelectorSheet(
            selectedCode = state.currencyCode,
            onCurrencySelected = { code ->
                viewModel.setEvent(AddEditContract.Event.OnCurrencyChange(code))
            },
            onDismiss = { showCurrencySheet = false }
        )
    }

    if (showTemplateSheet) {
        TemplateSelectorSheet(
            onTemplateSelected = { template ->
                viewModel.setEvent(AddEditContract.Event.OnNameChange(template.name))
                viewModel.setEvent(AddEditContract.Event.OnCategoryChange(template.category))
                viewModel.setEvent(AddEditContract.Event.OnIconChange(template.icon))
                viewModel.setEvent(AddEditContract.Event.OnPriceChange(template.defaultPrice.toString()))
                showTemplateSheet = false
            },
            onDismiss = { showTemplateSheet = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        // Main Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (state.isEditMode) stringResource(R.string.subscription_details) else stringResource(
                            R.string.add_subscription
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(AddEditContract.Event.OnBackClick) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (!state.isEditMode) {
                        IconButton(onClick = { showTemplateSheet = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.presets)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Emoji / Icon Section
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.setEvent(AddEditContract.Event.OnEmojiClick) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.icon, fontSize = 40.sp)
                }

                TextButton(onClick = { viewModel.setEvent(AddEditContract.Event.OnEmojiClick) }) {
                    Text(
                        stringResource(R.string.tap_to_change_emoji),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // INPUT FIELDS
                ScriblyInputLabel(text = stringResource(R.string.name), isRequired = true)
                ScriblyTextField(
                    value = state.name,
                    onValueChange = { viewModel.setEvent(AddEditContract.Event.OnNameChange(it)) },
                    placeholder = stringResource(R.string.eg_netflix_spotify)
                )

                if (state.isEditMode && state.totalSpent > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Lifetime spent: ${stringResource(R.string.price_format, String.format(Locale.US, "%.2f", state.totalSpent))}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ScriblyInputLabel(text = stringResource(R.string.price), isRequired = true)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScriblyTextField(
                        value = state.price,
                        onValueChange = { viewModel.setEvent(AddEditContract.Event.OnPriceChange(it)) },
                        placeholder = stringResource(R.string.zero_price),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    
                    val selectedCurrencySymbol = supportedCurrencies.find { it.code == state.currencyCode }?.symbol ?: "$"
                    TextButton(
                        onClick = { viewModel.setEvent(AddEditContract.Event.OnCurrencyClick) },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(text = "$selectedCurrencySymbol ${state.currencyCode}", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ScriblyInputLabel(text = "Shared with", isRequired = false)
                ScriblyTextField(
                    value = state.sharingCount,
                    onValueChange = { viewModel.setEvent(AddEditContract.Event.OnSharingCountChange(it)) },
                    placeholder = "1 person",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ScriblyInputLabel(text = stringResource(R.string.billing_cycle), isRequired = true)
                ScriblyTextField(
                    value = state.billingCycle,
                    onValueChange = {},
                    readOnly = true,
                    onClick = { viewModel.setEvent(AddEditContract.Event.OnBillingCycleClick) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ScriblyInputLabel(text = stringResource(R.string.category), isRequired = false)
                ScriblyTextField(
                    value = state.category,
                    onValueChange = {},
                    readOnly = true,
                    onClick = { viewModel.setEvent(AddEditContract.Event.OnCategoryClick) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ScriblyInputLabel(text = stringResource(R.string.start_date), isRequired = false)
                ScriblyTextField(
                    value = formatDateForDisplay(state.startDate),
                    onValueChange = {},
                    readOnly = true,
                    onClick = { showStartDatePicker = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ScriblyInputLabel(
                    text = stringResource(R.string.next_payment_date),
                    isRequired = false
                )
                ScriblyTextField(
                    value = formatDateForDisplay(state.paymentDate),
                    onValueChange = {},
                    readOnly = true,
                    onClick = { showPaymentDatePicker = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.free_trial),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = state.isFreeTrial,
                        onCheckedChange = {
                            viewModel.setEvent(
                                AddEditContract.Event.OnFreeTrialToggle(
                                    it
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.sync_to_calendar),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            stringResource(R.string.sync_to_calendar_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = state.isCalendarSyncEnabled,
                        onCheckedChange = {
                            viewModel.setEvent(
                                AddEditContract.Event.OnCalendarSyncToggle(
                                    it
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { viewModel.setEvent(AddEditContract.Event.OnNotesChange(it)) },
                    label = { Text(stringResource(R.string.notes_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Large spacer at the bottom to ensure user can scroll content past the bottom bar
                Spacer(modifier = Modifier.height(240.dp))
            }
        }

        // Overlayed Animated Bottom Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp, top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScriblyGradientButton(
                        text = if (state.isEditMode) stringResource(R.string.save_changes) else stringResource(
                            R.string.add_subscription
                        ),
                        onClick = { viewModel.setEvent(AddEditContract.Event.OnSaveClick) }
                    )

                    if (state.isEditMode) {
                        Button(
                            onClick = { viewModel.setEvent(AddEditContract.Event.OnDeleteClick) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.delete_subscription),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showStartDatePicker) {
        ScriblyDatePicker(
            initialDateMillis = state.startDate.toPaymentDateMillis(),
            onResult = {
                viewModel.setEvent(AddEditContract.Event.OnStartDateChange(it))
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showPaymentDatePicker) {
        ScriblyDatePicker(
            initialDateMillis = state.paymentDate.toPaymentDateMillis(),
            onResult = {
                viewModel.setEvent(AddEditContract.Event.OnPaymentDateChange(it))
                showPaymentDatePicker = false
            },
            onDismiss = { showPaymentDatePicker = false }
        )
    }
}

private fun String.toPaymentDateMillis(): Long? = runCatching {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    sdf.parse(this)?.time
}.getOrNull()

@Composable
private fun formatDateForDisplay(dateStr: String): String {
    if (dateStr.isBlank()) return stringResource(R.string.select_date)
    val sdf = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val displaySdf = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    return remember(dateStr) {
        try {
            val date = sdf.parse(dateStr)
            displaySdf.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }
}
