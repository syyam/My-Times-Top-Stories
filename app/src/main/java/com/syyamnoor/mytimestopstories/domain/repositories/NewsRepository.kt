package com.syyamnoor.mytimestopstories.domain.repositories

import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.utils.DataState
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getNews(query: String?, value: String): Flow<DataState<Flow<List<News>>>>

    suspend fun getNewsById(id: Long): Flow<DataState<News?>>

}