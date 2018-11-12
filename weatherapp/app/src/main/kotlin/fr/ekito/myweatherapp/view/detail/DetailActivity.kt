package fr.ekito.myweatherapp.view.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.domain.entity.getColorFromCode
import fr.ekito.myweatherapp.util.android.argument
import fr.ekito.myweatherapp.view.Failed
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

/**
 * Weather Detail View
 */
class DetailActivity : AppCompatActivity() {

    // Get all needed data
    private val detailId by argument<String>(INTENT_WEATHER_ID)

    private val detailViewModel: DetailContract.ViewModel by lazy {
        val viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.apply {
            dailyForecastRepository = get()
            schedulerProvider = get()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        listenToStateChanges()
        loadDetail()
    }

    fun showError(error: Throwable) {
        Snackbar.make(
                weatherItem,
                getString(R.string.loading_error) + " - $error",
                Snackbar.LENGTH_LONG
        ).show()
    }

    private fun listenToStateChanges() {
        detailViewModel.states.observe(this, Observer {
            when(it) {
                is Failed -> showError(it.error)
                is DetailViewModel.DetailLoaded -> showDetail(it.weather)
            }
        })
    }

    private fun loadDetail() {
        detailViewModel.getDetail(detailId)
    }

    private fun showDetail(weather: DailyForecast) {
        weatherIcon.text = weather.icon
        weatherDay.text = weather.day
        weatherText.text = weather.fullText
        weatherWindText.text = weather.wind.toString()
        weatherTempText.text = weather.temperature.toString()
        weatherHumidityText.text = weather.humidity.toString()
        weatherItem.background.setTint(getColorFromCode(weather))
        // Set back on background click
        weatherItem.setOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val INTENT_WEATHER_ID: String = "INTENT_WEATHER_ID"
    }
}
