package com.syyamnoor.mytimestopstories.data.repositories

import android.content.Context
import ApiResponses.failedResponse
import ApiResponses.successResponse
import ApiResponses.successResponseNullBody
import com.syyamnoor.mytimestopstories.data.retrofit.NyTimesApi
import com.syyamnoor.mytimestopstories.data.retrofit.RetrofitNewsMapper
import com.syyamnoor.mytimestopstories.data.room.RoomNewsMapper
import com.syyamnoor.mytimestopstories.data.room.daos.NewsDao
import com.syyamnoor.mytimestopstories.data.room.daos.NewsImageDao
import com.syyamnoor.mytimestopstories.data.room.joins.NewsInDbWithNewsImagesInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsImageInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb
import com.syyamnoor.mytimestopstories.utils.AppDispatchers
import com.syyamnoor.mytimestopstories.utils.DataState
import com.syyamnoor.mytimestopstories.utils.Utils.createRandomNews
import com.syyamnoor.mytimestopstories.utils.isConnectedToInternet
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsRepositoryImplTest {

    private val roomNewsMapper = RoomNewsMapper()
    private lateinit var newsRepositoryImpl: NewsRepositoryImpl
    private lateinit var nyTimesApi: NyTimesApi
    private lateinit var newsDao: NewsDao
    private lateinit var newsImageDao: NewsImageDao
    private lateinit var context: Context

    private val dispatchers: AppDispatchers = object : AppDispatchers {
        override fun main(): CoroutineDispatcher {
            return TestCoroutineDispatcher()
        }

        override fun default(): CoroutineDispatcher {
            return TestCoroutineDispatcher()
        }

        override fun io(): CoroutineDispatcher {
            return TestCoroutineDispatcher()
        }
    }

    @Before
    fun setup() {
        nyTimesApi = mockk(relaxed = true)
        newsDao = mockk(relaxed = true)
        newsImageDao = mockk(relaxed = true)
        context = mockk(relaxed = true)
        newsRepositoryImpl = NewsRepositoryImpl(
            RetrofitNewsMapper(),
            nyTimesApi,
            roomNewsMapper,
            newsDao,
            newsImageDao,
            dispatchers,
            context
        )

        coEvery { context.getString(any()) } returns "An Error Occurred"
        coEvery { context.isConnectedToInternet() } returns false
    }

    /**
     * getNews test cases
     * 1. Get News emits loading first
     * 2. Get news emits loading with data from local storage first
     * 3. The dao method queried could be
     *    a. getAllNewsWith query when query is not null
     *    b. getAllNews when query is null
     * 4. a. If connected to internet fetches from API
     *           i. If API response is successful and body is not null
     *           Emits Success
     *           Saves news items to database
     *           Saves news images to database
     *           ii. If an exception occurs or response body is null
     *           Emits failure
     *         b. If not connected emits Failure
     */

    @Test
    fun `get news starts in loading state`() = runBlockingTest {
        val first = newsRepositoryImpl.getNews("", "home").first()
        assertThat(first).isInstanceOf(DataState.Loading::class.java)
    }

    @Test
    fun `second emit is loading state and returns data`() = runBlockingTest {
        every { newsDao.getAllNews() } returns flow { getNewsInDbItems() }
        val second = newsRepositoryImpl.getNews("", "home").drop(1).first()
        assertThat(second).isInstanceOf(DataState.Loading::class.java)
        assertThat((second as DataState.Loading).data).isNotNull()
    }

    @Test
    fun `query not null calls get news with query on db and not get all news`() = runBlockingTest {
        newsRepositoryImpl.getNews("", "home").collect()
        coVerify { newsDao.getAllNews() wasNot called }
    }

    @Test
    fun `available network connection fetches from API`() = runBlockingTest {
        every { context.isConnectedToInternet() } returns true
        newsRepositoryImpl.getNews("", "home").collect()
        coVerify(exactly = 1) { nyTimesApi.getTopStories() }
    }

    @Test
    fun `unavailable network connection does not fetch from API`() = runBlockingTest {
        every { context.isConnectedToInternet() } returns false
        newsRepositoryImpl.getNews("", "home").collect()
        coVerify { nyTimesApi.getTopStories() wasNot called }
    }

    @Test
    fun `unavailable network returns failed state with data from local cache`() = runBlockingTest {
        every { context.isConnectedToInternet() } returns false
        every { newsDao.getAllNews() } returns flow { getNewsInDbItems() }
        val last = newsRepositoryImpl.getNews("", "home").last()
        assertThat(last).isInstanceOf(DataState.Failure::class.java)
        assertThat((last as DataState.Failure).data).isNotNull()
    }

    @Test
    fun `unsuccessful API request returns failure`() = runBlockingTest {
        every { context.isConnectedToInternet() } returns true
        every { newsDao.getAllNews() } returns flow { getNewsInDbItems() }
        coEvery { nyTimesApi.getTopStories() } returns failedResponse
        val last = newsRepositoryImpl.getNews("", "home").last()
        assertThat(last).isInstanceOf(DataState.Failure::class.java)
        assertThat((last as DataState.Failure).data).isNotNull()
    }

    @Test
    fun `successful API request with null body returns failure`() = runBlockingTest {
        every { context.isConnectedToInternet() } returns true
        every { newsDao.getAllNews() } returns flow { getNewsInDbItems() }
        coEvery { nyTimesApi.getTopStories() } returns successResponseNullBody
        val last = newsRepositoryImpl.getNews("", "home").last()
        assertThat(last).isInstanceOf(DataState.Failure::class.java)
        assertThat((last as DataState.Failure).data).isNotNull()
    }

    /**
     * getNewsById test cases
     * 1. First State is loading
     * 2. Get existing news returns success
     * 3. Get non-existing news returns failure
     */

    @Test
    fun `get news by id starts with loading with no news`() = runBlockingTest {
        val first = newsRepositoryImpl.getNewsById(1).first()
        assertThat(first).isInstanceOf(DataState.Loading::class.java)
        assertThat((first as DataState.Loading).data).isNull()
    }

    @Test
    fun `get an existing news returns success with the news`() = runBlockingTest {
        every { newsDao.getNewsById(any()) } returns getNewsInDbItems(1).asFlow()
        val last = newsRepositoryImpl.getNewsById(1).last()
        assertThat(last).isInstanceOf(DataState.Success::class.java)
        assertThat((last as DataState.Success).data).isNotNull()
    }

    @Test
    fun `get non existing news returns failure with no news`() = runBlockingTest {
        every { newsDao.getNewsById(any()) } returns flow { emit(null) }
        val last = newsRepositoryImpl.getNewsById(1).last()
        assertThat(last).isInstanceOf(DataState.Failure::class.java)
        assertThat((last as DataState.Failure).data).isNull()
    }

    private fun getNewsInDbItems(count: Int = 10): List<NewsInDbWithNewsImagesInDb> {
        return (1..count).map { roomNewsMapper.domainToEntity(createRandomNews()) }
    }
}