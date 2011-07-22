package org.syzygy.gps.midp;

import org.syzygy.gps.*;

/**
 * Displays location obtained from a GPS over a map
 * retrieved from the Internet.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public final class MappingMIDlet extends LocationAwareMIDlet
{
    public MappingMIDlet() throws Exception
    {
        String mapperName = props.getProperty("gps.coordinate.mapper");
        CoordinateMapper cm = (CoordinateMapper) Class.forName(mapperName).newInstance();
        this.location = new LocationCanvas(cm, new EventListener()
        {
            public void pan(final int x, final int y)
            {
                new Thread(new ExceptionHandler("pan")
                {
                    public void doRun() throws Exception
                    {
                        location.setTile(x == 0 && y == 0 ? map.panOff() : map.pan(x, y));
                    }
                }).start();
            }

            public void speedUp()
            {
            }

            public void slowDown()
            {
            }

            public void zoomIn()
            {
                new Thread(new ExceptionHandler("zoomIn")
                {
                    public void doRun() throws Exception
                    {
                        location.setTile(map.zoomIn());
                    }
                }).start();
            }

            public void zoomOut()
            {
                new Thread(new ExceptionHandler("zoomOut")
                {
                    public void doRun() throws Exception
                    {
                        location.setTile(map.zoomOut());
                    }
                }).start();
            }

            public void exit()
            {
                quit(false);
            }
        });
        cm.setMapCache(new FileMapCache(props.getProperty("gps.midlet.maps"), cm));
        this.map = new PanningZoomingMapper(cm, 12);
    }

    protected void startApp()
    {
        location.setFullScreenMode(true);
        reader.addListener(new ErrorListenerAdapter()
        {
            protected void onLocation(Location l)
            {
                try {
                    location.setTile(map.fromLocation(l));
                } catch (Exception e) {
                    e.printStackTrace();
                    error(e, "onLocation");
                }
                location.setLocation(l);
            }
        });
        display.setCurrent(location);
        new Thread(reader).start();
    }

    private final LocationCanvas location;
    private final PanningZoomingMapper map;
}
