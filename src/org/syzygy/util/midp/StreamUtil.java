package org.syzygy.util.midp;

import javax.microedition.io.Connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil
{
    public static void safeClose(InputStream s)
    {
        if (s != null)
            try {
                s.close();
            } catch (IOException e) {
                // ignore
            }
    }

    public static void safeClose(OutputStream s)
    {
        if (s != null)
            try {
                s.close();
            } catch (IOException e) {
                // ignore
            }
    }

    public static void safeClose(Connection s)
    {
        if (s != null)
            try {
                s.close();
            } catch (IOException e) {
                // ignore
            }
    }
}