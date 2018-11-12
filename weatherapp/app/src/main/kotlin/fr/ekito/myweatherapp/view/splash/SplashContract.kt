package fr.ekito.myweatherapp.view.splash

import android.arch.lifecycle.LiveData
import fr.ekito.myweatherapp.view.ViewModelEvent

/**
 * Weather MVVM Contract
 */
interface SplashContract {
    interface ViewModel {
        val events: LiveData<ViewModelEvent>

        fun getLastWeather()
    }
}