package com.syyamnoor.mytimestopstories.di

import android.content.Context
import com.syyamnoor.mytimestopstories.data.repositories.NewsRepositoryImpl
import com.syyamnoor.mytimestopstories.data.room.RoomNewsMapper
import com.syyamnoor.mytimestopstories.data.room.daos.NewsDao
import com.syyamnoor.mytimestopstories.data.room.daos.NewsImageDao
import com.syyamnoor.mytimestopstories.data.retrofit.NyTimesApi
import com.syyamnoor.mytimestopstories.data.retrofit.RetrofitNewsMapper
import com.syyamnoor.mytimestopstories.domain.repositories.NewsRepository
import com.syyamnoor.mytimestopstories.utils.AppDispatchers

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideDispatchers(): AppDispatchers {
        return object : AppDispatchers {
            override fun main(): CoroutineDispatcher {
                return Dispatchers.Main
            }

            override fun default(): CoroutineDispatcher {
                return Dispatchers.Default
            }

            override fun io(): CoroutineDispatcher {
                return Dispatchers.IO
            }

        }
    }

    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideNewsRepository(
        retrofitNewsMapper: RetrofitNewsMapper, nyTimesApi: NyTimesApi,
        roomNewsMapper: RoomNewsMapper,
        newsDao: NewsDao, newsImageDao: NewsImageDao,
        appDispatchers: AppDispatchers, @ApplicationContext context: Context
    ): NewsRepository {
        return NewsRepositoryImpl(

        )
    }
}