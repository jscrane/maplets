package org.syzygy.gps;

/**
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public final class PanningZoomingMapper
{
    public PanningZoomingMapper(CoordinateMapper mapper, int zoom)
    {
        this.mapper = mapper;
        this.lastTile = mapper.getTile(0, 0, zoom);
    }

    public CoordinateMapper.Tile panOff()
    {
        if (!isPanning())
            return lastTile;
        CoordinateMapper.Tile tile = mapper.getTile(panned.getLatitude(), panned.getLongitude(), lastTile.getZoom());
        return update(tile, panned);
    }

    public CoordinateMapper.Tile pan(int x, int y)
    {
        CoordinateMapper.Tile tile = lastTile.pan(x, y);
        int zoom = lastTile.getZoom();
        Location location = new Location();
        location.setLongitude(mapper.getLongitude(tile.getTileX(), zoom));
        location.setLatitude(mapper.getLatitude(tile.getTileY(), zoom));
        if (!isPanning() && !lastTile.equals(tile))
            panned = current;
        return update(tile, location);
    }

    public CoordinateMapper.Tile zoomIn()
    {
        CoordinateMapper.Tile tile = lastTile.zoomIn(0, 0);
        if (tile != lastTile) {
            tile = mapper.getTile(current.getLatitude(), current.getLongitude(), tile.getZoom());
            update(tile, current);
        }
        return lastTile;
    }

    public CoordinateMapper.Tile zoomOut()
    {
        return update(lastTile.zoomOut(), current);
    }

    public CoordinateMapper.Tile fromLocation(Location location)
    {
        CoordinateMapper.Tile tile = mapper.getTile(location.getLatitude(), location.getLongitude(), lastTile.getZoom());
        if (isPanning()) {
            if (!tile.equals(lastTile)) {
                panned = new Location(location);
                return lastTile;
            }
        }
        return update(tile, location);
    }

    private CoordinateMapper.Tile update(CoordinateMapper.Tile tile, Location location)
    {
        if (tile.equals(lastTile) && current.samePosition(location))
            return lastTile;

        if (!tile.equals(lastTile))
            lastTile = tile;
        if (!current.samePosition(location))
            current = new Location(location);
        if (isPanning()) {
            CoordinateMapper.Tile t = mapper.getTile(panned.getLatitude(), panned.getLongitude(), tile.getZoom());
            // if the actual location is on the same tile turn off panning
            if (tile.equals(t))
                panned = null;
        }
        return lastTile;
    }

    public boolean isPanning()
    {
        return panned != null;
    }

    private Location panned = null, current = new Location();
    private CoordinateMapper.Tile lastTile;
    private final CoordinateMapper mapper;
}