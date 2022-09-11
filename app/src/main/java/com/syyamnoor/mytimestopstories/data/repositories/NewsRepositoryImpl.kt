package com.syyamnoor.mytimestopstories.data.repositories

import android.content.Context
import com.syyamnoor.mytimestopstories.R
import com.syyamnoor.mytimestopstories.data.retrofit.NyTimesApi
import com.syyamnoor.mytimestopstories.data.retrofit.RetrofitNewsMapper
import com.syyamnoor.mytimestopstories.data.room.RoomNewsMapper
import com.syyamnoor.mytimestopstories.data.room.daos.NewsDao
import com.syyamnoor.mytimestopstories.data.room.daos.NewsImageDao
import com.syyamnoor.mytimestopstories.data.room.joins.NewsInDbWithNewsImagesInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsImageInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb
import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.repositories.NewsRepository
import com.syyamnoor.mytimestopstories.utils.AppDispatchers
import com.syyamnoor.mytimestopstories.utils.DataState
import com.syyamnoor.mytimestopstories.utils.isConnectedToInternet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException

@ExperimentalCoroutinesApi
class NewsRepositoryImpl @Inject constructor(
    private val retrofitNewsMapper: RetrofitNewsMapper,
    private val nyTimesApi: NyTimesApi,
    private val roomNewsMapper: RoomNewsMapper,
    private val newsDao: NewsDao,
    private val newsImageDao: NewsImageDao,
    private val dispatchers: AppDispatchers,
    private val context: Context
) : NewsRepository {

    override fun getNews(query: String?, value: String): Flow<DataState<Flow<List<News>>>> =
        flow {
            // Display loading with no items
            emit(DataState.Loading())

            val dbFlow: Flow<List<NewsInDbWithNewsImagesInDb>> =
                when {
                    (query != null) -> newsDao.getAllNewsWithQuery("%$query%")
                        .distinctUntilChanged()
                    else -> newsDao.getAllNews().distinctUntilChanged()
                }

            val newsItems = dbFlow.mapLatest {
                roomNewsMapper.entityListToDomainList(it)
            }


            // Display loading with current items
            emit(DataState.Loading(newsItems))

            var isSuccessful: Boolean
            var throwable: Throwable? = null
            val genericException = Exception(context.getString(R.string.geeric_error))
            val internetException = Exception(context.getString(R.string.internet_connection_error))
            if (context.isConnectedToInternet()) {
                try {
                    newsDao.deleteAllNews()
                    val viewedNews = nyTimesApi.getTopStories(value)
                    val newsResponse = viewedNews.body()
                    // API call was successful
                    if (viewedNews.isSuccessful && newsResponse != null) {
                        isSuccessful = true
                        val domainModels =
                            retrofitNewsMapper.entityListToDomainList(newsResponse.results)
                        val roomModels = roomNewsMapper.domainListToEntityList(domainModels)

                        // Create lists of the items and images to insert at once
                        val newsModelItems = mutableListOf<NewsInDb>()
                        val newsImageModelItems = mutableListOf<NewsImageInDb>()
                        roomModels.forEach {
                            newsModelItems.add(it.newsInDb)
                            newsImageModelItems.addAll(it.newsImageInDb)
                        }
                        // Cache the data in local storage
                        newsDao.upsert(newsModelItems)
                        newsImageDao.upsert(newsImageModelItems)
                    } else if (viewedNews.code() == 429) {
                        isSuccessful = false
                        throwable = Exception(context.getString(R.string.too_many_requests))
                    } else if (viewedNews.errorBody() != null) {
                        isSuccessful = false
                        throwable = Exception(viewedNews.errorBody().toString())
                    } else {
                        isSuccessful = false
                        throwable = genericException
                    }
                } catch (e: SSLHandshakeException) {
                    isSuccessful = false
                    throwable = internetException
                } catch (e: Exception) {
                    isSuccessful = false
                    throwable = e
                }
            } else {
                isSuccessful = false
                throwable = internetException
            }

            // Emit result with current value in DB
            emit(
                if (isSuccessful)
                    DataState.Success(newsItems)
                else DataState.Failure(
                    throwable ?: genericException, newsItems
                )
            )
        }.flowOn(dispatchers.io())

    override suspend fun getNewsById(id: Long): Flow<DataState<News?>> = channelFlow {
        send(DataState.Loading())
        newsDao.getNewsById(id).distinctUntilChanged().collectLatest {
            it?.let {
                send(DataState.Success(roomNewsMapper.entityToDomain(it)))
            } ?: send(
                DataState.Failure(
                    Exception(
                        context.getString(
                            R.string.news_with_id_not_found,
                            id.toInt()
                        )
                    )
                )
            )
        }
    }

}