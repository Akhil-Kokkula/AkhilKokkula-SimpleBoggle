package com.example.akhilkokkula_simpleboggle

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), BoardFragment.BoardFragmentListener, ScoreFragment.ScoreFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val shakeLimit = 10f
        var lastShakeTime = 0L
        val delay = 2000
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val accel = sqrt((x * x + y * y + z * z).toDouble())
                    if (accel > shakeLimit && System.currentTimeMillis() - lastShakeTime > delay) {
                        lastShakeTime = System.currentTimeMillis()
                        onClickPassed(true)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
        }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDataPassed(score: Int) {
        val fragmentScore = supportFragmentManager.findFragmentById(R.id.fragmentScore) as ScoreFragment?
        fragmentScore?.updateScore(score)
    }

    override fun onClickPassed(isClicked: Boolean) {
        val fragmentBoard = supportFragmentManager.findFragmentById(R.id.fragmentBoard) as BoardFragment?
        fragmentBoard?.updateBoard(isClicked)
    }


}