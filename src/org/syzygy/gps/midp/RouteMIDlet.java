package org.syzygy.gps.midp;

import org.syzygy.gps.ErrorListenerAdapter;
import org.syzygy.gps.GeoName;
import org.syzygy.gps.Location;
import org.syzygy.gps.Util;
import org.syzygy.util.WrappedException;
import org.syzygy.util.midp.FileUtil;

import javax.microedition.lcdui.*;
import java.io.IOException;
import java.util.Vector;

public final class RouteMIDlet extends LocationAwareMIDlet
{
    public RouteMIDlet() throws Exception
    {
        this.form = new Form("Routelet");
        final String saveDir = props.getProperty("gps.midlet.traces");

        final Form saver = new Form("Save Route");
        final TextField file = new TextField("File:", "", 16, TextField.ANY);
        final Command ok = new Command("Save", Command.SCREEN, 1);
        final Command cancel = new Command("Quit", Command.SCREEN, 1);
        final ChoiceGroup overwrite = new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
        overwrite.append("overwrite", null);
        saver.append(file);
        saver.append(overwrite);
        saver.addCommand(ok);
        saver.addCommand(cancel);
        saver.setCommandListener(new CommandListener()
        {
            public void commandAction(Command c, Displayable d)
            {
                if (c == ok)
                    try {
                        String fileName = file.getString();
                        boolean[] sel = new boolean[1];
                        boolean ow = overwrite.getSelectedFlags(sel) == 1;
                        saver.removeCommand(ok);
                        saver.removeCommand(cancel);
                        saver.deleteAll();
                        saver.append(new StringItem("Saving", fileName));
                        Vector p = listener.getPositions();
                        Gauge gauge = new Gauge(null, true, p.size(), 0);
                        saver.append(gauge);
                        FileUtil.save(gauge, saveDir + "/" + fileName, p.elements(), ow);
                    } catch (IOException ex) {
                        message(ex, "Save", "Error: ", false);
                        return;
                    }
                quit(true);
            }
        });

        form.setCommandListener(new CommandListener()
        {
            public void commandAction(Command c, Displayable d)
            {
                if (c == start) {
                    form.removeCommand(start);
                    listener.startRecording();
                } else if (c == locate) {
                    reader.stop();
                    locate();
                } else if (c == exit) {
                    reader.stop();
                    if (listener.getPositions().size() > 0) {
                        String l = location.getText();
                        if (!"".equals(l))
                            file.setString(l);
                        display.setCurrent(saver);
                        return;
                    }
                    quit(true);
                }
            }
        });
        this.time = new StringItem("Time/Sat", "");
        this.speed = new StringItem("Spd/Max", "");
        this.position = new StringItem("Lat/Lon", "");
        this.location = new StringItem("Location", "");
        this.status = new StringItem("", "");

        reader.addListener(listener);
    }

    private class Listener
            extends ErrorListenerAdapter
    {
        protected void onLocation(Location l)
        {
            time.setText(l.getGpsTime() + " " + sats + (l.getIsError() ? "E" : ""));
            boolean changed = spd != l.getSpeed() || lat != l.getLatitude() || lng != l.getLongitude();
            if (spd != l.getSpeed() || !connected) {
                spd = l.getSpeed();
                if (spd > smax)
                    smax = spd;
                speed.setText(Util.formatDouble(spd, 5, "0") + " " +
                        Util.formatDouble(smax, 5, "0"));
            } else if (changed) {
                lat = l.getLatitude();
                lng = l.getLongitude();
                position.setText(Util.formatCoords(lat, lng, 10));
            }
            if (changed) {
                if (started)
                    positions.addElement(l.toString());
                status.setText(Integer.toString(positions.size()));
            }
            connected = true;
        }

        protected void onClose()
        {
            if (!error)
                status.setText(connected ? "Connection closed" : "Connect failed");
        }

        protected void onSatellites(int s)
        {
            sats = s;
        }

        public void notifyError(WrappedException e)
        {
            error = true;
            status.setLabel(e.getWrapped().toString());
            status.setText(e.getMessage());
        }

        double getLatitude()
        {
            return lat;
        }

        double getLongitude()
        {
            return lng;
        }

        Vector getPositions()
        {
            return positions;
        }

        void startRecording()
        {
            started = true;
        }

        private double lat, lng, spd, smax;
        private int sats;
        private boolean connected, started, error;
        private final Vector positions = new Vector();
    }

    private void locate()
    {
        try {
            GeoName name = new GeoNameFactory().fromDefaultURL(listener.getLatitude(), listener.getLongitude());
            if (name != null) {
                form.append(location);
                location.setText(name.getName());
                form.removeCommand(locate);
            }
        } catch (Exception e) {
            message(e, "Error: ", "Locate", false);
        }
    }

    protected void startApp()
    {
        form.append(time);
        form.append(speed);
        form.append(position);
        form.append(status);

        form.addCommand(start);
        form.addCommand(locate);
        form.addCommand(exit);

        display.setCurrent(form);
        new Thread(reader).start();
    }

    private final Form form;
    private final Command start = new Command("Start", Command.SCREEN, 1);
    private final Command locate = new Command("Locate", Command.SCREEN, 1);
    private final StringItem time, speed, location, position, status;
    private final Listener listener = new Listener();
}
