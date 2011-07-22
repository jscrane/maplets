package org.syzygy.util.midp;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpUtil extends StreamUtil
{
    /**
     * Encodes a string according to W3C standards.
     * http://forum.java.sun.com/thread.jspa?threadID=594204&messageID=3596899
     *
     * @param s the string to encode
     * @return the encoded form
     */
    public static String urlEncode(String s)
    {
        StringBuffer sb = new StringBuffer(s.length() * 3);
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (c == '&')
                sb.append("&amp;");
            else if (c == ' ')
                sb.append('+');
            else if ((c >= ',' && c <= ';') || (c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z') || c == '_' || c == '?')
                sb.append(c);
            else {
                sb.append('%');
                if (c > 15) // is it a non-control char, ie. >x0F so 2 chars
                    sb.append(Integer.toHexString((int) c)); // just add % and the string
                else
                    sb.append("0").append(Integer.toHexString((int) c));
                // otherwise need to add a leading 0
            }
        }
        return sb.toString();
    }

    public static byte[] get(String url) throws IOException
    {
        HttpConnection conn = (HttpConnection) Connector.open(url);
        ByteArrayOutputStream output = null;
        InputStream input = null;
        try {
            // This is required to be present by Google Maps
            conn.setRequestProperty("User-Agent:", "Maplets");
            int code = conn.getResponseCode();
            if (code != HttpConnection.HTTP_OK) {
                System.err.println("code=" + code + " for " + url);
                return null;
            }
            input = conn.openInputStream();
            output = new ByteArrayOutputStream();
            int n;
            byte[] buf = new byte[4096];
            while ((n = input.read(buf)) != -1)
                output.write(buf, 0, n);

        } catch (NullPointerException e) {
            // me4se-2.1.3 throws an NPE on Jeode
            System.err.println("no connection for " + url);
            return null;
        } finally {
            safeClose(input);
            safeClose(output);
            safeClose(conn);
        }
        return output.toByteArray();
    }

    public static String tinyUrl(String u)
    {
        try {
            return new String(get("http://tinyurl.com/api-create.php?url=" + u));
        } catch (IOException e) {
            return null;
        }
    }
}