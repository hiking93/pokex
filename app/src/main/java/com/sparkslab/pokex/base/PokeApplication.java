package com.sparkslab.pokex.base;

import android.app.Application;

import com.sparkslab.pokex.lib.FirebaseAnalyticsHelper;
import com.sparkslab.pokex.lib.Prefs;

/**
 * Application to provide more functionality
 *
 * @author Created by hiking on 2016/8/26.
 */
public class PokeApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Prefs.init(this);
		FirebaseAnalyticsHelper.init(this);
	}
}
