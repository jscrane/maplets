package org.syzygy.gps;

import org.syzygy.util.PropertySource;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;

public class NmeaSourceFactory
{
    public NmeaSource create(PropertySource props) throws Exception
    {
        return create(props.getProperty("gps.receiver.url"), Integer.parseInt(props.getProperty("gps.channels")));
    }

    public NmeaSource create(String url, int channels) throws IOException
    {
        return create((StreamConnection) Connector.open(url), channels);
    }

    public NmeaSource create(StreamConnection conn, int channels)
    {
        return new NmeaSource(conn, channels);
    }
}
