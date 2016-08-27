package com.sparkslab.pokex.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Shared preference helper
 *
 * @author Created by hiking on 2016/7/17.
 */
public class Prefs {

	public static final String KEY_SENSOR_THRESHOLD = "KEY_SENSOR_THRESHOLD"; // in m/s^2
	public static final String KEY_UPDATE_INTERVAL = "KEY_UPDATE_INTERVAL"; // in ms
	public static final String KEY_MOVE_MULTIPLIER_LAT = "KEY_MOVE_MULTIPLIER_LAT";
	public static final String KEY_MOVE_MULTIPLIER_LONG = "KEY_MOVE_MULTIPLIER_LONG";
	public static final String KEY_RESPAWN_LAT = "KEY_RESPAWN_LAT";
	public static final String KEY_RESPAWN_LONG = "KEY_RESPAWN_LONG";
	public static final String KEY_SENSOR_CALIBRATION_X = "KEY_SENSOR_CALIBRATION_X";
	public static final String KEY_SENSOR_CALIBRATION_Y = "KEY_SENSOR_CALIBRATION_Y";

	private static final String DEFAULT_PREF_NAME = "pokemon";

	private static Bundle mDefaultValues;
	private static SharedPreferences mSharedPreferences;

	public static void init(Context context) {
		mSharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE);

		mDefaultValues = new Bundle();
		mDefaultValues.putFloat(KEY_SENSOR_THRESHOLD, 2f);
		mDefaultValues.putInt(KEY_UPDATE_INTERVAL, 100);
		mDefaultValues.putFloat(KEY_MOVE_MULTIPLIER_LONG, .00015f);
		mDefaultValues.putFloat(KEY_MOVE_MULTIPLIER_LAT, .00015f);
		mDefaultValues.putFloat(KEY_RESPAWN_LAT, 25.03895f);
		mDefaultValues.putFloat(KEY_RESPAWN_LONG, 121.55894f);
		mDefaultValues.putFloat(KEY_SENSOR_CALIBRATION_X, 0f);
		mDefaultValues.putFloat(KEY_SENSOR_CALIBRATION_Y, 3f);
	}

	public static Bundle getAll(Context context) {
		Bundle bundle = new Bundle();
		bundle.putFloat(KEY_SENSOR_THRESHOLD, getFloat(context, KEY_SENSOR_THRESHOLD));
		bundle.putInt(KEY_UPDATE_INTERVAL, getInt(context, KEY_UPDATE_INTERVAL));
		bundle.putFloat(KEY_MOVE_MULTIPLIER_LONG, getFloat(context, KEY_MOVE_MULTIPLIER_LONG));
		bundle.putFloat(KEY_MOVE_MULTIPLIER_LAT, getFloat(context, KEY_MOVE_MULTIPLIER_LAT));
		bundle.putFloat(KEY_RESPAWN_LAT, getFloat(context, KEY_RESPAWN_LAT));
		bundle.putFloat(KEY_RESPAWN_LONG, getFloat(context, KEY_RESPAWN_LONG));
		bundle.putFloat(KEY_SENSOR_CALIBRATION_X, getFloat(context, KEY_SENSOR_CALIBRATION_X));
		bundle.putFloat(KEY_SENSOR_CALIBRATION_Y, getFloat(context, KEY_SENSOR_CALIBRATION_Y));
		return bundle;
	}

	public static void setToDefault(Context context, String key) {
		mSharedPreferences.edit().remove(key).apply();
	}

	public static void setAllToDefault(Context context) {
		mSharedPreferences.edit().clear().apply();
	}

	public static int getInt(Context context, String key) {
		return mSharedPreferences.getInt(key, mDefaultValues.getInt(key));
	}

	public static void setInt(Context context, String key, int value) {
		mSharedPreferences.edit().putInt(key, value).apply();
	}

	public static float getFloat(Context context, String key) {
		return mSharedPreferences.getFloat(key, mDefaultValues.getFloat(key));
	}

	public static void setFloat(Context context, String key, float value) {
		mSharedPreferences.edit().putFloat(key, value).apply();
	}

	public static String getString(Context context, String key) {
		return mSharedPreferences.getString(key, mDefaultValues.getString(key));
	}

	public static void setString(Context context, String key, String value) {
		mSharedPreferences.edit().putString(key, value).apply();
	}
}
