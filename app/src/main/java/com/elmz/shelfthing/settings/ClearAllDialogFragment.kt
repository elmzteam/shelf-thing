package com.elmz.shelfthing.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceDialogFragmentCompat
import timber.log.Timber

class ClearAllDialogFragment : PreferenceDialogFragmentCompat() {

	override fun onDialogClosed(positiveResult: Boolean) {
		if (positiveResult) {
			Timber.d("deleting all")
		}
	}

	companion object {
		fun newInstance(key: String): ClearAllDialogFragment {
			val fragment = ClearAllDialogFragment()
			val b = Bundle(1)
			b.putString(PreferenceDialogFragmentCompat.ARG_KEY, key)
			fragment.arguments = b
			return fragment
		}
	}
}
