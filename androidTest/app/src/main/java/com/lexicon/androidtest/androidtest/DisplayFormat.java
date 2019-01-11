package com.lexicon.androidtest.androidtest;

import android.graphics.Color;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

public class DisplayFormat {
    public static String convertPercentageToDisplayFormat(String number, int precision) {
        return "";
    }

    public static String convertToDisplayFormat(String number, int precision) {
        Double n = Double.parseDouble(number);
        return "";
    }

    public static String formatString(String number) {
        Double d = Double.parseDouble(number);
        String newString = doubleToString(d);
        DecimalFormat df = new DecimalFormat();
        if(d >= 1d && d < 100d) {
            df.applyPattern("#.###");
            df.setMinimumFractionDigits(3);
            newString = String.format(Locale.US, "%.3f", d);
        }
        else if(d >= 100d) {
            df.applyPattern("#.##");
            df.setMinimumFractionDigits(2);
            newString = String.format(Locale.US, "%.2f", d);
        }
        else if(d < 1d) {
            newString = formatSignificantFigures(newString, 4);
            BigDecimal bigDecimal = new BigDecimal(newString);
            newString = bigDecimal.toPlainString();
            //set maximum to 10 digits
        }
        return newString;
    }

    public static String formatPercentString(String percent) {
        Double d = Double.parseDouble(percent);
        if(d < 10d) {
            return String.format(Locale.US, "%.4f", d);
        }
        else {
            return String.format(Locale.US,"%.3f", d);
        }
    }

    public static Color getPercentColor(String percent) {
        return null;
    }

    public static String formatSignificantFigures(String number, int significantFigures) {
        BigDecimal bigDecimal = new BigDecimal(number);
        String s = String.format("%." + significantFigures + "G", bigDecimal);
        if (s.contains("E+")){
            s = String.format(Locale.US, "%.0f", Double.valueOf(String.format("%." + significantFigures + "G", bigDecimal)));
        }
        return s;
    }

    public static String doubleToString(Double d) {
        if (d == null)
            return null;
        if (d.isNaN() || d.isInfinite())
            return d.toString();

        // Pre Java 8, a value of 0 would yield "0.0" below
        if (d.doubleValue() == 0)
            return "0";
        return new BigDecimal(d.toString()).stripTrailingZeros().toPlainString();
    }
}
