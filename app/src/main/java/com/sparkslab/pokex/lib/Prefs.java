package com.sparkslab.pokex.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

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

	private static Map<String, Object> mDefaultValueMap;
	private static SharedPreferences mSharedPreferences;

	private static void init(Context context) {
		mSharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE);

		mDefaultValueMap = new HashMap<>();
		mDefaultValueMap.put(KEY_SENSOR_THRESHOLD, 2f);
		mDefaultValueMap.put(KEY_UPDATE_INTERVAL, 100);
		mDefaultValueMap.put(KEY_MOVE_MULTIPLIER_LONG, .00015f);
		mDefaultValueMap.put(KEY_MOVE_MULTIPLIER_LAT, .00015f);
		mDefaultValueMap.put(KEY_RESPAWN_LAT, 25.03895f);
		mDefaultValueMap.put(KEY_RESPAWN_LONG, 121.55894f);
		mDefaultValueMap.put(KEY_SENSOR_CALIBRATION_X, 0f);
		mDefaultValueMap.put(KEY_SENSOR_CALIBRATION_Y, 3f);
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
		if (mSharedPreferences == null) {
			init(context);
		}
		mSharedPreferences.edit().remove(key).apply();
	}

	public static int getIntValue(Object obj) {
		int defVal;
		if (obj instanceof Integer) {
			defVal = (Integer) obj;
		} else if (obj instanceof Float) {
			defVal = ((Float) obj).intValue();
		} else if (obj instanceof Double) {
			defVal = ((Double) obj).intValue();
		} else {
			defVal = 0;
		}
		return defVal;
	}

	public static int getInt(Context context, String key) {
		if (mSharedPreferences == null) {
			init(context);
		}
		return getIntValue(mSharedPreferences.getInt(key, getIntValue(mDefaultValueMap.get(key))));
	}

	public static void setInt(Context context, String key, int value) {
		if (mSharedPreferences == null) {
			init(context);
		}
		mSharedPreferences.edit().putInt(key, value).apply();
	}

	public static float getFloatValue(Object obj) {
		float defVal;
		if (obj instanceof Integer) {
			defVal = ((Integer) obj).floatValue();
		} else if (obj instanceof Float) {
			defVal = (Float) obj;
		} else if (obj instanceof Double) {
			defVal = ((Double) obj).floatValue();
		} else {
			defVal = 0f;
		}
		return defVal;
	}

	public static float getFloat(Context context, String key) {
		if (mSharedPreferences == null) {
			init(context);
		}
		return getFloatValue(
				mSharedPreferences.getFloat(key, getFloatValue(mDefaultValueMap.get(key))));
	}

	public static void setFloat(Context context, String key, float value) {
		if (mSharedPreferences == null) {
			init(context);
		}
		mSharedPreferences.edit().putFloat(key, value).apply();
	}

	public static String getString(Context context, String key) {
		if (mSharedPreferences == null) {
			init(context);
		}
		return mSharedPreferences.getString(key, (String) mDefaultValueMap.get(key));
	}

	public static void setString(Context context, String key, String value) {
		if (mSharedPreferences == null) {
			init(context);
		}
		mSharedPreferences.edit().putString(key, value).apply();
	}
}
