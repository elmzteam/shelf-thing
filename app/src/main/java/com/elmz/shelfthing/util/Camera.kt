package com.elmz.shelfthing.util

import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import timber.log.Timber
import java.util.*

internal class Camera {
	// Image result processor
	private var mImageReader: ImageReader? = null
	// Active camera device connection
	private var mCameraDevice: CameraDevice? = null
	// Active camera capture session
	private var mCaptureSession: CameraCaptureSession? = null

	// Initialize a new camera device connection
	fun initializeCamera(context: Context,
	                     backgroundHandler: Handler,
	                     imageAvailableListener: ImageReader.OnImageAvailableListener) {

		// Discover the camera instance
		val manager = context.getSystemService(CAMERA_SERVICE) as CameraManager
		var camIds = arrayOf<String>()
		try {
			camIds = manager.cameraIdList
		} catch (e: CameraAccessException) {
			Timber.d("Cam access exception getting IDs", e)
		}

		if (camIds.isEmpty()) {
			Timber.d("No cameras found")
			return
		}
		val id = camIds[0]
		Timber.d("Using camera id " + id)

		// Initialize image processor
		mImageReader = ImageReader.newInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
				ImageFormat.JPEG, MAX_IMAGES)
		mImageReader!!.setOnImageAvailableListener(imageAvailableListener, backgroundHandler)

		// Open the camera resource
		try {
			manager.openCamera(id, mStateCallback, backgroundHandler)
		} catch (cae: CameraAccessException) {
			Timber.d("Camera access exception", cae)
		}

	}

	// Close the camera resources
	fun shutDown() {
		mCameraDevice?.close()
	}

	fun takePicture() {
		if (mCameraDevice == null) {
			Timber.w("Cannot capture image. Camera not initialized.")
			return
		}

		// Here, we create a CameraCaptureSession for capturing still images.
		try {
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
			Timber.d("Session initialized.")
			mCaptureSession?.capture(captureBuilder.build(), mCaptureCallback, null)
		} catch (cae: CameraAccessException) {
			Timber.d("camera capture exception")
		}

	}

	private val mStateCallback = object : CameraDevice.StateCallback() {
		override fun onOpened(cameraDevice: CameraDevice?) {
			mCameraDevice = cameraDevice
		}

		override fun onDisconnected(cameraDevice: CameraDevice?) {
			mCameraDevice = null
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
				Timber.d("CaptureSession closed")
			}
		}
	}

	// Callback handling session state changes
	private val mSessionCallback = object : CameraCaptureSession.StateCallback() {
		override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
			// The camera is already closed
			if (mCameraDevice == null) {
				return
			}

			// When the session is ready, we start capture.
			mCaptureSession = cameraCaptureSession
			triggerImageCapture()
		}

		override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
			Timber.w("Failed to configure camera")
		}
	}

	companion object {
		// Camera image parameters (device-specific)
		private val IMAGE_WIDTH = 0
		private val IMAGE_HEIGHT = 0
		private val MAX_IMAGES = 0
	}
}