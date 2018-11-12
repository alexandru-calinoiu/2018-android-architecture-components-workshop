package fr.ekito.myweatherapp.view.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.mvvm.RxViewModel
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with
import fr.ekito.myweatherapp.view.Failed
import fr.ekito.myweatherapp.view.Loading
import fr.ekito.myweatherapp.view.ViewModelState

class DetailViewModel : RxViewModel() {
    private lateinit var dailyForecastRepository: DailyForecastRepository
    private lateinit var schedulerProvider: SchedulerProvider

    data class DetailLoaded(val weather: DailyForecast) : ViewModelState()

    private val _states = MutableLiveData<ViewModelState>()

    public val states: LiveData<ViewModelState>
        get() = _states

    fun getDetail(id: String) {
        _states.value = Loading
        launch {
            dailyForecastRepository.getWeatherDetail(id).with(schedulerProvider).subscribe(
                    { _states.value = DetailLoaded(it) },
                    { _states.value = Failed(it) })
        }
    }
}