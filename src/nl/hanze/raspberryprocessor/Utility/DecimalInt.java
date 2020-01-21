package nl.hanze.raspberryprocessor.Utility;

/**
 * Simple class to store decimal numbers as an integer value
 */
public class DecimalInt {

    public static double getDouble(int value) {
        return ((double) value) / 100;
    }

    /**
     * @param s String to parse, with 0, 1 or 2 decimal numbers.
     * @return Integer value of 100* normal value.
     */
    public static int parseDecimalInt(String s) {
        if (s == null) {
            throw new NumberFormatException();
        }

        int i = 0;
        int radix = 10;
        int max = s.length();
        int result = 0;
        int decimalCorrection = 100;
        boolean negative = false;
        boolean isDecimal = false;
        int digit;

        if (max > 0) {
            if (s.charAt(0) == '-') {
                negative = true;
                i++;
            }
            while (i < max) {
                if (s.charAt(i) == '.' || s.charAt(i) == ',') {
                    isDecimal = true;
                } else {
                    digit = Character.digit(s.charAt(i),radix);
                    if (digit < 0 ) {
                        throw new NumberFormatException();
                    }
                    result *= radix;
                    result += digit;
                    if (isDecimal) {
                        decimalCorrection /= radix;
                    }
                }
                i++;
            }
            result *= decimalCorrection;
        }
        if (decimalCorrection == 0) {
            throw new NumberFormatException();
        }

        if (negative) {
            return -result;
        } else {
            return result;
        }
    }
}
