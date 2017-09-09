package com.elmz.shelfthing.component;

import com.elmz.shelfthing.module.AppModule;
import com.elmz.shelfthing.module.NetModule;
import com.elmz.shelfthing.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
	void inject(MainActivity activity);
}
