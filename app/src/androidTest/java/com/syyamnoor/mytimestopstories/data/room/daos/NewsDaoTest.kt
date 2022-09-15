package com.syyamnoor.mytimestopstories.data.room.daos

import CoroutineTestRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.syyamnoor.mytimestopstories.data.room.NewsDatabase
import com.syyamnoor.mytimestopstories.data.room.RoomNewsMapper
import com.syyamnoor.mytimestopstories.utils.Utils.createRandomNews
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class NewsDaoTest {

    @get: Rule
    val instantTaskExecutor = InstantTaskExecutorRule()

    @get: Rule(order = 0)
    val hiltAndroidRule = HiltAndroidRule(this)

    @get: Rule
    val coroutineTestRule = CoroutineTestRule()

    @Inject
    lateinit var newsDao: NewsDao

    @Inject
    lateinit var roomNewsMapper: RoomNewsMapper

    @Inject
    lateinit var newsDatabase: NewsDatabase

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
    }

    @Test
    fun queryGetNews_filtersResults() = runBlockingTest {
        insertRandomNewsToDb()
        val queryString = "when"
        val job = launch {
            newsDao.insert(roomNewsMapper.domainToEntity(createRandomNews().copy(author = "$queryString other values")).newsInDb)
            val last = newsDao.getAllNews().last()
            last.forEach { newsInDbWithImage ->
                val newsInDb = newsInDbWithImage.newsInDb
                assertThat(last.size).isGreaterThan(0)
                assertThat(
                    (
                            listOf(
                                newsInDb.author,
                                newsInDb.category,
                                newsInDb.newsAbstract,
                                newsInDb.title
                            )
                                .joinToString { " " })
                )
                    .contains(queryString)
            }
        }
        job.cancel()
    }

    @Test
    fun queryGetNews_returnsEmptyList() = runBlockingTest {
        insertRandomNewsToDb()
        val job = launch {
            val last = newsDao.getAllNews().last()
            assertThat(last.size).isEqualTo(0)
        }
        job.cancel()
    }

    @Test
    fun deleteNews_returnsEmptyList() = runBlockingTest {
        insertRandomNewsToDb()
        val job = launch {
            newsDao.deleteAllNews()
            val last = newsDao.getAllNews().last()
            assertThat(last.size).isEqualTo(0)
        }
        job.cancel()
    }

    @Test
    fun getNewsWithWrongId_returnsNull() = runBlockingTest {
        val newsId = 1L
        val insertedNewsInDb =
            roomNewsMapper.domainToEntity(createRandomNews().copy(id = newsId)).newsInDb
        val job = launch {
            newsDao.insert(insertedNewsInDb)
            val last = newsDao.getNewsById(2).last()
            val fetchedNews = last?.newsInDb
            assertThat(fetchedNews).isNull()
        }
        job.cancel()
    }

    private fun insertRandomNewsToDb(count: Int = 50) {
        (1..count).map { createRandomNews() }
            .forEach { newsDao.insert(roomNewsMapper.domainToEntity(it).newsInDb) }
    }

    @After
    fun teardown() {
        if (newsDatabase.isOpen)
            newsDatabase.close()
    }

}