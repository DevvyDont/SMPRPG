package xyz.devvydont.smprpg.util.formatting;

import java.text.DecimalFormat;
import java.util.regex.Pattern;


public class MinecraftStringUtils {

    /**
     * Convert an integer to its roman numeral representation
     *
     * @param n
     * @return
     */
    public static String numToRomanNumeral(int n) {

        // Can't be asked to do this the right way rn
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            case 11 -> "XI";
            case 12 -> "XII";
            case 13 -> "XIII";
            case 14 -> "XIV";
            case 15 -> "XV";
            case 20 -> "XX";
            case 30 -> "XXX";
            case 40 -> "XL";
            case 50 -> "L";
            default -> String.valueOf(n);
        };
    }

    public static String getTitledString(String string) {
        String regex = "\\b(.)(.*?)\\b";
        return Pattern.compile(regex).matcher(string.replace("_", " ").toLowerCase()).replaceAll(
                matche -> matche.group(1).toUpperCase() + matche.group(2)
        );
    }


    public static String formatNumber(long n) {
        return new DecimalFormat("#,###,###,###,###").format(n);
    }

    public static String formatDecimal(double d) {
        return new DecimalFormat("#,###.##").format(d);
    }

    public static String formatFloat(float f) {
        return new DecimalFormat("#,###.#").format(f);
    }

}
