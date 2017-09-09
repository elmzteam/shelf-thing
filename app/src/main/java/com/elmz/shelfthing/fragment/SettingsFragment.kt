package com.elmz.shelfthing.fragment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.elmz.shelfthing.R
import com.elmz.shelfthing.settings.ClearAllDialogFragment
import com.elmz.shelfthing.settings.ClearAllPreference

class SettingsFragment : PreferenceFragmentCompat() {

	override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
		setPreferencesFromResource(R.xml.preferences, rootKey)
	}

	override fun onDisplayPreferenceDialog(preference: Preference) {
		// Try if the preference is one of our custom Preferences
		var dialogFragment: DialogFragment? = null
		if (preference is ClearAllPreference) {
			// Create a new instance of TimePreferenceDialogFragment with the key of the related
			// Preference
			dialogFragment = ClearAllDialogFragment.newInstance(preference.key)
		}

		// If it was one of our custom Preferences, show its dialog
		if (dialogFragment != null) {
			dialogFragment.setTargetFragment(this, 0)
			dialogFragment.show(this.fragmentManager,
					"android.support.v7.preference.PreferenceFragment.DIALOG")
		} else {
			super.onDisplayPreferenceDialog(preference)
		}
	}
}