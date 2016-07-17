package go.pokemon.pokemon;

import android.text.TextUtils;

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
}
