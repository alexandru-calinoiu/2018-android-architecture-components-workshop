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

class DetailViewModel : RxViewModel(), DetailContract.ViewModel {
    lateinit var dailyForecastRepository: DailyForecastRepository
    lateinit var schedulerProvider: SchedulerProvider

    data class DetailLoaded(val weather: DailyForecast) : ViewModelState()

    override val states = MutableLiveData<ViewModelState>()

    override fun getDetail(id: String) {
        states.value = Loading
        launch {
            dailyForecastRepository.getWeatherDetail(id).with(schedulerProvider).subscribe(
                    { states.value = DetailLoaded(it) },
                    { states.value = Failed(it) })
        }
    }
}