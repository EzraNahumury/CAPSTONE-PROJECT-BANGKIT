package com.bangkit.trashup.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>){

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.userId
            preferences[NAME_KEY] = user.name
            preferences[TOKEN] = user.token
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[USER_ID] ?: -1,
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN] ?: ""
            )
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object{
        @Volatile
        private var Instance: UserPreference? = null

        private val USER_ID = intPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return Instance ?: synchronized(this){
                val instance = UserPreference(dataStore)
                Instance = instance
                instance
            }
        }
    }
}