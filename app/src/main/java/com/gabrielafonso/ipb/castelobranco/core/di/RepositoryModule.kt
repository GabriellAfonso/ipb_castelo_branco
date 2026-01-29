package com.gabrielafonso.ipb.castelobranco.core.di

import com.gabrielafonso.ipb.castelobranco.data.repository.HymnalRepositoryImpl
import com.gabrielafonso.ipb.castelobranco.data.repository.MonthScheduleRepositoryImpl
import com.gabrielafonso.ipb.castelobranco.data.repository.SongsRepositoryImpl
import com.gabrielafonso.ipb.castelobranco.domain.repository.HymnalRepository
import com.gabrielafonso.ipb.castelobranco.domain.repository.MonthScheduleRepository
import com.gabrielafonso.ipb.castelobranco.domain.repository.SongsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHymnalRepository(impl: HymnalRepositoryImpl): HymnalRepository

    @Binds
    @Singleton
    abstract fun bindSongsRepository(impl: SongsRepositoryImpl): SongsRepository

    @Binds
    @Singleton
    abstract fun bindMonthScheduleRepository(impl: MonthScheduleRepositoryImpl): MonthScheduleRepository
}
