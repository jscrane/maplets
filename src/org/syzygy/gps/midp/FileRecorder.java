package org.syzygy.gps.midp;

import org.syzygy.gps.Location;
import org.syzygy.gps.LocationListener;
import org.syzygy.util.WrappedException;
import org.syzygy.util.midp.StreamUtil;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import java.io.DataOutputStream;
import java.io.IOException;

final class FileRecorder implements LocationListener
{
    public FileRecorder(String fileName) throws IOException
    {
        this.file = (FileConnection) Connector.open(fileName, Connector.WRITE);
        if (!file.exists())
            file.create();
        this.output = file.openDataOutputStream();
    }

    public void notifyLocation(Location location)
    {
        if (location == null)
            close();
        else
            try {
                output.writeUTF(location.toString());
            } catch (IOException _) {
                // bleah
            }
    }

    public void notifySignals(int[] signals)
    {
    }

    public void notifyError(WrappedException e)
    {
    }

    private void close()
    {
        StreamUtil.safeClose(output);
        StreamUtil.safeClose(file);
    }

    private final FileConnection file;
    private final DataOutputStream output;
}    
