package com.griffith.feedreeder_3061874.ui.reader

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast

class ReaderActivity : Activity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var light: Sensor? = null

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_LIGHT) {
            getLightSensor(event)
        }
    }

    private fun getLightSensor(event: SensorEvent) {
        Log.w("LightFromSensor", "Light: ${event.values[0]}")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) =
        Toast.makeText(this, "accuracy changed!", Toast.LENGTH_SHORT).show()
}