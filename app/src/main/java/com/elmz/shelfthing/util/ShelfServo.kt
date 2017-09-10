package com.elmz.shelfthing.util

import android.util.Log
import com.google.android.things.contrib.driver.pwmservo.Servo
import java.io.IOException

class ShelfServo(gpioPin: String) {
	companion object {
		private val REFRESH_RATE: Double = 50.0
	}

	private val servo: Servo = Servo(gpioPin, ShelfServo.REFRESH_RATE)
	private var currentShelf: Int = 0

	init {
		servo.setAngleRange(-1.0, 1.0)
		servo.setEnabled(false)
	}

	fun destroy () {
		servo.close()
	}

	fun moveToShelf(shelf: Int, callback: () -> Unit) {
		if (shelf == currentShelf) {
			return callback()
		}

		Thread(Runnable {
			run {
				servo.setEnabled(true)

				// Move in one direction for a period of time
				// Fake this by moving at +/- .25 for 1s
				if (shelf < currentShelf) {
					servo.angle = -.25
				} else {
					servo.angle = .25
				}

				Log.i("ShelfServo", "Moving to shelf " + shelf)
				Thread.sleep(1000)
				Log.i("ShelfServo", "Done moving to shelf")
				servo.setEnabled(false)
				callback()
			}
		}).start()
	}
}