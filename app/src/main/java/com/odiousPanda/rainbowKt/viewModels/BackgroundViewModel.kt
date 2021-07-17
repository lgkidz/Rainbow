package com.odiousPanda.rainbowKt.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odiousPanda.rainbowKt.apis.Result
import com.odiousPanda.rainbowKt.model.dataSource.unsplash.Unsplash
import com.odiousPanda.rainbowKt.repositories.BackgroundRepos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class BackgroundViewModel : ViewModel() {
    private val repo = BackgroundRepos()

    private val _currentImageBackgroundInfo = MutableLiveData<Result<Unsplash>>()

    val currentImageBackgroundInfo: LiveData<Result<Unsplash>>
        get() = _currentImageBackgroundInfo

    fun getRandomBackground(){
        viewModelScope.launch {
            repo.getRandomBackground()
                .onStart {
                    emit(Result.Loading)
                }.collect {
                    _currentImageBackgroundInfo.value = it
                }
        }
    }

    fun getWeatherBackground(query: String){
        viewModelScope.launch {
            repo.getWeatherBackground(query)
                .onStart {
                    emit(Result.Loading)
                }.collect {
                    _currentImageBackgroundInfo.value = it
                }
        }
    }
}