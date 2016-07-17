package go.pokemon.pokemon.lib;

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

	public static String KEY_SENSOR_THRESHOLD = "KEY_SENSOR_THRESHOLD"; // in m/s^2
	public static String KEY_UPDATE_INTERVAL = "KEY_UPDATE_INTERVAL"; // in ms
	public static String KEY_MOVE_MULTIPLIER_LAT = "KEY_MOVE_MULTIPLIER_LAT";
	public static String KEY_MOVE_MULTIPLIER_LONG = "KEY_MOVE_MULTIPLIER_LONG";
	public static String KEY_RESPAWN_LAT = "KEY_RESPAWN_LAT";
	public static String KEY_RESPAWN_LONG = "KEY_RESPAWN_LONG";

	private static Map<String, Object> mDefaultValueMap;
	private static SharedPreferences mSharedPreferences;
	private static XSharedPreferences mXSharedPreferences;

	private static void init(Context context, boolean x) {
		if (x) {
			mXSharedPreferences = new XSharedPreferences("go.pokemon.pokemon", "pokemon");
		} else {
			mSharedPreferences = context.getSharedPreferences("pokemon",
					Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
		}

		mDefaultValueMap = new HashMap<>();
		mDefaultValueMap.put(KEY_SENSOR_THRESHOLD, 2f);
		mDefaultValueMap.put(KEY_UPDATE_INTERVAL, 200);
		mDefaultValueMap.put(KEY_MOVE_MULTIPLIER_LONG, .00001f);
		mDefaultValueMap.put(KEY_MOVE_MULTIPLIER_LAT, .00001f);
		mDefaultValueMap.put(KEY_RESPAWN_LAT, 40.7589f);
		mDefaultValueMap.put(KEY_RESPAWN_LONG, -73.9851f);
	}

	public static SharedPreferences getPrefs(Context context) {
		if (mSharedPreferences == null) {
			init(context, false);
		}
		return mSharedPreferences;
	}

	public static SharedPreferences.Editor getPrefEditor(Context context) {
		if (mSharedPreferences == null) {
			init(context, false);
		}
		return mSharedPreferences.edit();
	}

	public static void setToDefault(Context context, String key) {
		if (mSharedPreferences == null) {
			init(context, false);
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
			throw new ClassCastException("Unsupported class: " + obj.getClass().getName());
		}
		return defVal;
	}

	public static int getInt(Context context, String key) {
		if (mSharedPreferences == null) {
			init(context, false);
		}
		return getIntValue(mSharedPreferences.getInt(key, getIntValue(mDefaultValueMap.get(key))));
	}

	public static int getXInt(Context context, String key) {
		if (mXSharedPreferences == null) {
			init(context, true);
		}
		return getIntValue(mXSharedPreferences.getInt(key, getIntValue(mDefaultValueMap.get(key))));
	}

	public static void setInt(Context context, String key, int value) {
		if (mSharedPreferences == null) {
			init(context, false);
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
			throw new ClassCastException("Unsupported class: " + obj.getClass().getName());
		}
		return defVal;
	}

	public static float getFloat(Context context, String key) {
		if (mSharedPreferences == null) {
			init(context, false);
		}
		return getFloatValue(
				mSharedPreferences.getFloat(key, getFloatValue(mDefaultValueMap.get(key))));
	}

	public static float getXFloat(Context context, String key) {
		if (mXSharedPreferences == null) {
			init(context, true);
		}
		return getFloatValue(
				mXSharedPreferences.getFloat(key, getFloatValue(mDefaultValueMap.get(key))));
	}

	public static void setFloat(Context context, String key, float value) {
		if (mSharedPreferences == null) {
			init(context, false);
		}
		mSharedPreferences.edit().putFloat(key, value).apply();
	}

	public static String getString(Context context, String key) {
		if (mSharedPreferences == null) {
			init(context, false);
		}
		return mSharedPreferences.getString(key, (String) mDefaultValueMap.get(key));
	}

	public static String getXString(Context context, String key) {
		if (mXSharedPreferences == null) {
			init(context, true);
		}
		return mXSharedPreferences.getString(key, (String) mDefaultValueMap.get(key));
	}

	public static void setString(Context context, String key, String value) {
		if (mSharedPreferences == null) {
			init(context, false);
		}
		mSharedPreferences.edit().putString(key, value).apply();
	}
}