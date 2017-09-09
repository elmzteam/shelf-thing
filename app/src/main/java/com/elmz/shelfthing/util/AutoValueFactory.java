package com.elmz.shelfthing.util;

import com.squareup.moshi.JsonAdapter;
import com.ryanharter.auto.value.moshi.MoshiAdapterFactory;

// Automatically include auto-value generated TypeAdapters
@MoshiAdapterFactory
public abstract class AutoValueFactory implements JsonAdapter.Factory {

	// Static factory method to access the package
	// private generated implementation
	public static JsonAdapter.Factory create() {
		return new AutoValueMoshi_AutoValueFactory();
	}
}
