package org.syzygy.gps.midp;

import org.syzygy.gps.GeoName;
import org.syzygy.gps.Location;
import org.syzygy.gps.LocationSource;
import org.syzygy.gps.NmeaSourceFactory;
import org.syzygy.util.PropertySource;

import javax.microedition.lcdui.Command;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * A helper base-class for Maplets.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public abstract class LocationAwareMIDlet extends PropertyConfiguredMIDlet
{
    protected LocationAwareMIDlet() throws Exception
    {
        this.reader = createSource(props);
    }

    protected GeoName locate(Location location) throws Exception
    {
        return new GeoNameFactory().fromDefaultURL(location.getLatitude(), location.getLongitude());
    }

    protected LocationSource createSource(PropertySource props) throws Exception
    {
        return new NmeaSourceFactory().create(props);
    }

    protected void destroyApp(boolean unconditionally) throws MIDletStateChangeException
    {
        reader.stop();
        notifyDestroyed();
    }

    protected void pauseApp()
    {
    }

    protected final LocationSource reader;
    protected final Command exit = new Command("Exit", Command.EXIT, 1);
}
