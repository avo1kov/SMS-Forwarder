package ru.smsforwarder.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.smsforwarder.domain.UserInfoProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Singleton
    @Provides
    fun provideUserInfoProvider(): UserInfoProvider = UserInfoProvider()
}
