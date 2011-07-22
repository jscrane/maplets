package org.syzygy.gps.midp;

import org.syzygy.gps.CoordinateMapper;
import org.syzygy.gps.EventListener;
import org.syzygy.gps.Location;
import org.syzygy.gps.Util;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.IOException;


final class LocationCanvas extends Canvas
{
    public LocationCanvas(CoordinateMapper mapper, EventListener listener)
    {
        this.listener = listener;
        this.mapper = mapper;
    }

    protected void keyPressed(int keyCode)
    {
        switch ((char) keyCode) {
            case '0':
                listener.exit();
                return;
            case '1':
                painters[ZOOM] = new Zoomer("Zoom in from " + tile.getZoom());
                listener.zoomIn();
                return;
            case '2':
                painters[ZOOM] = new Zoomer("Zoom out from " + tile.getZoom());
                listener.zoomOut();
                return;
            case '4':
                listener.slowDown();
                return;
            case '5':
                listener.speedUp();
                return;
        }
        int action = getGameAction(keyCode);
        if (action != 0) {
            switch (action) {
                case FIRE:
                    listener.pan(0, 0);
                    break;
                case UP:
                    listener.pan(0, -1);
                    break;
                case DOWN:
                    listener.pan(0, 1);
                    break;
                case RIGHT:
                    listener.pan(1, 0);
                    break;
                case LEFT:
                    listener.pan(-1, 0);
                    break;
            }
        }
    }

    public void setTile(CoordinateMapper.Tile t) throws IOException
    {
        if (!t.equals(tile)) {
            tile = t;
            byte[] mapData = tile.getMap();
            Image map = Image.createImage(mapData, 0, mapData.length);
            mw = map.getWidth();
            mh = map.getHeight();
            painters[MAP] = new ImagePainter(map, mapper.getLongitudinalDistance(tile));
            updateXY();
            repaint();
        }
    }

    public void setLocation(Location l)
    {
        if (!l.samePosition(location)) {
            location = l;
            painters[POS] = new PositionPainter(l);
            painters[INFO] = new InfoPainter(l);
            updateXY();
            repaint();
        }
    }

    private void updateXY()
    {
        if (tile != null && location != null) {
            x = mapper.getScreenX(tile, location.getLongitude(), mw);
            y = mapper.getScreenY(tile, location.getLatitude(), mh);
        }
    }

    private int translateX()
    {
        int vw = getWidth();
        return mw > vw && x > vw ? vw - mw : 0;
    }

    private int translateY(int fh)
    {
        int vh = getHeight() - fh;
        return mh > vh && y > vh ? vh - mh : 0;
    }

    private interface Painter
    {
        void paint(Graphics g);
    }

    private final class Zoomer implements Painter
    {
        private final String label;
        private final Painter map;

        public Zoomer(String label)
        {
            this.label = label;
            this.map = painters[MAP];
        }

        public void paint(Graphics g)
        {
            Font f = Font.getDefaultFont();
            int w = f.stringWidth(label);
            int h = f.getHeight();
            int c = g.getColor();
            g.setColor(0, 0, 0);
            g.fillRect(0, 0, w, h);
            g.setColor(c);
            g.drawString(label, 0, 0, Graphics.TOP | Graphics.LEFT);
            if (map != painters[MAP])
                painters[ZOOM] = null;
        }
    }

    private final class ImagePainter implements Painter
    {
        private final Image map;
        private final double mapWidth;

        public ImagePainter(Image map, double mapWidth)
        {
            this.map = map;
            this.mapWidth = mapWidth;
        }

        public void paint(Graphics g)
        {
            int fontHeight = g.getFont().getHeight();
            g.drawImage(map, translateX(), translateY(g.getFont().getHeight()), Graphics.TOP | Graphics.LEFT);

            double dx = mapWidth / 2;
            double d = round(dx);
            String u = d >= 1000 ? "km" : "m";
            int v = (int) (d >= 1000 ? d / 1000 : d);
            int dd = (int) ((d / dx * (int) mw / 2));
            g.setColor(0, 0, 255);
            int ox = fontHeight, ex = ox + dd, oy = fontHeight;
            g.drawLine(ox, oy, ex, oy);
            g.drawLine(ox, oy - fontHeight / 2, ox, oy + fontHeight / 2);
            g.drawLine(ex, oy - fontHeight / 2, ex, oy + fontHeight / 2);
            g.drawString(v + u, ex, oy, Graphics.BOTTOM | Graphics.HCENTER);
        }

        private double round(double x)
        {
            int e = 0;
            for (; x > 10; e++)
                x /= 10;
            x = Math.floor(x);
            while (e-- > 0)
                x *= 10;
            return x;
        }
    }

    private final class InfoPainter implements Painter
    {
        private final String time;
        private final boolean isError;
        private final String speed, altitude;

        public InfoPainter(Location location)
        {
            this.time = location.getGpsTime();
            this.isError = location.getIsError();
            this.speed = Util.formatDouble(location.getSpeedKmh(), 5, "");
            this.altitude = Util.formatDouble(location.getAltitude(), 5, "");
        }

        public void paint(Graphics g)
        {
            g.setColor(0, 0, 0);
            int fontHeight = g.getFont().getHeight();
            int w = getWidth(), x = w / 2, h = getHeight();
            g.fillRect(0, h - fontHeight, w, fontHeight);
            g.setColor(255, 255, 255);
            if (time != null)
                g.drawString(time + (isError ? "E" : " "), 0, h, Graphics.LEFT | Graphics.BOTTOM);
            if (speed != null)
                g.drawString(speed + "kmh", x, h, Graphics.HCENTER | Graphics.BOTTOM);
            if (altitude != null)
                g.drawString(altitude + "m", w, h, Graphics.RIGHT | Graphics.BOTTOM);
        }
    }

    private final class PositionPainter implements Painter
    {
        private final Location location;
        private final int DIA = 4, LEN = 10;

        public PositionPainter(Location location)
        {
            this.location = location;
        }

        public void paint(Graphics g)
        {
            g.setColor(255, 0, 0);
            int tx = translateX();
            int ty = translateY(g.getFont().getHeight());
            g.translate(tx, ty);

            double orientation = Math.toRadians(location.getCourse());
            g.fillArc(x - DIA / 2, y - DIA / 2, DIA, DIA, 0, 360);
            int lx = (int) (LEN * Math.sin(orientation)), ly = (int) (LEN * Math.cos(orientation));
            g.drawLine(x, y, x + lx, y - ly);
        }
    }

    protected void paint(Graphics g)
    {
        for (int i = 0; i < painters.length; i++)
            if (painters[i] != null)
                painters[i].paint(g);
    }

    private CoordinateMapper.Tile tile;
    private Location location;
    private int mw, mh;
    private int x, y;

    private final EventListener listener;
    private final CoordinateMapper mapper;

    private final Painter[] painters = new Painter[4];
    private final int MAP = 0;
    private final int ZOOM = 1;
    private final int INFO = 2;
    private final int POS = 3;
}
