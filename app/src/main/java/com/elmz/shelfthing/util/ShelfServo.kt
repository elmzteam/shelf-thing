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
				Log.i("ShelfServo", "Moving to shelf " + shelf)

				if (shelf < currentShelf) {
					servo.angle = -.6
					Thread.sleep(3000)
				} else {
					servo.angle = .3
					Thread.sleep(2000)
				}

				Log.i("ShelfServo", "Done moving to shelf")
				servo.setEnabled(false)
				callback()
				currentShelf = shelf
			}
		}).start()
	}
}