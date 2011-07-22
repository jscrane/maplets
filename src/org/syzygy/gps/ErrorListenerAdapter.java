package org.syzygy.gps;

import org.syzygy.util.WrappedException;

public abstract class ErrorListenerAdapter implements ErrorListener
{
    protected abstract void onLocation(Location l);

    protected void onClose()
    {
    }

    public final void notifyLocation(Location l)
    {
        if (l == null)
            onClose();
        else
            onLocation(l);
    }

    // visible satellites
    protected void onSatellites(int satellites)
    {
    }

    public void notifySignals(int[] signals)
    {
        int satellites = 0;
        for (int i = 0; i < signals.length; i++)
            if (signals[i] > 0)
                satellites++;
        onSatellites(satellites);
    }

    public void notifyError(WrappedException e)
    {
    }
}
