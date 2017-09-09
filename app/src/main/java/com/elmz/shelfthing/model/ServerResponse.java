package com.elmz.shelfthing.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class ServerResponse implements Parcelable {
	@Json(name="test")
	public abstract String getTest();
	@Json(name="another_thing")
	public abstract int[] getAnother();

	public static JsonAdapter<ServerResponse> jsonAdapter(Moshi moshi) {
		return new AutoValue_ServerResponse.MoshiJsonAdapter(moshi);
	}
}
