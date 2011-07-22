package org.syzygy.gps;

import java.io.IOException;

/**
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public abstract class CoordinateMapper
{
    private final int tileSize;
    private final int maxZoom;
    private final MercatorProjection projection;
    private MapCache maps;

    protected CoordinateMapper(int tileSize, int maxZoom)
    {
        this.tileSize = tileSize;
        this.maxZoom = maxZoom;
        this.projection = new MercatorProjection(tileSize, maxZoom);
    }

    public void setMapCache(MapCache maps)
    {
        this.maps = maps;
    }

    public int getScreenY(Tile tile, double latitude, int height)
    {
        double y = projection.mapLatitude(latitude, tile.getZoom());
        return (int) ((y - tile.getTileY()) * (double) height);
    }

    public int getScreenX(Tile tile, double longitude, int width)
    {
        double x = projection.mapLongitude(longitude, tile.getZoom());
        return (int) ((x - tile.getTileX()) * (double) width);
    }

    public double getLatitude(double y, int zoom)
    {
        return projection.getLatitude(y, zoom);
    }

    public double getLongitude(double x, int zoom)
    {
        return projection.getLongitude(x, zoom);
    }

    public int getMaxZoom()
    {
        return maxZoom;
    }

    public int getTileSize()
    {
        return tileSize;
    }

    protected abstract Tile makeTile(int tileX, int tileY, int zoom);

    public abstract String getName();

    public Tile getTile(double latitude, double longitude, int zoom)
    {
        return makeTile((int) Math.floor(projection.mapLongitude(longitude, zoom)), (int) Math.floor(projection.mapLatitude(latitude, zoom)), zoom);
    }

    public Tile getTile(String s)
    {
        int start = 0, end = s.indexOf('_');
        int x = new Integer(s.substring(start, end)).intValue();
        start = end + 1;
        end = s.indexOf('_', start);
        int y = new Integer(s.substring(start, end)).intValue();
        int zoom = new Integer(s.substring(end + 1)).intValue();
        return makeTile(x, y, zoom);
    }

    public double getLongitudinalDistance(Tile lastTile)
    {
        int x = lastTile.getTileX(), zoom = lastTile.getZoom();
        double dlon = projection.getLongitude(x + 1, zoom) - projection.getLongitude(x, zoom);
        double lat = projection.getLatitude(lastTile.getTileY(), zoom);
        return projection.getLongitudinalDistance(lat, dlon);
    }

    public double getLatitudinalDistance(Tile lastTile)
    {
        int y = lastTile.getTileY(), zoom = lastTile.getZoom();
        double dlat = projection.getLatitude(y, zoom) - projection.getLatitude(y + 1, zoom);
        return projection.getLatitudinalDistance(dlat);
    }

    public abstract class Tile
    {
        private final int zoom;
        private final int tileX;
        private final int tileY;

        protected Tile(int tileX, int tileY, int zoom)
        {
            this.zoom = zoom;
            this.tileY = tileY;
            this.tileX = tileX;
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof Tile))
                return false;
            Tile t = (Tile) o;
            return t.getTileX() == getTileX() && t.getTileY() == getTileY() && t.getZoom() == getZoom();
        }

        public int getTileX()
        {
            return tileX;
        }

        public int getTileY()
        {
            return tileY;
        }

        public int getZoom()
        {
            return zoom;
        }

        public abstract String getURL();

        public String toString()
        {
            return getTileX() + "_" + getTileY() + "_" + getZoom();
        }

        public Tile zoomOut()
        {
            if (getZoom() == 0)
                return this;
            return makeTile(getTileX() / 2, getTileY() / 2, getZoom() - 1);
        }

        public Tile zoomIn(int dx, int dy)
        {
            if (getZoom() == getMaxZoom())
                return this;
            return makeTile(getTileX() * 2 + dx, getTileY() * 2 + dy, getZoom() + 1);
        }

        public Tile pan(int x, int y)
        {
            return makeTile(getTileX() + x, getTileY() + y, getZoom());
        }

        public byte[] getMap() throws IOException
        {
            byte[] mapData = maps.get(this);

            if (mapData == null)
                mapData = maps.fetch(this);

            if (mapData == null)
                mapData = zoomOut().getMap();

            return mapData;
        }
    }
}
