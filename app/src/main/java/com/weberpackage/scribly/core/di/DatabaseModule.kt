package com.weberpackage.scribly.core.di

import android.content.Context
import androidx.room.Room
import com.weberpackage.scribly.data.ScriblyDatabase
import com.weberpackage.scribly.data.SubscriptionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ScriblyDatabase {
        return Room.databaseBuilder(
            context,
            ScriblyDatabase::class.java,
            "scribly_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideSubscriptionDao(database: ScriblyDatabase): SubscriptionDao {
        val dao = database.subscriptionDao()
        // Simple way to trigger population on first access if needed, 
        // but here we just return the dao.
        return dao
    }
}
