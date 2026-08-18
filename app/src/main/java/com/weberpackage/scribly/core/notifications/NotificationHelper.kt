package com.weberpackage.scribly.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.weberpackage.scribly.MainActivity
import com.weberpackage.scribly.R
import com.weberpackage.scribly.data.Subscription
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val CHANNEL_ID = "renewal_reminders_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.renewal_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.renewal_reminders_desc)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showRenewalNotification(subscription: Subscription, isDueToday: Boolean) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            subscription.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val timeText = if (isDueToday) context.getString(R.string.today) else context.getString(R.string.in_2_days)
        val title = if (subscription.isFreeTrial) 
            context.getString(R.string.free_trial_ending) else context.getString(R.string.subscription_ending)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_scribly_logo)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.renewal_message, subscription.name, context.getString(R.string.price_format, String.format(Locale.US, "%.2f", subscription.price)), timeText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(subscription.id.toInt(), notification)
    }

    fun showWeeklyDigestNotification(count: Int, total: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_scribly_logo)
            .setContentTitle(context.getString(R.string.weekly_subscription_digest))
            .setContentText(context.getString(R.string.weekly_digest_message, count, context.getString(R.string.price_format, String.format(Locale.US, "%.2f", total))))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(999, notification)
    }
}
