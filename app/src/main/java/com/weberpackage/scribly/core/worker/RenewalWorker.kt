package com.weberpackage.scribly.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.weberpackage.scribly.core.notifications.NotificationHelper
import com.weberpackage.scribly.core.prefs.Pref
import com.weberpackage.scribly.core.prefs.Prefs
import com.weberpackage.scribly.data.Subscription
import com.weberpackage.scribly.data.SubscriptionDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class RenewalWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val subscriptionDao: SubscriptionDao,
    private val notificationHelper: NotificationHelper,
    private val prefs: Prefs
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // 1. Master switch check
        if (!prefs.get(Pref.RenewalRemindersEnabled)) {
            return Result.success()
        }

        val calendar = Calendar.getInstance()
        
        // 2. Weekly Digest (Only on Mondays)
        if (prefs.get(Pref.WeeklyDigestEnabled) && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            handleWeeklyDigest()
        }

        // 3. Define time ranges for specific reminders
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = today.timeInMillis
        val endOfToday = today.apply { add(Calendar.DAY_OF_YEAR, 1); add(Calendar.MILLISECOND, -1) }.timeInMillis

        val twoDaysLater = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 2)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfTwoDays = twoDaysLater.timeInMillis
        val endOfTwoDays = twoDaysLater.apply { add(Calendar.DAY_OF_YEAR, 1); add(Calendar.MILLISECOND, -1) }.timeInMillis

        // 4. Fetch setting values for specific reminders
        val remindUpcoming = prefs.get(Pref.UpcomingPaymentsEnabled)
        val remindTrialEnding = prefs.get(Pref.FreeTrialEndingEnabled)
        val remindTrialEnded = prefs.get(Pref.FreeTrialEndedEnabled)
        val remindEnding = prefs.get(Pref.SubscriptionEndingEnabled)

        // 5. Process Today's renewals (Ended/Due Today)
        val dueToday = subscriptionDao.getSubscriptionsInDateRange(startOfToday, endOfToday)
        dueToday.forEach { sub ->
            if (shouldNotify(sub, true, remindUpcoming, remindTrialEnding, remindTrialEnded, remindEnding)) {
                notificationHelper.showRenewalNotification(sub, isDueToday = true)
            }
        }

        // 6. Process "In 2 Days" renewals (Ending Soon)
        val dueInTwoDays = subscriptionDao.getSubscriptionsInDateRange(startOfTwoDays, endOfTwoDays)
        dueInTwoDays.forEach { sub ->
            if (shouldNotify(sub, false, remindUpcoming, remindTrialEnding, remindTrialEnded, remindEnding)) {
                notificationHelper.showRenewalNotification(sub, isDueToday = false)
            }
        }

        return Result.success()
    }

    private suspend fun handleWeeklyDigest() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfWeek = calendar.timeInMillis
        val endOfWeek = calendar.apply { add(Calendar.DAY_OF_YEAR, 7) }.timeInMillis

        val subscriptionsThisWeek = subscriptionDao.getSubscriptionsInDateRange(startOfWeek, endOfWeek)
        
        if (subscriptionsThisWeek.isNotEmpty()) {
            val total = subscriptionsThisWeek.sumOf { it.price }
            notificationHelper.showWeeklyDigestNotification(
                count = subscriptionsThisWeek.size,
                total = total
            )
        }
    }

    private fun shouldNotify(
        sub: Subscription,
        isToday: Boolean,
        remindUpcoming: Boolean,
        remindTrialEnding: Boolean,
        remindTrialEnded: Boolean,
        remindEnding: Boolean
    ): Boolean {
        return if (sub.isFreeTrial) {
            if (isToday) remindTrialEnded else remindTrialEnding
        } else {
            // For normal subs, we check upcoming/ending toggles
            // Typically these might both apply to "Today" and "Soon" reminders in this layout
            remindUpcoming || remindEnding
        }
    }

    companion object {
        private const val WORK_NAME = "SubscriptionRenewalWork"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<RenewalWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
