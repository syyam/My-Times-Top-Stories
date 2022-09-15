package com.syyamnoor.mytimestopstories.ui.detail

import CoroutineTestRule
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.SmallTest
import com.syyamnoor.mytimestopstories.R
import com.syyamnoor.mytimestopstories.data.room.NewsDatabase
import com.syyamnoor.mytimestopstories.data.room.RoomNewsMapper
import com.syyamnoor.mytimestopstories.data.room.daos.NewsDao
import com.syyamnoor.mytimestopstories.data.room.daos.NewsImageDao
import com.syyamnoor.mytimestopstories.launchFragmentInHiltContainer
import com.syyamnoor.mytimestopstories.utils.Utils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class ItemDetailFragmentTest {

    @get: Rule(order = 0)
    val hiltAndroidRule = HiltAndroidRule(this)

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineTestRule = CoroutineTestRule()

    @Inject
    lateinit var newsDao: NewsDao

    @Inject
    lateinit var newsImageDao: NewsImageDao

    @Inject
    lateinit var newsDatabase: NewsDatabase

    private val roomNewsMapper = RoomNewsMapper()

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun successfulLoadWithData_displaysDetails() {
        createNews()
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(
            Matchers.allOf(
                withId(R.id.linearLayout_details)
            )
        ).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun successfulLoadWithData_hidesEmptyState() {
        createNews()
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(
            Matchers.allOf(
                withId(R.id.layoutEmptyState),
                hasSibling(withId((R.id.linearLayout_details)))
            )
        ).check(ViewAssertions.matches(not(isDisplayed())))
    }

    @Test
    fun successfulLoadWithDataWithImages_displaysRecyclerView() {
        createNews()
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(withId(R.id.recyclerView_images)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun successfulLoadWithDataWithoutImages_hidesRecyclerView() {
        createNews(false)
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(withId(R.id.recyclerView_images)).check(ViewAssertions.matches(not(isDisplayed())))
    }

    @Test
    fun successfulLoad_hidesFailureState() {
        createNews()
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(
            Matchers.allOf(
                withId(R.id.layoutFailureState),
            )
        ).check(ViewAssertions.matches(Matchers.`is`(not(isDisplayed()))))
    }

    @Test
    fun failedLoad_hidesProgressBars() {
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(
            Matchers.allOf(
                withId(R.id.linearProgressIndicator),
            )
        ).check(ViewAssertions.matches(Matchers.`is`(not(isDisplayed()))))
    }

    @Test
    fun failedLoadWithoutData_hidesFailureState() {
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(
            Matchers.allOf(
                withId(R.id.layoutFailureState),
            )
        ).check(ViewAssertions.matches(Matchers.`is`(not(isDisplayed()))))
    }

    @Test
    fun failedLoadWithoutData_displaysEmptyState() {
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(
            Matchers.allOf(
                withId(R.id.layoutEmptyState),
            )
        ).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun failedLoadWithoutData_hidesNewsDetails() {
        val fragmentArgs = Bundle()
        fragmentArgs.putLong("item_id", 1)
        launchFragmentInHiltContainer<ItemDetailFragment>(fragmentArgs)
        onView(
            Matchers.allOf(
                withId(R.id.linearLayout_details),
            )
        ).check(ViewAssertions.matches(Matchers.`is`(not(isDisplayed()))))
    }

    @After
    fun teardown() {
        if (newsDatabase.isOpen)
            newsDatabase.close()
    }

    private fun createNews(withImages: Boolean = true) {
        val randomNews = Utils.createRandomNews()
        val domainToEntity = roomNewsMapper.domainToEntity(randomNews.copy(id = 1))
        newsDao.insert(domainToEntity.newsInDb)
        if (withImages)
            newsImageDao.insert(domainToEntity.newsImageInDb)
    }
}