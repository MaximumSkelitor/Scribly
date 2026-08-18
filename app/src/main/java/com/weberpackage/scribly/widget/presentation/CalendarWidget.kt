package com.weberpackage.scribly.widget.presentation

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.weberpackage.scribly.MainActivity
import com.weberpackage.scribly.R
import com.weberpackage.scribly.data.ScriblyDatabase
import com.weberpackage.scribly.data.Subscription
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CalendarWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

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
        } catch (e: Exception) {
            emptyList()
        } finally {
            database.close()
        }

        provideContent {
            GlanceTheme {
                CalendarWidgetContent(subscriptions)
            }
        }
    }
}

private val monthOffsetKey = intPreferencesKey("month_offset")

@SuppressLint("RestrictedApi")
@Composable
private fun CalendarWidgetContent(subscriptions: List<Subscription>) {
    val context = LocalContext.current
    val prefs = currentState<Preferences>()
    val monthOffset = prefs[monthOffsetKey] ?: 0

    val utcZone = TimeZone.getTimeZone("UTC")
    val calendar = Calendar.getInstance(utcZone).apply {
        add(Calendar.MONTH, monthOffset)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val today = Calendar.getInstance(utcZone)

    val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun
    val prevMonthDaysToShow = firstDayOfWeek - 1

    val prevMonthCal = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -1) }
    val daysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val renewalMap = subscriptions.filter { sub ->
        val subCal = Calendar.getInstance(utcZone).apply { timeInMillis = sub.nextPaymentDate }
        subCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
        subCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
    }.groupBy {
        Calendar.getInstance(utcZone).apply { timeInMillis = it.nextPaymentDate }.get(Calendar.DAY_OF_MONTH)
    }

    val backgroundColor = Color(0xFF232533)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(backgroundColor)
            .cornerRadius(24.dp)
            .padding(10.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthName ?: "",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GlanceTheme.colors.onSurface)
            )

            Spacer(modifier = GlanceModifier.defaultWeight())

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = GlanceModifier.size(40.dp).clickable(actionRunCallback<MonthNavAction>(actionParametersOf(navKey to -1))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = context.getString(R.string.nav_prev), style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                }
                Box(
                    modifier = GlanceModifier.size(40.dp).clickable(actionRunCallback<MonthNavAction>(actionParametersOf(navKey to 1))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = context.getString(R.string.nav_next), style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = GlanceModifier.width(8.dp))
                Box(
                    modifier = GlanceModifier
                        .size(44.dp)
                        .background(Color(0xFFACC7FF))
                        .cornerRadius(12.dp)
                        .clickable(actionStartActivity(
                            Intent(context, MainActivity::class.java).apply {
                                action = "com.weberpackage.scribly.ACTION_ADD_SUBSCRIPTION"
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        )),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = context.getString(R.string.plus_label), style = TextStyle(color = ColorProvider(Color.Black), fontWeight = FontWeight.Bold, fontSize = 20.sp))
                }
            }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Weekdays
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            listOf(
                context.getString(R.string.day_sun),
                context.getString(R.string.day_mon),
                context.getString(R.string.day_tue),
                context.getString(R.string.day_wed),
                context.getString(R.string.day_thu),
                context.getString(R.string.day_fri),
                context.getString(R.string.day_sat)
            ).forEach { day ->
                Text(
                    text = day,
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(fontSize = 11.sp, color = GlanceTheme.colors.onSurfaceVariant, textAlign = TextAlign.Center)
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(4.dp))
        
        // The Grid with Visible Borders
        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
        ) {
            var dayCounter = 1 - prevMonthDaysToShow

            for (week in 0..5) {
                if (dayCounter > daysInMonth && week > 0) break

                Row(modifier = GlanceModifier.fillMaxWidth().defaultWeight()) {
                    for (dayOfWeek in 0..6) {
                        val isCurrentMonth = dayCounter in 1..daysInMonth
                        val displayDay = if (dayCounter < 1) daysInPrevMonth + dayCounter else if (dayCounter > daysInMonth) dayCounter - daysInMonth else dayCounter

                        val isToday = isCurrentMonth &&
                                     dayCounter == today.get(Calendar.DAY_OF_MONTH) &&
                                     calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)

                        val renewals = if (isCurrentMonth) renewalMap[dayCounter] else null

                        // Cell with background showing through the parent border color
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .fillMaxHeight()
                                .padding(0.5.dp) // Creates the consistent 1dp border effect
                                .background(backgroundColor),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            DayCell(
                                day = displayDay,
                                isCurrentMonth = isCurrentMonth,
                                isToday = isToday,
                                renewals = renewals
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun DayCell(
    day: Int,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    renewals: List<Subscription>?
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.Top
    ) {
        // Day Number
        Box(
            modifier = GlanceModifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isToday) {
                Box(modifier = GlanceModifier.fillMaxSize().background(Color(0xFFACC7FF)).cornerRadius(8.dp)) {}
            }
            Text(
                text = day.toString(),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = if (isToday) ColorProvider(Color.Black) else if (isCurrentMonth) ColorProvider(Color.White) else ColorProvider(Color.White.copy(alpha = 0.4f)),
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            )
        }


        // Subscription Badge
        if (!renewals.isNullOrEmpty()) {
//            Spacer(GlanceModifier.padding(top = 2.dp))
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 1.dp)
                    .background(Color(0xFF5D5FEF))
                    .cornerRadius(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = renewals[0].name,
                    maxLines = 1,
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = ColorProvider(Color.White),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

private val navKey = ActionParameters.Key<Int>("nav_direction")

class MonthNavAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val direction = parameters[navKey] ?: 0
        updateAppWidgetState(context, glanceId) { prefs ->
            val current = prefs[monthOffsetKey] ?: 0
            prefs[monthOffsetKey] = current + direction
        }
        CalendarWidget().update(context, glanceId)
    }
}
