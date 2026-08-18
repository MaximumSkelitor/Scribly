package com.weberpackage.scribly.core.prefs

sealed class Pref<T>(val key: String, val defaultValue: T) {
    data object AppTheme : Pref<String>("app_theme", "SYSTEM")
    data object RenewalRemindersEnabled : Pref<Boolean>("renewal_reminders_enabled", true)
    
    // Notification Center Settings
    data object UpcomingPaymentsEnabled : Pref<Boolean>("upcoming_payments_enabled", true)
    data object SubscriptionEndingEnabled : Pref<Boolean>("subscription_ending_enabled", true)
    data object FreeTrialEndingEnabled : Pref<Boolean>("free_trial_ending_enabled", true)
    data object FreeTrialEndedEnabled : Pref<Boolean>("free_trial_ended_enabled", true)
    data object PriceChangesEnabled : Pref<Boolean>("price_changes_enabled", true)
    data object WeeklyDigestEnabled : Pref<Boolean>("weekly_digest_enabled", false)
    data object BiometricLockEnabled : Pref<Boolean>("biometric_lock_enabled", false)
    data object CalendarSyncEnabled : Pref<Boolean>("calendar_sync_enabled", false)
}
