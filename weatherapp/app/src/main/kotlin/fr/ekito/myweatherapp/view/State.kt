package fr.ekito.myweatherapp.view

open class ViewModelState

object Loading : ViewModelState()

data class Failed(val error: Throwable) : ViewModelState()
