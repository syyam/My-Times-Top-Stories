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
class GetNewsByIdTest {

    private lateinit var getNewsById: GetNewsById
    private lateinit var newsRepositoryImpl: NewsRepositoryImpl

    @Before
    fun setup() {
        newsRepositoryImpl = mockk(relaxed = true)
        getNewsById = GetNewsById(newsRepositoryImpl)

        coEvery { newsRepositoryImpl.getNewsById(any()) } returns flow { emit(DataState.Loading()) }
    }

    @Test
    fun `calls repository get news by id with same id`() = runBlockingTest {
        val id: Long = 1
        getNewsById(id).last()
        coVerify { newsRepositoryImpl.getNewsById(id) }
    }
}