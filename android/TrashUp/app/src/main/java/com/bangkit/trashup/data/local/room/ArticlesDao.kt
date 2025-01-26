package com.bangkit.trashup.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit.trashup.data.local.entity.ArticlesFavEntity

@Dao
interface ArticlesDao {

    @Query("SELECT * from ArticlesFavEntity ORDER BY id ASC")
    fun getAllFavoriteArticles(): LiveData<List<ArticlesFavEntity>>?

    @Query("SELECT * FROM ArticlesFavEntity WHERE id = :id")
    fun getFavoriteArticleById(id: Int): LiveData<ArticlesFavEntity>?

    @Query("DELETE FROM ArticlesFavEntity WHERE id = :id")
    fun deleteFavoriteArticleById(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavoriteArticle(articleFavEntity: ArticlesFavEntity)
}
