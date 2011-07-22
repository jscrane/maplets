package org.syzygy.gps;

import org.syzygy.util.WrappedException;

/**
 * A LocationListener is an observer of Location events.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public interface LocationListener
{
    /**
     * Notifies this listener of a new Location.
     *
     * @param location if null, the LocationSource has been stopped
     *                 and this is the last event.
     */
    public void notifyLocation(Location location);

    /**
     * Notifies this listener of signal-strengths associated with the
     * LocationSource.
     *
     * @param signals the signal strengths
     */
    public void notifySignals(int[] signals);

    /**
     * Notifies of an error
     *
     * @param e the exception
     */
    public void notifyError(WrappedException e);
}
