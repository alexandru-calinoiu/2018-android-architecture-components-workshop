package fr.ekito.myweatherapp

import android.arch.persistence.room.Room
import fr.ekito.myweatherapp.data.room.WeatherDatabase
import org.koin.dsl.module.module

// Room In memory database
val roomTestModule = module(override = true) {
    single {
        Room.inMemoryDatabaseBuilder(get(), WeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}