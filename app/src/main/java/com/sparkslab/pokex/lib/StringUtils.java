package com.sparkslab.pokex.lib;

import android.os.Bundle;

/**
 * String utilities
 *
 * @author Created by hiking on 2016/8/27.
 */
public class StringUtils {

	/**
	 * Convert bundle to readable string
	 *
	 * @param bundle The bundle to convert
	 * @return String representation of bundle
	 */
	public static String toString(Bundle bundle) {
		if (bundle == null) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			stringBuilder.append(String.format("%s %s (%s)\n", key, value,
					value == null ? "null" : value.getClass().getName()));
		}
		return stringBuilder.substring(0, stringBuilder.length() - 1);
	}
}
