package com.elmz.shelfthing.util

import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import timber.log.Timber
import java.util.*

internal class Camera(context: Context, backgroundHandler: Handler, imageAvailableListener: ImageReader.OnImageAvailableListener) {
	// Image result processor
	private var mImageReader: ImageReader? = null
	// Active camera device connection
	private var mCameraDevice: CameraDevice? = null
	// Active camera capture session
	private var mCaptureSession: CameraCaptureSession? = null

	private val mManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
	private val mBackgroundHandler = backgroundHandler
	private val mImageAvailableListener = imageAvailableListener

	// Initialize a new camera device connection
	fun startTakingPicture() {
		if (mCameraDevice != null) {
			Timber.e("Ugh")
			mImageReader?.close()
			mCameraDevice?.close()
		}
		// Discover the camera instance
		var camIds = arrayOf<String>()
		try {
			camIds = mManager.cameraIdList
		} catch (e: CameraAccessException) {
			Timber.e("Cam access exception getting IDs", e)
		}

		if (camIds.isEmpty()) {
			Timber.d("No cameras found!")
			return
		}
		val id = camIds[0]
		Timber.d("Using camera id %s", id)

		// Initialize image processor
		mImageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT, ImageFormat.JPEG, MAX_IMAGES)
		mImageReader!!.setOnImageAvailableListener(mImageAvailableListener, mBackgroundHandler)

		// Open the camera resource
		try {
			mManager.openCamera(id, mStateCallback, mBackgroundHandler)
		} catch (cae: CameraAccessException) {
			Timber.e("Camera access exception", cae)
		}
	}

	// Close the camera resources
	fun shutDown() {
		mCameraDevice?.close()
	}

	private fun takePicture() {
		if (mCameraDevice == null) {
			Timber.w("Cannot capture image. Camera not initialized.")
			return
		}

		// Here, we create a CameraCaptureSession for capturing still images.
		try {
			Timber.v("Creating capture session")
			mCameraDevice?.createCaptureSession(
					Collections.singletonList(mImageReader?.surface),
					mSessionCallback, null)
		} catch (cae: CameraAccessException) {
			Timber.d("access exception while preparing pic", cae)
		}

	}

	private fun triggerImageCapture() {
		try {
			val captureBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE) ?: return
			captureBuilder.addTarget(mImageReader?.surface)
			captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
			Timber.d("Session initialized: %s", mCaptureSession != null)
			mCaptureSession?.capture(captureBuilder.build(), mCaptureCallback, null)
		} catch (cae: CameraAccessException) {
			Timber.d("camera capture exception")
		}

	}

	private val mStateCallback = object : CameraDevice.StateCallback() {
		override fun onOpened(cameraDevice: CameraDevice?) {
			mCameraDevice = cameraDevice
			Timber.d("Camera initialized: %s", cameraDevice != null)
			takePicture()
		}

		override fun onDisconnected(cameraDevice: CameraDevice?) {
			mCameraDevice = null
			Timber.d("Camera disconnected")
		}

		override fun onError(cameraDevice: CameraDevice?, code: Int) {
			Timber.e("Camera state error")
		}
	}

	// Callback handling capture progress events
	private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
		override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?,
		                                result: TotalCaptureResult?) {
			if (session != null) {
				session.close()
				mCaptureSession = null
				Timber.v("CaptureSession closed")
			}
			mCameraDevice?.close()
			mCameraDevice = null
		}
	}

	// Callback handling session state changes
	private val mSessionCallback = object : CameraCaptureSession.StateCallback() {
		override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
			// The camera is already closed
			if (mCameraDevice == null) {
				Timber.d("Camera was already closed, aborting capture")
				return
			}

			// When the session is ready, we start capture.
			mCaptureSession = cameraCaptureSession
			Timber.v("Session configured, triggering image capture")
			triggerImageCapture()
		}

		override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
			Timber.w("Failed to configure camera")
		}
	}

	companion object {
		// Camera image parameters (device-specific)
		private val IMAGE_WIDTH = 2560
		private val IMAGE_HEIGHT = 1920
		// TODO: Not sure
		private val MAX_IMAGES = 3
	}
}
