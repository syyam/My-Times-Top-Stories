package com.syyamnoor.mytimestopstories.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.usecases.GetNewsUseCase
import com.syyamnoor.mytimestopstories.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class ItemListViewModel @Inject constructor(
    private val getNews: GetNewsUseCase,
) : ViewModel() {

    private val _uiState = MutableLiveData(ListUiState())
    val listUiState: LiveData<ListUiState>
        get() = _uiState

    private val _singleEventState = MutableLiveData<ListSingleEvent?>()
    val singleEventState: LiveData<ListSingleEvent?>
        get() = _singleEventState

    private var job: Job? = null

    init {
        performQuery()
    }

    fun performEvent(listUiEvent: ListUiEvent) {
        val uiStateValue = _uiState.value ?: ListUiState()
        val value: String
        when (listUiEvent) {
            is ListUiEvent.Search -> {
                val newQuery = if (listUiEvent.query == null || listUiEvent.query.isBlank()) null
                else listUiEvent.query.trim()
                // Nothing has changed
                if (newQuery == uiStateValue.query)
                    return

                _uiState.value = uiStateValue.copy(query = newQuery)
                uiStateValue.value?.let { performQuery(it) }
            }
            is ListUiEvent.SelectSection -> {

                value = listUiEvent.column.lowercase()

                val sameTypeAsCurrent: Boolean = uiStateValue.value.equals(value)

                // Nothing has changed
                if (sameTypeAsCurrent)
                    return

                _uiState.value = uiStateValue.copy(value = value)
                performQuery(value)
            }
            is ListUiEvent.Refresh -> {
                uiStateValue.value?.let { performQuery(it) }
            }

            is ListUiEvent.ViewNews -> viewSingleNews(listUiEvent.id)
            is ListUiEvent.ConsumeSingleEvent -> consumeSingleEvent()
        }
    }

    private fun viewSingleNews(id: Long) {
        _singleEventState.value = ListSingleEvent.ViewDetail(id)
    }

    private fun consumeSingleEvent() {
        _singleEventState.value = null
    }

    private fun performQuery(value: String = "home") {
        // Stop previous emissions of data
        job?.cancel()

        val uiStateValue = _uiState.value ?: ListUiState()
        job = viewModelScope.launch {
            getNews(uiStateValue.query, value)
                // Wait half a second in case user is still typing
                .debounce(600)
                .collectLatest {
                    when (it) {
                        // Watch for database changes of the results
                        is DataState.Success -> {
                            it.data.collectLatest { list ->
                                _uiState.value = uiStateValue.copy(result = DataState.Success(list))
                            }
                        }
                        // Watch for database changes of the results if they are available
                        is DataState.Failure -> {
                            if (it.data != null) {
                                it.data.collectLatest { list ->
                                    _uiState.value = uiStateValue.copy(
                                        result = DataState.Failure(
                                            it.throwable,
                                            list
                                        )
                                    )
                                }
                            } else {
                                _uiState.value = uiStateValue.copy(
                                    result = DataState.Failure(
                                        it.throwable,
                                        null
                                    )
                                )
                            }
                        }
                        // Watch for database changes of results if they are available
                        is DataState.Loading -> {
                            if (it.data != null) {
                                it.data.collectLatest { list ->
                                    _uiState.value =
                                        uiStateValue.copy(result = DataState.Loading(list))
                                }
                            } else {
                                _uiState.value = uiStateValue.copy(result = DataState.Loading())
                            }
                        }
                    }
                }
        }
    }

    sealed class ListUiEvent {
        data class Search(val query: String?) : ListUiEvent()
        data class SelectSection(val column: String) : ListUiEvent()

        data class ViewNews(val id: Long) : ListUiEvent()
        object Refresh : ListUiEvent()
        object ConsumeSingleEvent : ListUiEvent()
    }

    sealed class ListSingleEvent {
        data class ShowSnackbar(val message: String) : ListSingleEvent()
        data class ViewDetail(val id: Long) : ListSingleEvent()
    }

    data class ListUiState(
        val result: DataState<List<News>> = DataState.Loading(),
        val query: String? = null,
        val value: String? = null,
    )
}