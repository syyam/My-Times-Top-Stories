package com.syyamnoor.mytimestopstories.data.retrofit

import com.syyamnoor.mytimestopstories.data.retrofit.models.NewsItemResult
import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.models.NewsImage
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

class RetrofitNewsMapperTest {

    private lateinit var retrofitNewsMapper: RetrofitNewsMapper
    private lateinit var newsRetro: NewsItemResult
    private lateinit var news: News

    @Before
    fun setUp() {
        retrofitNewsMapper = RetrofitNewsMapper()

        val newsId = 0L
        val title = "title"
        val newsAbstract = "Abstract"
        val publishDate = Date(100000)
        val section = "Category"
        val author = "Author"
        val source = "Source"
        val newsUrl = "link"
        val caption = "caption"
        val copyright = "2022"
        val imageUrl = "link"

        // Initialize news in Retrofit
        newsRetro = NewsItemResult(
            newsId,
            newsAbstract,
            section,
            author,
            emptyList(),
            emptyList(),
            section,
            "caption",
            source,
            emptyList(),
            emptyList(),
            emptyList(),
            publishDate,
            section,
            source,
            "",
            title,
            "",
            copyright,
            "",
        )

        // Initialize news
        val newsImages = listOf(
            NewsImage(
                caption,
                copyright,
                imageUrl,
            )
        )

        news = News(
            newsId,
            title,
            newsAbstract,
            title,
            publishDate,
            author,
            source,
            newsUrl,
            newsImages
        )
    }

    @Test
    fun `news in server with no subsection is mapped to news correctly`() {
        val entityToTest = newsRetro.copy(subsection = "")
        val domainToTest = news.copy(category = entityToTest.section)
        val mappedNews = retrofitNewsMapper.entityToDomain(entityToTest)
        assertThat(mappedNews).isEqualTo(domainToTest)
    }

    @Test
    fun `news in server with subsection is mapped to news correctly`() {
        val entityToTest = newsRetro.copy(subsection = "Some Subsection")
        val domainToTest =
            news.copy(category = entityToTest.section + " " + entityToTest.subsection)
        val mappedNews = retrofitNewsMapper.entityToDomain(entityToTest)
        assertThat(mappedNews).isEqualTo(domainToTest)
    }


    @Test
    fun `news is mapped to news in server correctly`() {
        val entityToTest = newsRetro.copy(
            section = news.category,
            subsection = "",
            published_date = Date(0)
        )
        val mappedNewsInServer = retrofitNewsMapper.domainToEntity(news)
        assertThat(mappedNewsInServer).isEqualTo(entityToTest)
    }


    @Test
    fun `list of news in server is mapped to news correctly`() {
        val entityToTest = newsRetro.copy(
            section = news.category,
            subsection = ""
        )
        val entitiesList = listOf(entityToTest)
        val mappedNews = retrofitNewsMapper.entityListToDomainList(entitiesList)
        val domainsList = listOf(news)
        assertThat(mappedNews).isEqualTo(domainsList)
    }


    @Test
    fun `list of news is mapped to news in server correctly`() {
        val entityToTest = newsRetro.copy(
            section = news.section,
        )
        val domainsList = listOf(news)
        val mappedNewsInServer = retrofitNewsMapper.domainListToEntityList(domainsList)
        val entitiesList = listOf(entityToTest)
        assertThat(mappedNewsInServer).isEqualTo(entitiesList)
    }

}