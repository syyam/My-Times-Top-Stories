package com.syyamnoor.mytimestopstories.data.room

import androidx.test.filters.SmallTest
import com.syyamnoor.mytimestopstories.data.room.joins.NewsInDbWithNewsImagesInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsImageInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb
import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.models.NewsImage
import com.google.common.truth.Truth.assertThat

import org.junit.Before
import org.junit.Test
import java.util.*

@SmallTest
class RoomNewsMapperTest {

    private lateinit var roomNewsMapper: RoomNewsMapper
    private lateinit var newsInDb: NewsInDbWithNewsImagesInDb
    private lateinit var news: News

    @Before
    fun setUp() {
        roomNewsMapper = RoomNewsMapper()

        val newsId = 0L
        val title = "title"
        val newsAbstract = "Abstract of the news"
        val publishDate = Date()
        val section = "Category"
        val author = "Author"
        val source = "Source"
        val newsUrl = "link"
        val newsImageId = 0L
        val caption = "caption"
        val copyright = "2022"
        val imageUrl = "link"

        // Initialize news in DB
        val newsImagesInDb = listOf(
            NewsImageInDb(
                newsImageId,
                newsId,
                caption,
                copyright,
                imageUrl
            )
        )
        newsInDb = NewsInDbWithNewsImagesInDb(
            NewsInDb(
                newsId,
                title,
                section,
                newsAbstract,
                publishDate,
                section,
                author,
                newsUrl
            ), newsImagesInDb
        )

        // Initialize news
        val newsImages = listOf(
            NewsImage(
                caption,
                copyright,
                imageUrl
            )
        )

        news = News(
            newsId,
            title,
            section,
            newsAbstract,
            publishDate,
            section,
            author,
            newsUrl,
            newsImages
        )
    }

    @Test
    fun `news in db is mapped to news correctly`() {
        val mappedNews = roomNewsMapper.entityToDomain(newsInDb)
        assertThat(mappedNews).isEqualTo(news)
    }

    @Test
    fun `news is mapped to news in db correctly`() {
        val mappedNewsInDb = roomNewsMapper.domainToEntity(news)
        assertThat(mappedNewsInDb).isEqualTo(newsInDb)
    }

    @Test
    fun `list of news in db is mapped to news correctly`() {
        val entitiesList = listOf(newsInDb)
        val mappedNews = roomNewsMapper.entityListToDomainList(entitiesList)
        val domainList = listOf(news)
        assertThat(mappedNews).isEqualTo(domainList)
    }

    @Test
    fun `list of news is mapped to news in db correctly`() {
        val domainList = listOf(news)
        val mappedNewsInDb = roomNewsMapper.domainListToEntityList(domainList)
        val entitiesListOf = listOf(newsInDb)
        assertThat(mappedNewsInDb).isEqualTo(entitiesListOf)
    }

}