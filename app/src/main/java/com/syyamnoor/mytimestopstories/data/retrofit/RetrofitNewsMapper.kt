package com.syyamnoor.mytimestopstories.data.retrofit

import com.syyamnoor.mytimestopstories.data.retrofit.models.NewsItemRetro1
import com.syyamnoor.mytimestopstories.domain.models.News
import com.syyamnoor.mytimestopstories.domain.models.NewsImage
import com.syyamnoor.mytimestopstories.domain.utils.EntityDomainMapper
import javax.inject.Inject

class RetrofitNewsMapper
@Inject constructor() : EntityDomainMapper<NewsItemRetro1, News>() {
    override fun entityToDomain(entity: NewsItemRetro1): News {
        val images = mutableListOf<NewsImage>()
        for (media in entity.multimedia) {
            if (media.type == "image") {
                val largestImageUrl = media.url
                images.add(NewsImage(media.caption, media.copyright, largestImageUrl))
            }
        }
        return News(
            entity.published_date.time,
            entity.title,
            entity.section,
            entity.abstract,
            entity.published_date,
            (entity.section + " " + entity.subsection).trim(),
            entity.byline,
            entity.url,
            images
        )
    }

    //
    override fun domainToEntity(domain: News): NewsItemRetro1 {
        return NewsItemRetro1(
            domain.id,
            "",
            domain.publishDate.toString(),
            "",
            emptyList(),
            emptyList(),
            "",
            "",
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            domain.publishDate,
            "",
            domain.category,
            "",
            domain.title,
            "",
            "",
            ""
        )
    }
}