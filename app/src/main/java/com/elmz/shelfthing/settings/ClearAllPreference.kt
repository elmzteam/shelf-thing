package com.elmz.shelfthing.settings

import android.content.Context
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet

class ClearAllPreference @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
	init {
		dialogMessage = "This will delete all data!"
		positiveButtonText = "Clear"
	}
}
