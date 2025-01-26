package com.bangkit.trashup.ui.favorite

import UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.trashup.data.local.entity.ArticlesFavEntity
import kotlinx.coroutines.launch

class FavoriteViewModel(private val userRepository: UserRepository) : ViewModel()  {

    fun getAllFavoriteArticles(): LiveData<List<ArticlesFavEntity>>?
            = userRepository.getAllFavoriteArticles()

    fun getFavoriteArticleById(id: Int): LiveData<ArticlesFavEntity>? {
        return userRepository.getFavoriteArticleById(id)
    }
    fun insertFavoriteArticle(favoriteEventEntity: ArticlesFavEntity) {
        viewModelScope.launch {
            userRepository.insertFavoriteArticle(favoriteEventEntity)
        }
    }
    fun deleteFavoriteArticleById(id: Int) {
        viewModelScope.launch {
            userRepository.deleteFavoriteArticleById(id)
        }
    }
}