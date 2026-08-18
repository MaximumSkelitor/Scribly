package com.weberpackage.scribly.data

import kotlinx.coroutines.flow.Flow

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(private val subscriptionDao: SubscriptionDao) {
    val allSubscriptions: Flow<List<Subscription>> = subscriptionDao.getAllSubscriptions()

    suspend fun getSubscriptionById(id: Long): Subscription? {
        return subscriptionDao.getSubscriptionById(id)
    }

    suspend fun insert(subscription: Subscription) {
        subscriptionDao.insertSubscription(subscription)
    }

    suspend fun update(subscription: Subscription) {
        subscriptionDao.updateSubscription(subscription)
    }

    suspend fun delete(subscription: Subscription) {
        subscriptionDao.deleteSubscription(subscription)
    }
}
