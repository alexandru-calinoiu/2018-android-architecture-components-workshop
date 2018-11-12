package fr.ekito.myweatherapp.domain.repository

import fr.ekito.myweatherapp.data.WeatherDataSource
import fr.ekito.myweatherapp.data.room.WeatherDAO
import fr.ekito.myweatherapp.data.room.WeatherEntity
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.ext.getDailyForecasts
import fr.ekito.myweatherapp.domain.ext.getLocation
import io.reactivex.Single
import java.util.*

/**
 * Weather repository
 */
interface DailyForecastRepository {
    /**
     * Get weather from given location
     * if location is null, get list weather or default
     */
    fun getWeather(location: String? = null): Single<List<DailyForecast>>

    /**
     * Get weather for given id
     */
    fun getWeatherDetail(id: String): Single<DailyForecast>
}

class DailyForecastRepositoryRoomImpl(
        private val weatherDataSource: WeatherDataSource,
        private val weatherDAO: WeatherDAO
) : DailyForecastRepository {
    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }

    override fun getWeather(location: String?): Single<List<DailyForecast>> =
            if (location == null) {
                weatherDAO
                        .findLatestWeather()
                        .flatMap {
                            if (it.isEmpty()) getNewWeather(DEFAULT_LOCATION) else getWeatherFromLatest(it.first())
                        }
            } else {
                getNewWeather(location)
            }

    override fun getWeatherDetail(id: String): Single<DailyForecast> =
            weatherDAO.findWeatherById(id).map { DailyForecast.from(it) }

    private fun getNewWeather(location: String): Single<List<DailyForecast>> {
        val now = Date()
        return weatherDataSource
                .geocode(location)
                .map { it.getLocation() ?: error("No Location date") }
                .flatMap { weatherDataSource.weather(it.lat, it.lng, DEFAULT_LANG) }
                .map { it.getDailyForecasts(location) }
                .doOnSuccess {
                    weatherDAO.saveAll(it.map { item -> WeatherEntity.from(item, now) })
                }
    }

    private fun getWeatherFromLatest(latest: WeatherEntity): Single<List<DailyForecast>> {
        return weatherDAO.findAllBy(latest.location, latest.date)
                .map { list -> list.map { DailyForecast.from(it) } }
    }
}

/**
 * Weather repository
 * Make use of WeatherDataSource & add some cache
 */
class DailyForecastRepositoryImpl(private val weatherDataSource: WeatherDataSource) :
        DailyForecastRepository {

    private fun lastLocationFromCache() = weatherCache.firstOrNull()?.location

    private val weatherCache = arrayListOf<DailyForecast>()

    override fun getWeatherDetail(id: String): Single<DailyForecast> =
            Single.just(weatherCache.first { it.id == id })

    override fun getWeather(
            location: String?
    ): Single<List<DailyForecast>> {
        // Take cache
        return if (location == null && weatherCache.isNotEmpty()) return Single.just(weatherCache)
        else {
            val targetLocation: String = location ?: lastLocationFromCache() ?: DEFAULT_LOCATION
            weatherCache.clear()
            weatherDataSource.geocode(targetLocation)
                    .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                    .flatMap { weatherDataSource.weather(it.lat, it.lng, DEFAULT_LANG) }
                    .map { it.getDailyForecasts(targetLocation) }
                    .doOnSuccess { weatherCache.addAll(it) }
        }
    }

    companion object {
        const val DEFAULT_LOCATION = "Paris"
        const val DEFAULT_LANG = "EN"
    }
}
