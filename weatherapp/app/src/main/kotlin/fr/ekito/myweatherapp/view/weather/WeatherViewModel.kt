package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.MutableLiveData
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.mvvm.RxViewModel
import fr.ekito.myweatherapp.util.mvvm.SingleLiveEvent
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with
import fr.ekito.myweatherapp.view.Failed
import fr.ekito.myweatherapp.view.Loading
import fr.ekito.myweatherapp.view.ViewModelEvent
import fr.ekito.myweatherapp.view.ViewModelState

class WeatherViewModel(
        private val dailyForecastRepository: DailyForecastRepository,
        private val schedulerProvider: SchedulerProvider
) : RxViewModel(), WeatherContract.ViewModel {

    override val states = MutableLiveData<ViewModelState>()

    override val events = SingleLiveEvent<ViewModelEvent>()

    data class WeatherListLoaded(
            val location: String,
            val first: DailyForecast,
            val list: List<DailyForecast>
    ) : ViewModelState() {
        companion object {
            fun from(list: List<DailyForecast>): WeatherListLoaded {
                return if (list.isEmpty()) error("Weather list should not be empty")
                else {
                    val first = list.first()
                    val location = first.location
                    WeatherListLoaded(location, first, list)
                }
            }
        }
    }

    data class ProceedLocation(val location: String) : ViewModelEvent()
    data class ProceedLocationFail(val location: String, val error: Throwable) : ViewModelEvent()

    override fun getWeather() {
        states.value = Loading
        launch {
            dailyForecastRepository.getWeather()
                    .with(schedulerProvider)
                    .subscribe(
                            { states.value = WeatherListLoaded.from(it) },
                            { states.value = Failed(it) }
                    )
        }
    }

    override fun loadNewLocation(location: String) {
        events.value = ProceedLocation(location)

        launch {
            dailyForecastRepository.getWeather(location)
                    .with(schedulerProvider)
                    .subscribe(
                            { states.value = WeatherListLoaded.from(it) },
                            { events.value = ProceedLocationFail(location, it) })
        }
    }
}