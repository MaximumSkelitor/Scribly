package com.weberpackage.scribly.widget.presentation

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.weberpackage.scribly.MainActivity
import com.weberpackage.scribly.R
import com.weberpackage.scribly.data.ScriblyDatabase
import com.weberpackage.scribly.data.Subscription
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpcomingWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = androidx.room.Room.databaseBuilder(
            context,
            ScriblyDatabase::class.java,
            "scribly_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        
        val dao = database.subscriptionDao()
        val subscriptions = try {
            dao.getAllSubscriptions().first()
                .sortedBy { it.nextPaymentDate }
        } catch (e: Exception) {
            emptyList()
        } finally {
            database.close()
        }

        provideContent {
            GlanceTheme {
                UpcomingWidgetContent(subscriptions)
            }
        }
    }
}

@Composable
private fun UpcomingWidgetContent(subscriptions: List<Subscription>) {
    val context = LocalContext.current
    val size = LocalSize.current
    
    // Optimized for small sizes:
    // If height is very small, we reduce padding and font sizes significantly to fit 3 items.
    val isVerySmall = size.height < 120.dp
    
    val padding = if (isVerySmall) 6.dp else 12.dp
    val headerHeight = if (isVerySmall) 24.dp else 32.dp
    val itemHeight = if (isVerySmall) 34.dp else 52.dp // Compact vs Standard row height
    val spacerHeight = if (isVerySmall) 2.dp else 6.dp

    val availableHeight = size.height - (padding * 2) - headerHeight
    val maxItems = (availableHeight / itemHeight).toInt().coerceAtLeast(1)
    
    val itemsToShow = subscriptions.take(maxItems)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(padding)
            .background(GlanceTheme.colors.surface)
            .clickable(actionStartActivity(ComponentName(context, MainActivity::class.java))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.Top
    ) {
        if (size.height > 60.dp) {
            Text(
                text = context.getString(R.string.upcoming),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isVerySmall) 13.sp else 16.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
            Spacer(modifier = GlanceModifier.height(if (isVerySmall) 4.dp else 8.dp))
        }

        if (itemsToShow.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.no_upcoming_renewals),
                    style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant, fontSize = 11.sp)
                )
            }
        } else {
            Column(modifier = GlanceModifier.fillMaxWidth()) {
                itemsToShow.forEach { sub ->
                    SubscriptionWidgetItem(sub, isCompact = isVerySmall)
                    Spacer(modifier = GlanceModifier.height(spacerHeight))
                }
            }
        }
    }
}

@Composable
private fun SubscriptionWidgetItem(subscription: Subscription, isCompact: Boolean) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("MMM dd", Locale.US)
    val dateStr = sdf.format(Date(subscription.nextPaymentDate))

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f))
            .cornerRadius(if (isCompact) 8.dp else 12.dp)
            .padding(horizontal = 8.dp, vertical = if (isCompact) 2.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = subscription.icon,
            style = TextStyle(fontSize = if (isCompact) 13.sp else 16.sp)
        )

        Spacer(modifier = GlanceModifier.width(if (isCompact) 8.dp else 10.dp))

        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = subscription.name,
                maxLines = 1,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isCompact) 11.sp else 13.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
            if (!isCompact || sizeHeightAbove(40.dp)) {
                Text(
                    text = dateStr,
                    style = TextStyle(fontSize = if (isCompact) 9.sp else 11.sp, color = GlanceTheme.colors.onSurfaceVariant)
                )
            }
        }
        Text(
            text = context.getString(R.string.price_format, String.format(Locale.US, "%.2f", subscription.price)),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 11.sp else 13.sp,
                color = GlanceTheme.colors.primary
            )
        )
    }
}

@Composable
private fun sizeHeightAbove(threshold: androidx.compose.ui.unit.Dp): Boolean {
    return LocalSize.current.height > threshold
}
