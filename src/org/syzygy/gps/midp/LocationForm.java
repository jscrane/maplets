package org.syzygy.gps.midp;

import org.syzygy.gps.ErrorListenerAdapter;
import org.syzygy.gps.Location;
import org.syzygy.gps.LocationSource;
import org.syzygy.gps.Util;

import javax.microedition.lcdui.*;

public final class LocationForm extends Form
{
    public LocationForm(LocationSource reader)
    {
        super("Location");
        this.reader = reader;
        position.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_AFTER);
        time.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_NEWLINE_AFTER);
        append(position);
        append(time);
        append(description);
        append(status);
        reader.addListener(new ErrorListenerAdapter()
        {
            protected void onLocation(Location location)
            {
                setLocation(location);
                setTimeInfo(location.getGpsTime(), location.getSpeed(), location.getAltitude());
                String s = satellites + " satellite";
                if (satellites != 1)
                    s += "s";
                status.setText(location.getIsError() ? "Signal Error " + s : s);
            }

            protected void onSatellites(int satellites)
            {
                this.satellites = satellites;
            }

            private int satellites;
        });
        description.setItemCommandListener(new ItemCommandListener()
        {
            public void commandAction(Command c, Item item)
            {
                System.out.println("cmd=" + c + " item=" + item);
            }
        });
    }

    private void setLocation(Location l)
    {
        location = l;
        position.setText(Util.formatDouble(l.getLatitude(), LOC_PRECISION, "0") + "," +
                Util.formatDouble(l.getLongitude(), LOC_PRECISION, "0"));
    }

    private void setTimeInfo(String t, double s, double alt)
    {
        time.setText(t + " " + Util.formatDouble(s, SPD_PRECISION, "0") +
                " " + Util.formatDouble(alt, ALT_PRECISION, "0"));
    }

    public void stop()
    {
        reader.stop();
    }

    public String getPosition()
    {
        return position.getText();
    }

    public String getTime()
    {
        return time.getText();
    }

    public String getDescription()
    {
        return description.getString();
    }

    public void setDescription(String s)
    {
        description.setString(s);
    }

    public Location getLocation()
    {
        return location;
    }

    public void setStatus(Exception e)
    {
        status.setText(e.toString());
    }

    protected void sizeChanged(int w, int h)
    {
        new Thread(reader).start();
    }

    private Location location;
    private final LocationSource reader;
    private final int LOC_PRECISION = 10, SPD_PRECISION = 4, ALT_PRECISION = 6;

    private final StringItem time = new StringItem("", "hhmmss m/s alt");
    private final StringItem position = new StringItem("", "lat,lon");
    private final StringItem status = new StringItem("", "");
    private final TextField description = new TextField("", "", 64, TextField.ANY);
}
