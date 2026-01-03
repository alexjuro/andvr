package andvr.android.vrclient

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.androidgamesdk.GameActivity

class MainActivity : GameActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize of the sensor managers
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (gyroscope == null) {
            Log.e("GyroLog", "Device has no gyroscope!")
        }
    }

    override fun onResume() {
        super.onResume()
        gyroscope?.let {
            // 5000 microseconds equal to 200hz
            sensorManager.registerListener(this, it, 5000)
        }
    }

    override fun onPause() {
        super.onPause()
        // stopping sensor on pause to save battery
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val x = event.values[0] // Rotation um X-Achse
            val y = event.values[1] // Rotation um Y-Achse
            val z = event.values[2] // Rotation um Z-Achse

            Log.d("GyroLog", "X: ${"%.3f".format(x)} | Y: ${"%.3f".format(y)} | Z: ${"%.3f".format(z)}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nicht zwingend nötig für einfaches Logging
    }


    companion object {
        init {
            System.loadLibrary("vrclient")
        }
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            hideSystemUi()
//        }
//    }
//
//    private fun hideSystemUi() {
//        val decorView = window.decorView
//        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN)
//    }
}