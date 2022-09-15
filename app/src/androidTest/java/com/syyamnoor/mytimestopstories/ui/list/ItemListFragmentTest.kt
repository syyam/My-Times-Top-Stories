package com.syyamnoor.mytimestopstories.ui.list

import ApiResponses.failedResponse
import ApiResponses.successResponse
import ApiResponses.successResponseNullBody
import CoroutineTestRule
import android.content.Context
import android.view.KeyEvent
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.widget.SearchView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.syyamnoor.mytimestopstories.ToastMatcher
import com.syyamnoor.mytimestopstories.R
import com.syyamnoor.mytimestopstories.data.retrofit.NyTimesApi
import com.syyamnoor.mytimestopstories.data.room.NewsDatabase
import com.syyamnoor.mytimestopstories.data.room.RoomNewsMapper
import com.syyamnoor.mytimestopstories.data.room.daos.NewsDao
import com.syyamnoor.mytimestopstories.launchFragmentInHiltContainer
import com.syyamnoor.mytimestopstories.utils.Utils.createRandomNews
import com.syyamnoor.mytimestopstories.utils.isConnectedToInternet
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.hamcrest.CoreMatchers.*
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import java.util.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class ItemListFragmentTest {

    @get: Rule(order = 0)
    val hiltAndroidRule = HiltAndroidRule(this)

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val coroutineTestRule = CoroutineTestRule()

    @Inject
    lateinit var newsDao: NewsDao

    @Inject
    lateinit var nyTimesApi: NyTimesApi

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var newsDatabase: NewsDatabase

    private val roomNewsMapper = RoomNewsMapper()

    @Before
    fun setup() {
        hiltAndroidRule.inject()
        Intents.init()
    }

    @Test
    fun pressingBackOnceWhenDrawerIsClosed_showsClickAgainToast() {
        launchFragmentInHiltContainer<ItemListFragment>()
        pressBack()

        val isToastDisplayed: () -> Boolean = {
            try {
                isToastMessageDisplayed(R.string.press_back_again)
                true
            } catch (e: NoMatchingViewException) {
                false
            }
        }

        // Slow devices might need some time
        for (x in 0..9) {
            sleep(200)
            if (isToastDisplayed()) {
                break
            }
        }

    }

    @Test
    fun successfulLoad_hidesFailureState() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories() } coAnswers { successResponse }
        createRandomNewsWithIdsIndex(5)
        launchFragmentInHiltContainer<ItemListFragment>()
        onView(
            allOf(
                withId(R.id.textView_failureMessage),
                hasSibling(withId(R.id.button_failureRetry)),
            )
        ).check(matches(`is`(not(isDisplayed()))))
    }

    @Test
    fun successfulLoadWithData_displaysRecyclerViewAndHidesEmptyState() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories() } returns successResponse
        createRandomNewsWithIdsIndex(5)
        launchFragmentInHiltContainer<ItemListFragment>()
        onView(
            allOf(
                withId(R.id.recyclerView),
            )
        ).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.layoutEmptyState),
                hasSibling(withId(R.id.recyclerView))
            )
        ).check(matches(not(isDisplayed())))
    }

    @Test
    fun successfulLoadWithNoData_hidesRecyclerViewAndShowsEmptyState() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories() } returns successResponseNullBody
        launchFragmentInHiltContainer<ItemListFragment>()
        onView(
            allOf(
                withId(R.id.recyclerView),
            )
        ).check(matches(not(isDisplayed())))
        onView(
            allOf(
                withId(R.id.layoutEmptyState),
                hasSibling(withId(R.id.recyclerView))
            )
        ).check(matches(isDisplayed()))
    }


    @Test
    fun failedLoadWithData_showsFailureState() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories() } coAnswers { failedResponse }
        createRandomNewsWithIdsIndex(5)
        launchFragmentInHiltContainer<ItemListFragment>()
        onView(
            allOf(
                withId(R.id.textView_failureMessage),
                hasSibling(withId(R.id.button_failureRetry)),
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun failedLoadWithoutData_hidesFailureState() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories("home") } coAnswers { failedResponse }
        launchFragmentInHiltContainer<ItemListFragment>()
        onView(
            allOf(
                withId(R.id.textView_failureMessage),
                hasSibling(withId(R.id.button_failureRetry)),
            )
        ).check(matches(`is`(not(isDisplayed()))))
    }


    @Test
    fun failedLoad_hidesProgressBars() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories() } coAnswers { failedResponse }
        launchFragmentInHiltContainer<ItemListFragment>()

        onView(
            allOf(
                withId(R.id.linearProgressIndicator),
                hasSibling(withId(R.id.swipeRefreshLayout)),

            )
        ).check(matches(`is`(not(isDisplayed()))))
    }

    @Test
    fun failedLoadWithData_displaysRecyclerViewAndHidesEmptyState() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories() } returns failedResponse
        createRandomNewsWithIdsIndex(5)
        launchFragmentInHiltContainer<ItemListFragment>()
        onView(
            allOf(
                withId(R.id.recyclerView),
            )
        ).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.layoutEmptyState),
                hasSibling(withId(R.id.recyclerView))
            )
        ).check(matches(not(isDisplayed())))
    }

    @Test
    fun failedLoadWithNoData_hidesRecyclerViewAndShowsEmptyState() {
        every { context.isConnectedToInternet() } returns true
        coEvery { nyTimesApi.getTopStories() } returns failedResponse
        launchFragmentInHiltContainer<ItemListFragment>()
        onView(
            allOf(
                withId(R.id.recyclerView),
            )
        ).check(matches(not(isDisplayed())))
        onView(
            allOf(
                withId(R.id.layoutEmptyState),
                hasSibling(withId(R.id.recyclerView))
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun viewDetailEventOnMobile_navigatesToDetail() {
        createRandomNewsWithIdsIndex(5, false)
        val mockNavController = mockk<NavController>(relaxed = true)
        launchFragmentInHiltContainer<ItemListFragment> {
            view?.let {

                Navigation.setViewNavController(it, mockNavController)

            }
        }
        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<NewsAdapter.NewsViewHolder>(
                    0,
                    click()
                )
            )
        verify { mockNavController.navigate(ItemListFragmentDirections.showItemDetail(1)) }

    }

    @Test
    fun clickingSearch_displaysSearchView() {
        launchFragmentInHiltContainer<ItemListFragment>()
        try {
            openActionBarOverflowOrOptionsMenu(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        } catch (e: Exception) {
            //This is normal. Maybe we don't have overflow menu.
        }
        onView(
            allOf(
                anyOf(
                    withText(R.string.search),
                    withId(R.id.action_search)
                ),
                isDisplayed()
            )
        ).perform(click())
        onView(
            allOf(
                isDescendantOfA(withClassName(`is`(SearchView::class.java.name))),
                isDisplayed(),
                isAssignableFrom(EditText::class.java)
            )
        )
            .perform(
                clearText(),
                typeText("enter the text"),
                pressKey(KeyEvent.KEYCODE_ENTER)
            )
    }

    @Test
    fun searching_filtersItems() {
        val searchQuery =
            "Syyam uwu"
        createRandomNewsWithIdsIndex(20)
        val domainToEntity =
            roomNewsMapper.domainToEntity(createRandomNews().copy(id = 100, title = searchQuery))
        newsDao.insert(domainToEntity.newsInDb)

        launchFragmentInHiltContainer<ItemListFragment>()

        onView(
            anyOf(
                withText(R.string.search),
                withId(R.id.action_search)
            )
        ).perform(click())
        onView(
            allOf(
                isDescendantOfA(withClassName(`is`(SearchView::class.java.name))),
                isDisplayed(),
                isAssignableFrom(EditText::class.java)
            )
        )
            .perform(
                clearText(),
                typeText(searchQuery),
                pressKey(KeyEvent.KEYCODE_ENTER)
            )

        onView(withId(R.id.recyclerView)).check(matches(hasChildCount(1)))

    }

    @Test
    fun clickingSort_displaysSortDialog() {
        launchFragmentInHiltContainer<ItemListFragment>()
        try {
            openActionBarOverflowOrOptionsMenu(
                InstrumentationRegistry.getInstrumentation().targetContext
            )
        } catch (e: Exception) {
            //This is normal. Maybe we don't have overflow menu.
        }
        onView(
            allOf(
                anyOf(
                    withText(R.string.section),
                    withId(R.id.action_section)
                ),
                isDisplayed()
            )
        ).perform(click())
        onView(
            allOf(
                isDescendantOfA(withClassName(`is`(RadioGroup::class.java.name))),
                withText(R.string.arts)
            )
        ).check(matches(isDisplayed()))
    }

    @After
    fun teardown() {
        Intents.release()
        if (newsDatabase.isOpen)
            newsDatabase.close()
    }

    private fun createRandomNewsWithIdsIndex(count: Int, randomDate: Boolean = true) {
        var date = Date()
        newsDao.insert((1..count).map {
            val createRandomNews = createRandomNews()
            if (randomDate)
                date = createRandomNews.publishDate
            val domainToEntity =
                roomNewsMapper.domainToEntity(
                    createRandomNews.copy(
                        id = it.toLong(),
                        publishDate = date
                    )
                )
            domainToEntity.newsInDb
        })
    }

    private fun isToastMessageDisplayed(textId: Int) {
        onView(withText(textId)).inRoot(ToastMatcher.isToast()).check(matches(isDisplayed()))
    }
}