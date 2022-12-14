package com.syyamnoor.mytimestopstories.di

import com.syyamnoor.mytimestopstories.data.retrofit.NyTimesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RetrofitModule::class])
object TestRetrofitModule {

    @Singleton
    @Provides
    fun provideNyTimesApi(): NyTimesApi {
        val mockk = mockk<NyTimesApi>(relaxed = true)
        return mockk
    }

}