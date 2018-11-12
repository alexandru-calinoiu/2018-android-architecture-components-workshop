package fr.ekito.myweatherapp.view.splash

import fr.ekito.myweatherapp.domain.repository.DailyForecastRepository
import fr.ekito.myweatherapp.util.mvvm.RxViewModel
import fr.ekito.myweatherapp.util.mvvm.SingleLiveEvent
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with
import fr.ekito.myweatherapp.view.Fail
import fr.ekito.myweatherapp.view.Pending
import fr.ekito.myweatherapp.view.Success
import fr.ekito.myweatherapp.view.ViewModelEvent

class SplashViewModel(
        private val dailyForecastRepository: DailyForecastRepository,
        private val schedulerProvider: SchedulerProvider
) : RxViewModel(), SplashContract.ViewModel {

    override val events = SingleLiveEvent<ViewModelEvent>()

    override fun getLastWeather() {
        events.value = Pending

        launch {
            dailyForecastRepository.getWeather().with(schedulerProvider)
                    .toCompletable()
                    .subscribe(
                            { events.value = Success },
                            { events.value = Fail(it) })
        }
    }

}