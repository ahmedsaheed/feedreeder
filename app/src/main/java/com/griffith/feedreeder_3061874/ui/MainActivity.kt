package com.griffith.feedreeder_3061874.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.WindowCompat
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.griffith.feedreeder_3061874.ui.theme.Feedreeder_3061874Theme

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var light: Sensor? = null
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val displayFeatures = calculateDisplayFeatures(this)

            Feedreeder_3061874Theme {
                FeedreederApp(windowSizeClass, displayFeatures)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_LIGHT){
            getLightSensor(event)
        }
    }

    private fun getLightSensor(event: SensorEvent) {
        Log.w("LightFromSensor", "Light: ${event.values[0].toString()}")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) =
        Toast.makeText(this, "accuracy changed!", Toast.LENGTH_SHORT).show()

}