package com.sparkslab.pokex.lib;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.text.DecimalFormat;

/**
 * Utilities
 *
 * @author Created by hiking on 2016/7/17.
 */
public class Utils {

	public static String toDecimalString(float value) {
		return new DecimalFormat("0.######").format(value);
	}

	public static boolean isInt(CharSequence value) {
		return !TextUtils.isEmpty(value) && TextUtils.isDigitsOnly(value);
	}

	public static boolean isFloat(CharSequence value) {
		String string = value.toString();
		return !TextUtils.isEmpty(string) && string.matches("\\-?\\d*\\.?\\d*") &&
				!TextUtils.isEmpty(string.replaceAll("\\.|\\-", ""));
	}

	public static DisplayMetrics getDisplayMetrics(Context context) {
		Resources resources = context.getResources();
		return resources.getDisplayMetrics();
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 *
	 * @param context Context to get resources and device specific display metrics
	 * @param dp      A value in dp (density independent pixels) unit. Which we need
	 *                to convert into pixels
	 * @return A float value to represent px equivalent to dp depending on
	 * device density
	 */
	public static float convertDpToPixel(Context context, float dp) {
		return dp * (getDisplayMetrics(context).densityDpi / 160f);
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 *
	 * @param context Context to get resources and device specific display metrics
	 * @param dp      A value in dp (density independent pixels) unit. Which we need
	 *                to convert into pixels
	 * @return Value multiplied by the appropriate metric and truncated to
	 * integer pixels.
	 */
	public static int convertDpToPixelSize(Context context, float dp) {
		float pixels = convertDpToPixel(context, dp);
		final int res = (int) (pixels + 0.5f);
		if (res != 0) {
			return res;
		} else if (pixels == 0) {
			return 0;
		} else if (pixels > 0) {
			return 1;
		}
		return -1;
	}
}
