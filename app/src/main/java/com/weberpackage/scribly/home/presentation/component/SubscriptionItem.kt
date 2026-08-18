package com.weberpackage.scribly.home.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberpackage.scribly.R
import com.weberpackage.scribly.add_edit.presentation.component.supportedCurrencies
import com.weberpackage.scribly.add_edit.presentation.model.getCategoryIcon
import com.weberpackage.scribly.common.presentation.components.ScriblyCard
import com.weberpackage.scribly.common.presentation.utils.calculateLifetimeSpent
import com.weberpackage.scribly.data.Subscription
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun SubscriptionItem(
    subscription: Subscription,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", LocalLocale.current.platformLocale)
    val dateStr = dateFormat.format(Date(subscription.nextPaymentDate))
    val lifetimeSpent = calculateLifetimeSpent(subscription)
    val currencySymbol = supportedCurrencies.find { it.code == subscription.currencyCode }?.symbol ?: "$"

    ScriblyCard(
        modifier = Modifier.padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (subscription.icon.isNotBlank()) {
                    Text(text = subscription.icon, fontSize = 20.sp)
                } else {
                    Icon(
                        imageVector = getCategoryIcon(subscription.category),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (subscription.isFreeTrial) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.free_trial),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.category_date_separator, subscription.category, dateStr),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                if (lifetimeSpent > 0) {
                    Text(
                        text = "Total spent: ${stringResource(R.string.price_format_simple, currencySymbol, String.format(Locale.US, "%.2f", lifetimeSpent))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val displayPrice = if (subscription.sharingCount > 1) {
                    subscription.price / subscription.sharingCount
                } else {
                    subscription.price
                }
                
                Text(
                    text = stringResource(R.string.price_format_simple, currencySymbol, String.format(Locale.US, "%.2f", displayPrice)),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = stringResource(R.string.cycle_format, subscription.billingCycle.take(2).lowercase()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}
