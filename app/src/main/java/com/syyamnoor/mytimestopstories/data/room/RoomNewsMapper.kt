package com.syyamnoor.mytimestopstories.data.room

import com.syyamnoor.mytimestopstories.data.room.joins.NewsInDbWithNewsImagesInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsImageInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb
import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.models.NewsImage
import com.syyamnoor.mytimestopstories.domain.utils.EntityDomainMapper
import javax.inject.Inject

class RoomNewsMapper
@Inject constructor() : EntityDomainMapper<NewsInDbWithNewsImagesInDb, News>() {
    override fun entityToDomain(entity: NewsInDbWithNewsImagesInDb): News {
        val images = mutableListOf<NewsImage>()
        for (media in entity.newsImageInDb) {
            images.add(NewsImage(media.caption, media.copyright, media.url))
        }
        val actualEntity = entity.newsInDb
        return News(
            actualEntity.id,
            actualEntity.title,
            actualEntity.section,
            actualEntity.newsAbstract,
            actualEntity.publishDate,
            actualEntity.category,
            actualEntity.author,
            actualEntity.url,
            images
        )
    }

    override fun domainToEntity(domain: News): NewsInDbWithNewsImagesInDb {
        val newsInDb = NewsInDb(
            domain.id,
            domain.title,
            domain.section,
            domain.newsAbstract,
            domain.publishDate,
            domain.category,
            domain.author,
            domain.url
        )
        val newsImagesInDb = mutableListOf<NewsImageInDb>()
        for (newsImage in domain.images) {
            newsImagesInDb.add(
                NewsImageInDb(
                    id = 0,
                    newsId = domain.id,
                    caption = newsImage.caption,
                    copyright = newsImage.copyright,
                    url = newsImage.url
                )
            )
        }
        return NewsInDbWithNewsImagesInDb(newsInDb, newsImagesInDb)
    }
}