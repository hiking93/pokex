package com.sparkslab.pokex.lib;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Utilities for Firebase Analytics logging.
 *
 * @author Created by hiking on 2016/8/26.
 */
public class FirebaseAnalyticsHelper {

	private static FirebaseAnalytics mFirebaseAnalytics;

	public static void init(Application application) {
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(application);
	}

	public static void onAppOpen() {
		mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null);
	}

	public static void onCreateService() {
		mFirebaseAnalytics.logEvent("service_create", null);
	}

	public static void onBindService() {
		mFirebaseAnalytics.logEvent("service_bind", null);
	}
}
