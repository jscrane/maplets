package org.syzygy.gps;

import org.syzygy.util.WrappedException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A LocationSource provides a stream of Location beans (and signal
 * strengths where appropriate) to interested observers
 * (LocationListeners or ErrorListeners).
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public abstract class LocationSource implements Runnable
{
    public void addListener(LocationListener listener)
    {
        if (listener instanceof ErrorListener)
            notifyErrors = true;
        listeners.addElement(listener);
    }

    public void clearListeners()
    {
        listeners.removeAllElements();
    }

    protected void notifyLocation(Location location)
    {
        boolean always = location == null || !location.getIsError();
        if (always || notifyErrors)
            for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
                LocationListener l = (LocationListener) e.nextElement();
                if (always || (l instanceof ErrorListener))
                    l.notifyLocation(location);
            }
    }

    protected void notifySignals(int[] signals)
    {
        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
            LocationListener l = (LocationListener) e.nextElement();
            l.notifySignals(signals);
        }
    }

    protected void notifyError(WrappedException ex)
    {
        for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
            LocationListener l = (LocationListener) e.nextElement();
            l.notifyError(ex);
        }
    }

    public abstract void run();

    public void stop()
    {
        this.stopped = true;
    }

    public boolean isStopped()
    {
        return stopped;
    }

    private boolean notifyErrors = false;
    private final Vector listeners = new Vector();
    private volatile boolean stopped = false;
}
