package com.elmz.shelfthing

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ImageReader.OnImageAvailableListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import android.view.Surface
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.elmz.shelfthing.fragment.HomeFragment
import com.elmz.shelfthing.fragment.SettingsFragment
import com.elmz.shelfthing.fragment.StatusFragment
import com.elmz.shelfthing.util.Api
import com.elmz.shelfthing.util.Camera
import com.elmz.shelfthing.util.DrawerActivity
import com.elmz.shelfthing.util.EnumUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : DrawerActivity(), HomeFragment.OnFragmentInteractionListener,
		StatusFragment.OnFragmentInteractionListener {
	private val PERMISSION_REQUEST_CODE = 12
	private lateinit var mInputMethodManager: InputMethodManager
	private lateinit var mFragmentManager: FragmentManager

	// Fragments
	private var mHomeFragment: HomeFragment? = null
	private var mStatusFragment: StatusFragment? = null
	private var mSettingsFragment: SettingsFragment? = null

	// Other things
	private var mActiveDisplay: Display? = null
	private lateinit var mApi: Api
	private var mCameraHandler: Handler? = null
	private var mCameraThread: HandlerThread? = null
	private var mCamera: Camera? = null
	private var mMissingProducts: ArrayList<String> = ArrayList()

	// Injected components
	@Inject
	lateinit var mRetrofit: Retrofit
	@Inject
	lateinit var mHttpClient: OkHttpClient

	private enum class Display {
		HOME, STATUS, SETTINGS;
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		mInputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
		mFragmentManager = supportFragmentManager
		(application as App).netComponent.inject(this)
		mApi = mRetrofit.create(Api::class.java)

		mFragmentManager.addOnBackStackChangedListener {
			val count = mFragmentManager.backStackEntryCount
			setDrawerIndicatorEnabled(count == 0)
		}

		if (hasPermission()) {
			initialize()
		} else {
			requestPermission()
		}

		// Restore previous state or default
		if (savedInstanceState == null) {
			switchToFragment(Display.HOME)
		} else {
			mActiveDisplay = EnumUtil.deserialize(Display::class.java).from(savedInstanceState)
			mHomeFragment = mFragmentManager.getFragment(savedInstanceState, Display.HOME.name) as HomeFragment?
			mStatusFragment = mFragmentManager.getFragment(savedInstanceState, Display.STATUS.name) as StatusFragment?
			mSettingsFragment = mFragmentManager.getFragment(savedInstanceState, Display.SETTINGS.name) as SettingsFragment?
			switchToFragment(mActiveDisplay!!)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		mCameraThread?.quitSafely()
		mCamera?.shutDown()
	}

	override fun onSaveInstanceState(savedInstanceState: Bundle) {
		super.onSaveInstanceState(savedInstanceState)
		EnumUtil.serialize(mActiveDisplay).to(savedInstanceState)
		var temp: Fragment?
		for (d in Display.values()) {
			// Check if manager contains fragment
			temp = mFragmentManager.findFragmentByTag(d.name)
			if (temp != null) {
				mFragmentManager.putFragment(savedInstanceState, d.name, temp)
			}
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		// Handle navigation view item clicks here.
		when (item.itemId) {
			R.id.nav_home -> if (mActiveDisplay !== Display.HOME) {
				switchToFragment(Display.HOME)
			}
			R.id.nav_status -> if (mActiveDisplay !== Display.STATUS) {
				switchToFragment(Display.STATUS)
			}
			R.id.nav_settings -> if (mActiveDisplay !== Display.SETTINGS) {
				switchToFragment(Display.SETTINGS)
			}
		}

		return super.onNavigationItemSelected(item)
	}

	override fun onBackPressed() {
		val stackSize = mFragmentManager.backStackEntryCount
		super.onBackPressed()
		// Change display if stack was popped
		if (mFragmentManager.backStackEntryCount < stackSize) {
			onBackStackPopped()
		}
	}

	override fun popBackStack(): Boolean {
		if (super.popBackStack()) {
			onBackStackPopped()
			return true
		}
		return false
	}

	private fun onBackStackPopped() {
		val fragment = mFragmentManager.findFragmentById(R.id.container)
		switchToDisplay(Display.valueOf(fragment.tag))
	}

	private fun switchToFragment(display: Display) {
		when (display) {
			Display.HOME -> {
				if (mHomeFragment == null) {
					mHomeFragment = HomeFragment.newInstance()
				}
				mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
				mFragmentManager.beginTransaction()
						.replace(R.id.container, mHomeFragment, display.name)
						.commit()
			}
			Display.STATUS -> {
				if (mStatusFragment == null) {
					mStatusFragment = StatusFragment.newInstance(mMissingProducts)
				}
				mFragmentManager.beginTransaction()
						.replace(R.id.container, mStatusFragment, display.name)
						.commit()
			}
			Display.SETTINGS -> {
				if (mSettingsFragment == null) {
					mSettingsFragment = SettingsFragment()
				}
				mFragmentManager.beginTransaction()
						.replace(R.id.container, mSettingsFragment, display.name)
						.addToBackStack(null)
						.commit()
			}
		}
		switchToDisplay(display)
	}

	private fun switchToDisplay(display: Display) {
		mActiveDisplay = display
		invalidateOptionsMenu()
		title = when (display) {
			Display.HOME -> resources.getString(R.string.title_home)
			Display.STATUS -> resources.getString(R.string.title_status)
			Display.SETTINGS -> resources.getString(R.string.title_settings)
		}
	}

	private fun hasPermission(): Boolean {
		return arrayOf(CAMERA, INTERNET, ACCESS_NETWORK_STATE).none {
			checkSelfPermission(it) == PackageManager.PERMISSION_DENIED }
	}

	private fun requestPermission() {
		if (shouldShowRequestPermissionRationale(CAMERA)
				|| shouldShowRequestPermissionRationale(INTERNET)
				|| shouldShowRequestPermissionRationale(ACCESS_NETWORK_STATE)) {
			Toast.makeText(this, "Camera, settings, and internet connectivity are " +
					"required for this application", Toast.LENGTH_LONG).show()
		}
		arrayOf(CAMERA, INTERNET, ACCESS_NETWORK_STATE)
				.filter { checkSelfPermission(it) == PackageManager.PERMISSION_DENIED }
				.forEach { requestPermissions(arrayOf(it), PERMISSION_REQUEST_CODE) }
	}

	override fun onRequestPermissionsResult(requestCode: Int,
	                                        permissions: Array<String>, grantResults: IntArray) {
		when (requestCode) {
			PERMISSION_REQUEST_CODE -> {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					initialize()
					Timber.i("Permission granted")
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Timber.e("Permission denied")
					mCameraThread?.quitSafely()
					mCamera = null
				}
			}
		}
	}

	private fun initialize() {
		if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED && mCamera == null) {
			mCameraThread = HandlerThread("CameraBackground")
			mCameraThread!!.start()
			mCameraHandler = Handler(mCameraThread!!.looper)
			mCamera = Camera(this, mCameraHandler!!, mOnImageAvailableListener)
		}
		if (!android.provider.Settings.System.canWrite(this)) {
			intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
			intent.data = Uri.parse("package:" + packageName)
			startActivity(intent)
		}
		Settings.System.putInt(
				contentResolver,
				Settings.System.ACCELEROMETER_ROTATION,
				0
		)
		Settings.System.putInt(
				contentResolver,
				Settings.System.USER_ROTATION,
				Surface.ROTATION_180
		)
	}

	private fun handleApiFailure(t: Throwable) {
		val message = t.message ?: "Could not connect"
		makeSnackbar(message, Snackbar.LENGTH_LONG)
		if (BuildConfig.DEBUG) t.printStackTrace()
	}

	private fun makeSnackbar(message: String, length: Int) {
		// container is defined in each fragment
		Snackbar.make(findViewById(R.id.container), message, length).show()
	}

	override fun onClickInfo() {
		switchToFragment(Display.STATUS)
	}

	override fun onClickSettings() {
		mCamera?.startTakingPicture()
	}

	override fun onClickBack() {
		switchToFragment(Display.HOME)
	}

	// Callback to receive captured camera image data
	private val mOnImageAvailableListener = OnImageAvailableListener { reader ->
		// Get the raw image bytes
		val image = reader.acquireLatestImage()
		val imageBuf = image.planes[0].buffer
		val imageBytes = ByteArray(imageBuf.remaining())
		imageBuf.get(imageBytes)
		image.close()
		reader.close()

		onPictureTaken(imageBytes)
	}

	private fun onPictureTaken(imageBytes: ByteArray?) {
		if (imageBytes != null) {
			Timber.i("Picture was taken")
			val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), imageBytes)
			val body = MultipartBody.Part.createFormData("pantry", "test", requestBody)
			val fileList: List<MultipartBody.Part> = listOf(body)
			mApi.upload(fileList).enqueue(object : Callback<List<String>> {
				override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>?) {
					Timber.d("response received")
					mMissingProducts = ArrayList(response?.body())
					mHomeFragment?.update(mMissingProducts)
				}

				override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
					if (t != null) handleApiFailure(t)
					else Timber.e("API really failed")
				}
			})
		}
	}
}
