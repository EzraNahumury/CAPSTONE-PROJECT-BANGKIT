@file:Suppress("PackageDirectoryMismatch")

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.trashup.data.Result
import com.bangkit.trashup.data.local.entity.ArticlesFavEntity
import com.bangkit.trashup.data.local.room.ArticlesDao
import com.bangkit.trashup.data.pref.UserModel
import com.bangkit.trashup.data.pref.UserPreference
import com.bangkit.trashup.data.remote.response.DatasItem
import com.bangkit.trashup.data.remote.retrofit.ApiService
import com.bangkit.trashup.data.remote.request.LoginRequest
import com.bangkit.trashup.data.remote.request.RegisterRequest
import com.bangkit.trashup.data.remote.request.ViewRequest
import com.bangkit.trashup.data.remote.response.RegisterResponse
import com.bangkit.trashup.utils.AppExecutors
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val articlesDao: ArticlesDao,
    private val appExecutors: AppExecutors
) {

    fun getAllFavoriteArticles(): LiveData<List<ArticlesFavEntity>>? {
        return articlesDao.getAllFavoriteArticles()
    }
    fun getFavoriteArticleById(id: Int): LiveData<ArticlesFavEntity>? {
        return  articlesDao.getFavoriteArticleById(id)
    }
    fun deleteFavoriteArticleById(id: Int) {
        appExecutors.diskIO.execute {
            articlesDao.deleteFavoriteArticleById(id)
        }
    }
    fun insertFavoriteArticle(favoriteEventEntity: ArticlesFavEntity) {
        appExecutors.diskIO.execute {
            articlesDao.insertFavoriteArticle(favoriteEventEntity)
        }
    }

    fun register(name: String, email: String, password: String): LiveData<Result<String>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val request = RegisterRequest(name, email, password)
            val response = apiService.register(request)

            if (response.isSuccessful) {
                val message = response.body()?.message.orEmpty()
                emit(Result.Success(message))
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error("Register Failed: ${error.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<String>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            if (response.isSuccessful) {
                val loginResult = response.body()?.loginResult
                val user = UserModel(
                    userId = (loginResult?.userId ?: -1),
                    name = loginResult?.name.orEmpty(),
                    token = loginResult?.token.orEmpty()
                )
                userPreference.saveSession(user)
                emit(Result.Success("Login Successful"))
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error("Login Failed: ${error.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getArticles(): LiveData<Result<List<DatasItem>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val token = userPreference.getSession().firstOrNull()?.token.orEmpty()
            if (token.isEmpty()) {
                emit(Result.Error("Login first"))
                return@liveData
            }

            val response = apiService.getArticles()
            if (response.isSuccessful) {
                val listArticles = response.body()?.payload?.datas.orEmpty().filterNotNull()
                emit(Result.Success(listArticles))
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error("Error: ${error.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    suspend fun updateArticleView(viewRequest: ViewRequest): Result<String> {
        return try {
            val response = apiService.updateArticleView(viewRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.Success("Article view updated successfully") // Success message
                } ?: Result.Error("Invalid response")
            } else {
                Result.Error("Error code: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Exception: ${e.message}")
        }
    }


//    fun getArticles(): LiveData<PagingData<DatasItem>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = 5
//            ),
//            pagingSourceFactory = {
//                ArticlesPagingSource(apiService)
//            }
//        ).liveData
//    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logOut() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            pref: UserPreference,
            apiService: ApiService,
            articlesDao: ArticlesDao,
            appExecutors: AppExecutors
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(pref, apiService, articlesDao, appExecutors)
            }.also { instance = it }
    }
}
