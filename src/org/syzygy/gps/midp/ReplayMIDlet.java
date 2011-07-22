package org.syzygy.gps.midp;

import org.syzygy.gps.*;
import org.syzygy.util.PropertySource;
import org.syzygy.util.midp.FileUtil;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Replays a previously-saved trace over a map retrieved from the Internet.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public abstract class ReplayMIDlet extends LocationAwareMIDlet
{
    protected ReplayMIDlet(String name) throws Exception
    {
        String mapperName = props.getProperty("gps.coordinate.mapper");
        String cacheName = props.getProperty("gps.midlet.maps");
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

            public void speedUp()
            {
                new Thread(new ExceptionHandler("speedUp")
                {
                    public void doRun() throws Exception
                    {
                        ((ReplaySource) reader).speedUp();
                    }
                }).start();
            }

            public void slowDown()
            {
                new Thread(new ExceptionHandler("slowDown")
                {
                    public void doRun() throws Exception
                    {
                        ((ReplaySource) reader).slowDown();
                    }
                }).start();
            }

            public void exit()
            {
                quit(false);
            }
        });
        this.map = new PanningZoomingMapper(cm, 12);
        cm.setMapCache(new FileMapCache(cacheName, cm));
        this.name = name;
    }

    protected abstract String getLocations();

    protected LocationSource createSource(PropertySource props)
    {
        return new ReplaySource();
    }

    protected void startApp()
    {
        location.setFullScreenMode(true);
        reader.addListener(new ErrorListenerAdapter()
        {
            protected void onLocation(Location l)
            {
                if (!l.getIsError())
                    try {
                        location.setTile(map.fromLocation(l));
                    } catch (Exception e) {
                        error(e, "onLocation");
                    }
                location.setLocation(l);
            }
        });

        final Command ok = new Command("OK", Command.SCREEN, 1);
        final Command delete = new Command("Delete", Command.SCREEN, 1);
        final List choices = new List(name, ChoiceGroup.IMPLICIT);
        FileConnection traceDir = null;
        try {
            traceDir = (FileConnection) Connector.open(getLocations(), Connector.READ);
            for (Enumeration files = traceDir.list(); files.hasMoreElements();)
                choices.append((String) files.nextElement(), null);
        } catch (IOException e) {
            FileUtil.safeClose(traceDir);
            error(e, "Loading");
        }

        choices.addCommand(ok);
        choices.addCommand(delete);
        choices.setSelectCommand(ok);

        choices.setCommandListener(new CommandListener()
        {
            public void commandAction(Command c, Displayable d)
            {
                final int sel = choices.getSelectedIndex();
                if (sel == -1)
                    quit(true);
                else {
                    final String choice = getLocations() + "/" + choices.getString(sel);
                    if (c == ok) {
                        ((ReplaySource) reader).setPath(choice);
                        display.setCurrent(location);
                        new Thread(reader).start();
                    } else if (c == delete) {
                        new Thread(new ExceptionHandler("delete")
                        {
                            public void doRun() throws Exception
                            {
                                FileConnection file = (FileConnection) Connector.open(choice);
                                try {
                                    file.delete();
                                    choices.delete(sel);
                                } finally {
                                    file.close();
                                }
                            }
                        }).start();
                    }
                }
            }
        });

        display.setCurrent(choices);
    }

    private final LocationCanvas location;
    private final String name;

    private PanningZoomingMapper map;
}
