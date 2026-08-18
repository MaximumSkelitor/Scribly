package com.weberpackage.scribly.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val billingCycle: String, // e.g., "Monthly", "Yearly"
    val startDate: Long, // Timestamp
    val nextPaymentDate: Long, // Timestamp
    val category: String,
    val icon: String = "💳", // Default icon/emoji
    val isFreeTrial: Boolean = false,
    val notes: String? = null,
    val sharingCount: Int = 1,
    val currencyCode: String = "USD"
)
