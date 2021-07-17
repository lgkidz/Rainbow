package com.odiousPanda.rainbowKt.repositories

import com.odiousPanda.rainbowKt.apis.Result
import com.odiousPanda.rainbowKt.apis.RetrofitService
import com.odiousPanda.rainbowKt.model.dataSource.unsplash.Unsplash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class BackgroundRepos {
    private val call = RetrofitService.createUnsplashCall()

    suspend fun getRandomBackground(): Flow<Result<Unsplash>> = flow {
        val response = withContext(Dispatchers.IO) {
            call.getRandom()
        }

        if(response.isSuccessful) {
            emit(Result.Success(response.body()!!))
        } else {
            emit(Result.Error)
        }
    }

    suspend fun getWeatherBackground(query: String): Flow<Result<Unsplash>> = flow {
        val response = withContext(Dispatchers.IO) {
            call.getRandomWeather(query = query)
        }

        if(response.isSuccessful) {
            emit(Result.Success(response.body()!!))
        } else {
            emit(Result.Error)
        }
    }
}