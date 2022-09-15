package com.syyamnoor.mytimestopstories.ui.list

import CoroutineTestRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.usecases.GetNewsUseCase
import com.syyamnoor.mytimestopstories.utils.DataState
import getOrAwaitValue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class ItemListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var getNews: GetNewsUseCase
    private lateinit var itemListViewModel: ItemListViewModel

    @Before
    fun setup() {
        getNews = mockk(relaxed = true)
        itemListViewModel = ItemListViewModel(getNews)
    }

    @Test
    fun `initializing item list viewmodel fetches data`() = runBlockingTest {
        verify(exactly = 1) { getNews(any(), any()) }
    }

    @Test
    fun `search with blank query changes query in state to null`() = runBlockingTest {
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.Search("      "))
        val value = itemListViewModel.listUiState.getOrAwaitValue()
        assertThat(value.query).isNull()
    }

    @Test
    fun `search with new query changes query in state to that value`() = runBlockingTest {
        val newQuery = "some query"
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.Search(newQuery))
        val value = itemListViewModel.listUiState.getOrAwaitValue()
        assertThat(value.query).isEqualTo(newQuery)
    }

    @Test
    fun `same id multiple times on tablet does not change id of viewed news in ui state`() {
        val newsId = 999L
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ViewNews(newsId))
        val initialUi = itemListViewModel.listUiState.getOrAwaitValue()
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ViewNews(newsId))
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ViewNews(newsId))
        val finalUi = itemListViewModel.listUiState.getOrAwaitValue()
        assertThat(initialUi).isSameInstanceAs(finalUi)
    }

    @Test
    fun `id on phone creates new ViewDetailEvent`() {
        val newsId = 1L
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ViewNews(newsId))
        val singleEvent = itemListViewModel.singleEventState.getOrAwaitValue()
        assertThat(singleEvent).isInstanceOf(ItemListViewModel.ListSingleEvent.ViewDetail::class.java)
        assertThat((singleEvent as ItemListViewModel.ListSingleEvent.ViewDetail).id).isEqualTo(
            newsId
        )
    }

    @Test
    fun `same id on phone multiple times creates different ViewDetailEvent`() {
        val newsId = 1L
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ViewNews(newsId))
        val initialEvent = itemListViewModel.singleEventState.getOrAwaitValue()
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ViewNews(newsId))
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ViewNews(newsId))
        val finalEvent = itemListViewModel.singleEventState.getOrAwaitValue()
        assertThat(initialEvent).isNotSameInstanceAs(finalEvent)
    }

    @Test
    fun `consume single event sets single event to null`() {
        itemListViewModel.performEvent(ItemListViewModel.ListUiEvent.ConsumeSingleEvent)
        val singleEvent = itemListViewModel.singleEventState.getOrAwaitValue()
        assertThat(singleEvent).isNull()
    }


}
