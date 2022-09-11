package com.syyamnoor.mytimestopstories.data.repositories

import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.repositories.NewsRepository
import com.syyamnoor.mytimestopstories.utils.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NewsRepositoryImpl @Inject constructor(
) : NewsRepository {
    override fun getNews(query: String?, value: String): Flow<DataState<Flow<List<News>>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getNewsById(id: Long): Flow<DataState<News?>> {
        TODO("Not yet implemented")
    }


}