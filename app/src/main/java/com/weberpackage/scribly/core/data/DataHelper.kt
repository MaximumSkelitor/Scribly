package com.weberpackage.scribly.core.data

import android.content.Context
import android.net.Uri
import com.weberpackage.scribly.data.Subscription
import com.weberpackage.scribly.data.SubscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: SubscriptionRepository
) {

    suspend fun exportData(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val subscriptions = repository.allSubscriptions.first()
            val json = Json.encodeToString(subscriptions)
            context.contentResolver.openOutputStream(uri)?.use { 
                it.write(json.toByteArray())
            } ?: throw Exception("Failed to open output stream")
        }
    }

    suspend fun importData(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: throw Exception("Failed to read file")
            val subscriptions = Json.decodeFromString<List<Subscription>>(json)
            subscriptions.forEach { repository.insert(it.copy(id = 0L)) }
        }
    }
}
