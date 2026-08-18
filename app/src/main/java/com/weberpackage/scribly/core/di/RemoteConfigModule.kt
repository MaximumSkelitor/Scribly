package com.weberpackage.scribly.core.di

import com.weberpackage.scribly.common.data.repo.RemoteConfigRepository
import com.weberpackage.scribly.data.repo.impl.RemoteConfigRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteConfigModule {

    @Binds
    @Singleton
    abstract fun bindRemoteConfigRepository(
        impl: RemoteConfigRepositoryImpl
    ): RemoteConfigRepository
}
