package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import fr.ekito.myweatherapp.R
import fr.ekito.myweatherapp.view.Failed
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.android.viewmodel.ext.android.viewModelByClass

/**
 * Weather Result View
 */
class WeatherActivity() : AppCompatActivity(), Parcelable {

    private val TAG = this::class.java.simpleName

    private val viewModel: WeatherContract.ViewModel by viewModelByClass(WeatherViewModel::class)

    constructor(parcel: Parcel) : this() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val weatherTitleFragment = WeatherHeaderFragment()
        val resultListFragment = WeatherListFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.weather_title, weatherTitleFragment)
            .commit()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.weather_list, resultListFragment)
            .commit()

        listenToStates()
        getWeather()
    }

    private fun listenToStates() {
        viewModel.states.observe(this, Observer {
            when(it) {
                is Failed -> showError(it.error)
            }
        })
    }

    private fun getWeather() {
        viewModel.getWeather()
    }

    private fun showError(error: Throwable) {
        Log.e(TAG, "error $error while displaying weather")
        weather_views.visibility = View.GONE
        weather_error.visibility = View.VISIBLE
        Snackbar.make(
            weather_result,
            "WeatherActivity got error : $error",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.retry) {
                startActivity(intentFor<WeatherActivity>().clearTop().clearTask().newTask())
            }
            .show()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherActivity> {
        override fun createFromParcel(parcel: Parcel): WeatherActivity {
            return WeatherActivity(parcel)
        }

        override fun newArray(size: Int): Array<WeatherActivity?> {
            return arrayOfNulls(size)
        }
    }
}
