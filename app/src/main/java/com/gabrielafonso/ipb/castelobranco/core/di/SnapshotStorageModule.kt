package com.gabrielafonso.ipb.castelobranco.core.di

import android.content.Context
import com.gabrielafonso.ipb.castelobranco.core.data.local.JsonSnapshotStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SnapshotStorageModule {

    @Provides
    @Singleton
    fun provideJsonSnapshotStorage(
        @ApplicationContext context: Context
    ): JsonSnapshotStorage = JsonSnapshotStorage(context)
}