package com.syyamnoor.mytimestopstories.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.syyamnoor.mytimestopstories.data.room.daos.NewsDao
import com.syyamnoor.mytimestopstories.data.room.daos.NewsImageDao
import com.syyamnoor.mytimestopstories.data.room.models.NewsImageInDb
import com.syyamnoor.mytimestopstories.data.room.models.NewsInDb
import com.syyamnoor.mytimestopstories.data.room.utils.DateConverter

@Database(entities = [NewsInDb::class, NewsImageInDb::class], version = 4, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract val newsDao: NewsDao
    abstract val newsImageDao: NewsImageDao

    companion object {
        const val DATABASE_NAME = "news_db"
    }
}