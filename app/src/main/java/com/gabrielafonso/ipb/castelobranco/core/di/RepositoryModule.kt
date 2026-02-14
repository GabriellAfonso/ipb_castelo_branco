package com.gabrielafonso.ipb.castelobranco.core.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

//    @Binds
//    @Singleton
//    abstract fun bindHymnalRepository(impl: HymnalRepositoryImpl): HymnalRepository

//    @Binds
//    @Singleton
//    abstract fun bindSongsRepository(impl: SongsRepositoryImpl): SongsRepository

//    @Binds
//    @Singleton
//    abstract fun bindMonthScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository

//    @Binds
//    @Singleton
//    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

//    @Binds
//    @Singleton
//    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

//    @Binds
//    @Singleton
//    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
