package org.syzygy.gps.midp;

import org.syzygy.gps.Location;
import org.syzygy.gps.LocationSource;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

public class ReplaySource extends LocationSource
{
    public void setPath(String path)
    {
        this.path = path;
    }

    private Vector readLocations(String path) throws IOException
    {
        FileConnection conn = (FileConnection) Connector.open(path, Connector.READ);
        int b;
        StringBuffer buf = new StringBuffer();
        InputStream input = conn.openInputStream();
        Vector locations = new Vector();
        try {
            while ((b = input.read()) != -1) {
                char c = (char) b;
                if (c == '\n') {
                    locations.addElement(Location.parse(buf.toString()));
                    buf = new StringBuffer();
                } else
                    buf.append(c);
            }
        } finally {
            input.close();
        }
        return locations;
    }

    public void speedUp()
    {
        if (delay > 0)
            delay -= 200;
    }

    public void slowDown()
    {
        delay += 200;
    }

    public void run()
    {
        this.runner = Thread.currentThread();
        try {
            Vector locations = readLocations(path);
            for (Enumeration e = locations.elements(); e.hasMoreElements();) {
                Location l = (Location) e.nextElement();
                System.out.println(l.toString());
                notifyLocation(l);
                if (!sleep(delay))
                    return;
            }
        } catch (IOException _) {
            // yuk
        } finally {
            notifyLocation(null);
        }
    }

    public void stop()
    {
        super.stop();
        if (runner != null)
            runner.interrupt();
    }

    private boolean sleep(int millis)
    {
        if (millis > 0)
            try {
                Thread.sleep(millis);
            } catch (InterruptedException _) {
                // expected if call stop()
            }
        return !isStopped();
    }

    private String path;
    private Thread runner;
    private int delay = 1000;
}
