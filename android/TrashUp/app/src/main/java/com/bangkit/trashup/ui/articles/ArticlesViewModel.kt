package com.bangkit.trashup.ui.articles

import UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.trashup.data.Result
import com.bangkit.trashup.data.remote.request.ViewRequest
import com.bangkit.trashup.data.remote.response.DatasItem
import kotlinx.coroutines.launch

class ArticlesViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _result = MediatorLiveData<Result<List<DatasItem>>>()
    val result: LiveData<Result<List<DatasItem>>> = _result

    private val _text = MutableLiveData<Result<String>>()
    val text: LiveData<Result<String>> = _text

    private val _updateViewResult = MutableLiveData<Result<String>>()
    val updateViewResult: LiveData<Result<String>> = _updateViewResult

    fun updateArticleView(id: Int, wasteGroup: String) {
        val viewRequest = ViewRequest(id, wasteGroup)
        viewModelScope.launch {
            _updateViewResult.value = Result.Loading
            val result = userRepository.updateArticleView(viewRequest)
            _updateViewResult.value = result
        }
    }

    fun getStories() {
        _result.value = Result.Loading
        viewModelScope.launch {
            val results = userRepository.getArticles()
            _result.addSource(results) { response ->
                _result.value = response
            }
        }
    }
}
