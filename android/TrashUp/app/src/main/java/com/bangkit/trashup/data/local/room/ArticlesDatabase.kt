package com.bangkit.trashup.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bangkit.trashup.data.local.entity.ArticlesFavEntity

@Database(entities = [ArticlesFavEntity::class], version = 2)
abstract class ArticlesDatabase: RoomDatabase() {
    abstract fun articlesDao(): ArticlesDao

    companion object {
        @Volatile
        private var instance: ArticlesDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): ArticlesDatabase {

            @Suppress("LocalVariableName") val MIGRATION = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {

                }
            }

            if (instance == null) {
                synchronized(ArticlesDatabase::class.java) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ArticlesDatabase::class.java, "note_database"
                    ).addMigrations(MIGRATION)
                        .build()
                }
            }

            return instance as ArticlesDatabase
        }

    }
}