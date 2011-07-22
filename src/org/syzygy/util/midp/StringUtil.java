package org.syzygy.util.midp;

import java.util.Vector;

public class StringUtil
{
    public static String[] split(String s, char c)
    {
        Vector v = new Vector();
        int start = 0;
        while (true) {
            int end = s.indexOf(c, start);
            if (end == -1) {
                v.addElement(s.substring(start));
                break;
            }
            v.addElement(s.substring(start, end));
            start = end + 1;
        }
        String[] split = new String[v.size()];
        for (int i = 0; i < v.size(); i++)
            split[i] = (String) v.elementAt(i);
        return split;
    }

    public static boolean isEmpty(String s)
    {
        return s == null || "".equals(s);
    }
}