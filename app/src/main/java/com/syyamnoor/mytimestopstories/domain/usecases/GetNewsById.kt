package com.syyamnoor.mytimestopstories.domain.usecases

import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.repositories.NewsRepository
import com.syyamnoor.mytimestopstories.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsById @Inject constructor(val newsRepository: NewsRepository) {

    suspend operator fun invoke(id: Long): Flow<DataState<News?>> {
        return newsRepository.getNewsById(id)
    }

}