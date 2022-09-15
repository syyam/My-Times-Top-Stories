package com.syyamnoor.mytimestopstories.domain.usecases

import com.syyamnoor.mytimestopstories.data.repositories.NewsRepositoryImpl
import com.syyamnoor.mytimestopstories.utils.DataState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetNewsTest {

    private lateinit var getNews: GetNewsUseCase
    private lateinit var newsRepositoryImpl: NewsRepositoryImpl

    @Before
    fun setup() {
        newsRepositoryImpl = mockk(relaxed = true)
        getNews = GetNewsUseCase(newsRepositoryImpl)

        coEvery {
            newsRepositoryImpl.getNews(
                any(),
                any()
            )
        } returns flow { emit(DataState.Loading()) }
    }

    @Test
    fun `calls repository get news with same query`() = runBlockingTest {
        val query = null
        getNews(query, "home").last()
        coVerify { newsRepositoryImpl.getNews(query, "home") }
    }
}