package com.elmz.shelfthing.util

import android.os.Handler
import android.util.Log
import com.google.android.things.contrib.driver.pwmservo.Servo
import java.io.IOException

class CameraServo(gpioPin: String) {
	companion object {
		private val REFRESH_RATE: Double = 50.0
	}

	private val servo: Servo = Servo(gpioPin, CameraServo.REFRESH_RATE)
	private val handler: Handler = Handler()

	init {
		servo.setAngleRange(-90.0, 90.0)
		servo.setEnabled(false)
	}

	fun destroy () {
		servo.close()
	}

	fun sweepShelf(angleCallback: () -> Unit, doneCallback: () -> Unit) {
		servo.setEnabled(true)

		val runnable = object : Runnable {
			val ANGLE_STEP: Double = 30.0
			val STOP_ANGLE: Double = 60.0
			val STEP_DELAY: Long = 400
			val WAIT_DELAY: Long = 1600
			var angle: Double = -60.0

			override fun run() {
				if (angle > STOP_ANGLE) {
					Log.i("CameraServo", "Done sweeping shelf")
					servo.setEnabled(false)
					doneCallback()
				} else {
					Log.i("CameraServo", "Rotating to angle " + angle)
					servo.angle = angle
					angle += ANGLE_STEP

					handler.postDelayed(angleCallback, STEP_DELAY)
					handler.postDelayed(this, STEP_DELAY + WAIT_DELAY)
				}
			}
		}

		handler.post(runnable)
	}
}