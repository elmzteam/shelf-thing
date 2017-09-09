package com.elmz.shelfthing

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.elmz.shelfthing.fragment.HomeFragment
import com.elmz.shelfthing.fragment.SettingsFragment
import com.elmz.shelfthing.fragment.StatusFragment
import com.elmz.shelfthing.util.Api
import com.elmz.shelfthing.util.DrawerActivity
import com.elmz.shelfthing.util.EnumUtil
import com.elmz.shelfthing.util.TabFragment
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File
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
	private lateinit var mInputMethodManager: InputMethodManager
	private lateinit var mFragmentManager: FragmentManager

	// Fragments
	private var mHomeFragment: HomeFragment? = null
	private var mStatusFragment: StatusFragment? = null
	private var mSettingsFragment: SettingsFragment? = null

	private var mActiveDisplay: Display? = null
	private var mApi: Api? = null

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

		// Restore previous state or default
		if (savedInstanceState == null) {
			switchToFragment(Display.HOME)
		} else {
			mActiveDisplay = EnumUtil.deserialize(Display::class.java).from(savedInstanceState)
			mSettingsFragment = mFragmentManager.getFragment(savedInstanceState, Display.SETTINGS.name) as SettingsFragment
			switchToFragment(mActiveDisplay!!)
		}
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

	private fun switchToFragment(display: Display) {
		when (display) {
			Display.HOME -> {
				if (mHomeFragment == null) {
					mHomeFragment = HomeFragment.newInstance("", "")
				}
				mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
				mFragmentManager.beginTransaction()
						.replace(R.id.container, mHomeFragment, display.name)
						.commit()
			}
			Display.STATUS -> {
				if (mStatusFragment == null) {
					mStatusFragment = StatusFragment.newInstance("", "")
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
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		val params = toolbar.layoutParams as AppBarLayout.LayoutParams
		var fragment: TabFragment? = null
		params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
		var expandedToolbar = false
		when (display) {
			Display.HOME -> {
				expandedToolbar = true
				title = resources.getString(R.string.title_home)
			}
			Display.STATUS -> {
				params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
						AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
						AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
				expandedToolbar = true
				title = resources.getString(R.string.title_status)
			}
			Display.SETTINGS -> title = resources.getString(R.string.title_settings)
		}
		findViewById<AppBarLayout>(R.id.appbar).setExpanded(expandedToolbar)
		toolbar.layoutParams = params
		prepareTabLayout(fragment)
	}

	fun sendImage(filePath: String) {
		val file = File(filePath)

		val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
		val body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile)
		val name = RequestBody.create(MediaType.parse("text/plain"), "upload_test")

//        mApi.postImage()
	}

	private fun handleApiFailure(t: Throwable) {
		val message = t.message ?: "Could not connect"
		Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_LONG).show()
		if (BuildConfig.DEBUG) t.printStackTrace()
	}

	override fun onFragmentInteraction(s: String) {
	}
}
