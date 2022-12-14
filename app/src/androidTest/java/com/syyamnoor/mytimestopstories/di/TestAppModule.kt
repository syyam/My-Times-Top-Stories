package com.syyamnoor.mytimestopstories.di

import android.content.Context
import com.syyamnoor.mytimestopstories.data.repositories.NewsRepositoryImpl
import com.syyamnoor.mytimestopstories.data.retrofit.NyTimesApi
import com.syyamnoor.mytimestopstories.data.retrofit.RetrofitNewsMapper
import com.syyamnoor.mytimestopstories.data.room.RoomNewsMapper
import com.syyamnoor.mytimestopstories.data.room.daos.NewsDao
import com.syyamnoor.mytimestopstories.data.room.daos.NewsImageDao
import com.syyamnoor.mytimestopstories.domain.repositories.NewsRepository
import com.syyamnoor.mytimestopstories.utils.AppDispatchers
import com.syyamnoor.mytimestopstories.utils.isConnectedToInternet
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
@TestInstallIn(components = [SingletonComponent::class], replaces = [AppModule::class])
@Module
object TestAppModule {

    @Provides
    fun provideContext(): Context {
        val context = mockk<Context>(relaxed = true)
        every { context.getString(any()) } returns "Some Error Message"
        every { context.isConnectedToInternet() } returns true
        return context
    }

    @Provides
    fun provideDispatchers(): AppDispatchers {
        return object : AppDispatchers {
            override fun main(): CoroutineDispatcher {
                return TestCoroutineDispatcher()
            }

            override fun default(): CoroutineDispatcher {
                return TestCoroutineDispatcher()
            }

            override fun io(): CoroutineDispatcher {
                return TestCoroutineDispatcher()
            }

        }
    }


    @ExperimentalCoroutinesApi
    @Provides
    fun provideNewsRepository(
        retrofitNewsMapper: RetrofitNewsMapper, nyTimesApi: NyTimesApi,
        roomNewsMapper: RoomNewsMapper,
        newsDao: NewsDao, newsImageDao: NewsImageDao,
        appDispatchers: AppDispatchers, context: Context
    ): NewsRepository {
        return NewsRepositoryImpl(
            retrofitNewsMapper,
            nyTimesApi,
            roomNewsMapper,
            newsDao,
            newsImageDao,
            appDispatchers,
            context
        )
    }
}