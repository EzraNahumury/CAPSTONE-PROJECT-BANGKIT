package com.bangkit.trashup.di

import UserRepository
import android.content.Context
import com.bangkit.trashup.data.local.room.ArticlesDatabase
import com.bangkit.trashup.data.pref.UserPreference
import com.bangkit.trashup.data.pref.dataStore
import com.bangkit.trashup.data.remote.retrofit.ApiConfig
import com.bangkit.trashup.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(context)
        val database = ArticlesDatabase.getInstance(context)
        val dao = database.articlesDao()
        val appExecutors = AppExecutors()
        return UserRepository.getInstance(pref, apiService, dao, appExecutors)
    }
}