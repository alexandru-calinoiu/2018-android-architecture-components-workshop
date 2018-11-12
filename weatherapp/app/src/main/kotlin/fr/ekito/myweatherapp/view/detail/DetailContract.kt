package fr.ekito.myweatherapp.view.detail

import android.arch.lifecycle.LiveData
import fr.ekito.myweatherapp.view.ViewModelState

interface DetailContract {
    interface ViewModel {
        val states: LiveData<ViewModelState>

        fun getDetail(id: String)
    }
}