package com.syyamnoor.mytimestopstories.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import CoroutineTestRule
import com.syyamnoor.mytimestopstories.domain.usecases.GetNewsById
import getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import com.syyamnoor.mytimestopstories.utils.DataState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ItemDetailViewModelTest {

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var getNewsById: GetNewsById
    private lateinit var itemDetailViewModel: ItemDetailViewModel

    @Before
    fun setup() {
        getNewsById = mockk(relaxed = true)
        itemDetailViewModel = ItemDetailViewModel(getNewsById)
    }

    @Test
    fun `same id multiple times does not perform the fetch more than once`() = runBlockingTest {
        val id = 1L
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        coVerify(exactly = 1) { getNewsById(id) }
    }

    @Test
    fun `different id performs the fetch more than once`() = runBlockingTest {
        val id1 = 1L
        val id2 = 2L
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id1))
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id2))
        coVerify(exactly = 2) { getNewsById(any()) }
    }

    @Test
    fun `same id maintains ui state value`() = runBlockingTest {
        coEvery { getNewsById(any()) } returns flow { emit(DataState.Success(null)) }

        val id = 1L
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        val initialValue = itemDetailViewModel.uiState.getOrAwaitValue()
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id))
        val finalValue = itemDetailViewModel.uiState.getOrAwaitValue()
        assertThat(finalValue).isSameInstanceAs(initialValue)
    }

    @Test
    fun `different id changes ui state value`() = runBlockingTest {
        coEvery { getNewsById(any()) } returns flow { emit(DataState.Success(null)) }

        val id1 = 1L
        val id2 = 2L
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id1))
        val initialValue = itemDetailViewModel.uiState.getOrAwaitValue()
        itemDetailViewModel.performEvent(ItemDetailViewModel.DetailUiEvent.GetNews(id2))
        val finalValue = itemDetailViewModel.uiState.getOrAwaitValue()
        assertThat(finalValue).isNotSameInstanceAs(initialValue)
    }
}