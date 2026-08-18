package com.weberpackage.scribly.common.presentation.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberpackage.scribly.common.presentation.components.ScriblyAlerter
import com.weberpackage.scribly.common.presentation.components.iconPulse
import com.weberpackage.scribly.common.presentation.theme.AlerterError
import com.weberpackage.scribly.common.presentation.theme.AlerterInfo
import com.weberpackage.scribly.common.presentation.theme.spacing

fun Activity.showToast(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_LONG
) {
    Toast.makeText(this, this.getString(message), duration).show()
}

fun Context.showToast(
    @StringRes message: Int,
    duration: Int = Toast.LENGTH_LONG
) {
    Toast.makeText(this, this.getString(message), duration).show()
}

fun Activity.showAlerter(
    title: UiText? = null,
    message: UiText,
    backgroundColor: Color = AlerterInfo,
    duration: Long = 3000,
    enableVibration: Boolean = true,
    enableSwipeToDismiss: Boolean = false,
    disableOutsideTouch: Boolean = false,
    enableInfiniteDuration: Boolean = false,
    gravity: Int = Gravity.TOP,
    isError: Boolean = false
) {
    val alerter = com.tapadoo.alerter.Alerter.create(this)
    alerter.setText(message.asString(this))
    alerter.setBackgroundColorInt(
        if (isError)
            AlerterError.toArgb() else backgroundColor.toArgb()
    )
    alerter.enableClickAnimation(false)
    if (enableSwipeToDismiss) alerter.enableSwipeToDismiss()
    alerter.enableVibration(enableVibration)
    if (enableInfiniteDuration) alerter.enableInfiniteDuration(true)
    alerter.setLayoutGravity(gravity)
    alerter.setDuration(duration)
    if (disableOutsideTouch) alerter.disableOutsideTouch()
    if (title != null) alerter.setTitle(title.asString(this))
    alerter.show()
}

@Composable
fun ErrorAlerter(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    showAlert: Boolean, onChanged: (showAlert: Boolean) -> Unit,
) {
    ScriblyAlerter(
        isShown = showAlert,
        onChanged = onChanged,
        backgroundColor = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.smallTwo)
                    .iconPulse()
            )

            Column(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.smallTwo)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun InfoAlerter(
    modifier: Modifier = Modifier,
    showAlert: Boolean = false,
    title: String,
    message: String,
) {
    var show by remember(showAlert) { mutableStateOf(showAlert) }
    ScriblyAlerter(
        isShown = show,
        onChanged = {
            show = it
        },
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = MaterialTheme.spacing.smallTwo)
                    .iconPulse()
            )

            Column(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.smallTwo)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            }
        }
    }
}
