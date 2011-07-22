package org.syzygy.gps;

/**
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public final class Util
{
    /*
     * Formats a double to n digits, padding if necessary.
     * FIXME: should round LSD.
     */
    public static String formatDouble(double d, int n, String pad)
    {
        String s = Double.toString(d);
        int len = s.length();
        if (len > n)
            return s.substring(0, n);
        for (; len < n; len++)
            s = s + pad;
        return s;
    }

    public static String formatCoords(double a, double b, int n)
    {
        return "(" + formatDouble(a, n, "0") + ". " + formatDouble(b, n, "0") + ")";
    }

    public static double decimalize(double dms)
    {
        int deg = (int) dms / 100;
        double min = dms - deg * 100;
        return deg + min / 60;
    }

    public static double undecimalize(double n)
    {
        int deg = (int) n;
        double min = (n - deg) * 60 + 0.00005;
        return 100 * deg + min;
    }
}
