package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.LiveData
import fr.ekito.myweatherapp.view.ViewModelEvent
import fr.ekito.myweatherapp.view.ViewModelState

interface WeatherContract {
    interface ViewModel {
        val events: LiveData<ViewModelEvent>

        val states: LiveData<ViewModelState>

        fun getWeather()

        fun loadNewLocation(location: String)
    }
}