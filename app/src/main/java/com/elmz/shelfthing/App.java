package com.elmz.shelfthing;

import android.app.Application;

import com.elmz.shelfthing.component.DaggerNetComponent;
import com.elmz.shelfthing.component.NetComponent;
import com.elmz.shelfthing.module.AppModule;
import com.elmz.shelfthing.module.NetModule;

import timber.log.Timber;

public class App extends Application {
	private NetComponent mNetComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}
		// TODO: crash reporting tree

		mNetComponent = DaggerNetComponent.builder()
			.appModule(new AppModule(this))
			.netModule(new NetModule("https://104.197.173.128/"))
			.build();
	}

	public NetComponent getNetComponent() {
		return mNetComponent;
	}
}
