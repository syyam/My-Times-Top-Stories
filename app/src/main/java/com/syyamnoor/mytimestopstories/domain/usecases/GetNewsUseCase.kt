package com.syyamnoor.mytimestopstories.domain.usecases

import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.repositories.NewsRepository
import com.syyamnoor.mytimestopstories.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(val newsRepository: NewsRepository) {

    operator fun invoke(query: String?, value: String): Flow<DataState<Flow<List<News>>>> {
        return newsRepository.getNews(query, value)
    }

}