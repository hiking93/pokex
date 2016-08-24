package com.sparkslab.pokex.lib;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XSharedPreferences;

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
	private static XSharedPreferences mXSharedPreferences;

	private static void refresh(Context context, boolean x) {
		if (x) {
			mXSharedPreferences = new XSharedPreferences("com.sparkslab.pokex", DEFAULT_PREF_NAME);
		} else {
			mSharedPreferences = context.getSharedPreferences(DEFAULT_PREF_NAME,
					Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
		}

		mDefaultValueMap = new HashMap<>();
		mDefaultValueMap.put(KEY_SENSOR_THRESHOLD, 2f);
		mDefaultValueMap.put(KEY_UPDATE_INTERVAL, 100);
		mDefaultValueMap.put(KEY_MOVE_MULTIPLIER_LONG, .00015f);
		mDefaultValueMap.put(KEY_MOVE_MULTIPLIER_LAT, .00015f);
		mDefaultValueMap.put(KEY_RESPAWN_LAT, 25.044194f);
		mDefaultValueMap.put(KEY_RESPAWN_LONG, 121.553897f);
		mDefaultValueMap.put(KEY_SENSOR_CALIBRATION_X, 0f);
		mDefaultValueMap.put(KEY_SENSOR_CALIBRATION_Y, 3f);
	}

	public static void refresh(Context context) {
		refresh(context, false);
	}

	public static void refreshX(Context context) {
		refresh(context, true);
	}

	public static SharedPreferences getPrefs(Context context) {
		if (mSharedPreferences == null) {
			refresh(context, false);
		}
		return mSharedPreferences;
	}

	public static SharedPreferences.Editor getPrefEditor(Context context) {
		if (mSharedPreferences == null) {
			refresh(context, false);
		}
		return mSharedPreferences.edit();
	}

	public static void setToDefault(Context context, String key) {
		if (mSharedPreferences == null) {
			refresh(context, false);
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
			refresh(context, false);
		}
		return getIntValue(mSharedPreferences.getInt(key, getIntValue(mDefaultValueMap.get(key))));
	}

	public static int getXInt(Context context, String key) {
		if (mXSharedPreferences == null) {
			refresh(context, true);
		}
		return getIntValue(mXSharedPreferences.getInt(key, getIntValue(mDefaultValueMap.get(key))));
	}

	public static void setInt(Context context, String key, int value) {
		if (mSharedPreferences == null) {
			refresh(context, false);
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
			refresh(context, false);
		}
		return getFloatValue(
				mSharedPreferences.getFloat(key, getFloatValue(mDefaultValueMap.get(key))));
	}

	public static float getXFloat(Context context, String key) {
		if (mXSharedPreferences == null) {
			refresh(context, true);
		}
		return getFloatValue(
				mXSharedPreferences.getFloat(key, getFloatValue(mDefaultValueMap.get(key))));
	}

	public static void setFloat(Context context, String key, float value) {
		if (mSharedPreferences == null) {
			refresh(context, false);
		}
		mSharedPreferences.edit().putFloat(key, value).apply();
	}

	public static String getString(Context context, String key) {
		if (mSharedPreferences == null) {
			refresh(context, false);
		}
		return mSharedPreferences.getString(key, (String) mDefaultValueMap.get(key));
	}

	public static String getXString(Context context, String key) {
		if (mXSharedPreferences == null) {
			refresh(context, true);
		}
		return mXSharedPreferences.getString(key, (String) mDefaultValueMap.get(key));
	}

	public static void setString(Context context, String key, String value) {
		if (mSharedPreferences == null) {
			refresh(context, false);
		}
		mSharedPreferences.edit().putString(key, value).apply();
	}
}
