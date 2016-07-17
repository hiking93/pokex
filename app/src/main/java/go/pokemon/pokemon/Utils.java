package go.pokemon.pokemon;

import java.text.DecimalFormat;

/**
 * Utilities
 *
 * @author Created by hiking on 2016/7/17.
 */
public class Utils {

	public static String toDecimalString(float value) {
		return new DecimalFormat("0.########").format(value);
	}
}
