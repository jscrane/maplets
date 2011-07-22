package org.syzygy.gps;

import org.syzygy.util.WrappedException;
import org.syzygy.util.midp.StreamUtil;
import org.syzygy.util.midp.StringUtil;

import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * An NmeaSource is a LocationSource which parses NMEA Sentences,
 * see, e.g., http://www.gpsinformation.org/dale/nmea.htm
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public final class NmeaSource extends LocationSource
{
    private interface SentenceProcessor
    {
        void process(String[] data, int last, Location location);
    }

    public NmeaSource(StreamConnection conn, final int channels)
    {
        this.conn = conn;
        processors.put("GGA", new SentenceProcessor()
        {
            public void process(String[] data, int last, Location l)
            {
                l.setGpsTime(data[0]);
                l.setLatitude(decimalize(data[1], "S".equals(data[2])));
                l.setLongitude(decimalize(data[3], "W".equals(data[4])));
                l.setIsError("0".equals(data[5]));
                l.setSatellites(readInt(data[6]));
                l.setAltitude(readDouble(data[8]));
                notifyLocation(l);
            }
        });
        processors.put("GLL", new SentenceProcessor()
        {
            public void process(String[] data, int last, Location l)
            {
                l.setLatitude(decimalize(data[0], "S".equals(data[1])));
                l.setLongitude(decimalize(data[2], "W".equals(data[3])));
                l.setGpsTime(data[4]);
                l.setIsError("V".equals(data[5]));
                notifyLocation(l);
            }
        });
        processors.put("RMC", new SentenceProcessor()
        {
            public void process(String[] data, int last, Location l)
            {
                l.setGpsTime(data[0]);
                l.setIsError("V".equals(data[1]));
                l.setLatitude(decimalize(data[2], "S".equals(data[3])));
                l.setLongitude(decimalize(data[4], "W".equals(data[5])));
                l.setSpeed(readDouble(data[6]));
                l.setCourse(readDouble(data[7]));
                l.setUtcDate(data[8]);
                notifyLocation(l);
            }
        });
        processors.put("GSV", new SentenceProcessor()
        {
            public void process(String[] data, int last, Location l)
            {
                int n = Integer.parseInt(data[0]);
                int seq = Integer.parseInt(data[1]);
                int idx = seq == 1 ? 0 : index;
                for (int i = 6; i < last && idx < channels; i += 4, idx++)
                    signals[idx] = readInt(data[i]);

                index = idx;
                if (seq == n) {
                    notifySignals(signals);
                    for (int i = 0; i < channels; i++)
                        signals[i] = 0;
                }
            }

            private int index = 0;
            private final int[] signals = new int[channels];
        });
    }

    static double readDouble(String s)
    {
        return StringUtil.isEmpty(s) ? 0.0 : Double.valueOf(s).doubleValue();
    }

    static int readInt(String s)
    {
        return StringUtil.isEmpty(s) ? 0 : Integer.valueOf(s).intValue();
    }

    static double decimalize(String s, boolean isNegative)
    {
        double d = Util.decimalize(readDouble(s));
        return isNegative ? -d : d;
    }

    static String unDecimalize(double n, boolean isLongitude)
    {
        if (n < 0)
            n = -n;
        String s = Double.toString(Util.undecimalize(n));
        if (isLongitude && n < 100)
            s = "0" + s;
        return s.substring(0, 9);
    }

    /**
     * Returns a waypoint string for sending to the device.
     * Note: this does <i>not</i> include the initial "$" or the
     * terminating <cr><lf>.
     * Note: If the name parameter is longer than 46 characters
     * it will be truncated to keep the total length == 80.
     *
     * @param lat  the waypoint's latitude
     * @param lon  the waypoint's longitude
     * @param name the waypoint's name
     * @return the waypoint string
     */
    static String waypoint(double lat, double lon, String name)
    {
        String ns = "N", ew = "E";
        if (lon < 0) {
            ns = "S";
            lon = -lon;
        }
        if (lat < 0) {
            ew = "W";
            lat = -lat;
        }
        String s = "GPWPL," + unDecimalize(lat, false) + "," +
                ns + "," + unDecimalize(lon, true) + "," + ew + "," + name;
        if (s.length() > 77)
            s = s.substring(0, 76);
        return s + "*" + Integer.toHexString(checksum(s, 0, s.length())).toUpperCase();
    }

    boolean parseSentence(String line, Location location)
    {
        int len = line.length();
        if (len < 9 || len > 80 || line.indexOf('*') == -1)
            return false;

        String op = line.substring(2, 5);
        SentenceProcessor sp = (SentenceProcessor) processors.get(op);
        if (sp == null) {
            System.err.println("unprocessed: " + op);
            return false;
        }
        String s = line.substring(6);
        int i;
        for (i = 0; i < data.length; i++) {
            int p = s.indexOf(',');
            if (p == -1)
                p = s.indexOf('*');
            if (p == -1)
                break;
            data[i] = s.substring(0, p);
            s = s.substring(p + 1);
        }
        if (i == data.length || StringUtil.isEmpty(s))
            return false;
        int check = Integer.parseInt(s, 16);
        int sum = checksum(line, 0, len - 3);
        System.err.println("checksum=" + Integer.toHexString(check) +
                " computed=" + Integer.toHexString(sum));
        if (check == sum) {
            sp.process(data, i, location);
            return true;
        }
        return false;
    }

    static int checksum(String s, int start, int end)
    {
        System.err.println(s.substring(start, end));
        int sum = 0;
        for (int i = start; i < end; i++)
            sum ^= s.charAt(i);
        return sum;
    }

    String readSentence(InputStream input)
            throws IOException
    {
        buf.setLength(0);
        for (; ;) {
            int ch = input.read();
            if (ch == 0x0d)
                break;
            if (ch == '$')
                return readSentence(input);
            buf.append((char) ch);
        }
        input.read();  // skip \n
        return buf.toString();
    }

    public void run()
    {
        Location location = new Location();
        String sentence = null;
        try {
            synchronized (this) {
                input = conn.openInputStream();
            }
            do {
                while (input.read() != '$') ;
                sentence = readSentence(input);
                System.err.println(sentence);
                parseSentence(sentence, location);
            } while (!isStopped());
            notifyLocation(null);
        } catch (Exception e) {
            notifyError(new WrappedException(e, sentence));
        } finally {
            StreamUtil.safeClose(input);
            StreamUtil.safeClose(conn);
        }
    }

    private final Hashtable processors = new Hashtable();
    private final StreamConnection conn;
    private final String[] data = new String[40];
    private final StringBuffer buf = new StringBuffer();
    private InputStream input = null;
}
