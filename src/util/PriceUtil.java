package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/*
 * ImageUtil provides useful methods for formatting money values
 *
 * Author: Nick Chen
 * Date: June 18, 2022
 */

public class PriceUtil {

	// Rounds a money value to two decimal places

	public static double roundMoney(double d) {

		BigDecimal bd = new BigDecimal(d);
		BigDecimal roundOff = bd.setScale(2, RoundingMode.HALF_EVEN);
		return roundOff.doubleValue();
	}

	// Adds a dollar sign to money values

	public static String moneyFormat(double d) {

		DecimalFormat df = new DecimalFormat("$0.00");
		return df.format(d);
	}

	public static String roundAndFormat(double d) {

		return moneyFormat(roundMoney(d));
	}
}
