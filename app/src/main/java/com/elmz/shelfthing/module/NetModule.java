package com.elmz.shelfthing.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.elmz.shelfthing.util.AutoValueFactory;
import com.elmz.shelfthing.util.Util;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class NetModule {
	String mBaseUrl;

	// Constructor needs one parameter to instantiate.
	public NetModule(String baseUrl) {
		this.mBaseUrl = baseUrl;
	}

	// Dagger will only look for methods annotated with @Provides
	@Provides
	@Singleton
	// Application reference must come from AppModule.class
	SharedPreferences providesSharedPreferences(Application application) {
		return PreferenceManager.getDefaultSharedPreferences(application);
	}

	@Provides
	@Singleton
	Cache provideOkHttpCache(Application application) {
		int cacheSize = 10 * 1024 * 1024; // 10 MiB
		Cache cache = new Cache(application.getCacheDir(), cacheSize);
		return cache;
	}

	@Provides
	@Singleton
	Moshi provideMoshi() {
		Moshi.Builder moshiBuilder = new Moshi.Builder();
		moshiBuilder.add(AutoValueFactory.create());
		return moshiBuilder.build();
	}

	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(final Application application, Cache cache) {
		OkHttpClient builder = new OkHttpClient.Builder()
			.cache(cache)
			.addInterceptor(new Interceptor() {
				@Override
				public Response intercept(Chain chain) throws IOException {
					if (Util.isNetworkAvailable(application)) {
						return chain.proceed(chain.request());
					} else {
						throw new IOException("No internet connection");
					}
				}
			})
			.build();
		return builder;
	}

	@Provides
	@Singleton
	Retrofit provideRetrofit(Moshi moshi, OkHttpClient okHttpClient) {
		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.baseUrl(mBaseUrl)
			.client(okHttpClient)
			.build();
		return retrofit;
	}
}
