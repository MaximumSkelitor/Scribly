package com.weberpackage.scribly.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Subscription::class], version = 4, exportSchema = false)
abstract class ScriblyDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        fun populateDatabase(subscriptionDao: SubscriptionDao) {
            val sampleSubscriptions = listOf(
                Subscription(
                    name = "Netflix",
                    price = 15.99,
                    billingCycle = "Monthly",
                    startDate = System.currentTimeMillis() - 86400000 * 30,
                    nextPaymentDate = System.currentTimeMillis(),
                    category = "Entertainment",
                    icon = "🎬",
                    notes = "Premium plan"
                ),
                Subscription(
                    name = "Spotify",
                    price = 9.99,
                    billingCycle = "Monthly",
                    startDate = System.currentTimeMillis() - 86400000 * 15,
                    nextPaymentDate = System.currentTimeMillis() + 86400000 * 5,
                    category = "Music & Audio",
                    icon = "🎵"
                ),
                Subscription(
                    name = "Adobe Creative Cloud",
                    price = 52.99,
                    billingCycle = "Monthly",
                    startDate = System.currentTimeMillis() - 86400000 * 10,
                    nextPaymentDate = System.currentTimeMillis() + 86400000 * 20,
                    category = "Software",
                    icon = "🎨",
                    isFreeTrial = true,
                    notes = "Student discount"
                ),
                Subscription(
                    name = "iCloud",
                    price = 2.99,
                    billingCycle = "Monthly",
                    startDate = System.currentTimeMillis() - 86400000 * 60,
                    nextPaymentDate = System.currentTimeMillis() + 86400000 * 12,
                    category = "Cloud Storage",
                    icon = "☁️"
                ),
                Subscription(
                    name = "Gym Membership",
                    price = 45.00,
                    billingCycle = "Monthly",
                    startDate = System.currentTimeMillis() - 86400000 * 90,
                    nextPaymentDate = System.currentTimeMillis() + 86400000 * 2,
                    category = "Health & Fitness",
                    icon = "🏋️"
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                sampleSubscriptions.forEach { subscriptionDao.insertSubscription(it) }
            }
        }
    }
}
