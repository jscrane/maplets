package org.syzygy.gps.mappers;

import org.syzygy.gps.CoordinateMapper;

/**
 * OpenStreetMapper is a CoordinateMapper which understands Slippy Map
 * tilenames: http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public final class OpenStreetMapper extends CoordinateMapper
{
    public OpenStreetMapper()
    {
        super(256, 18);
    }

    private final class SlippyTile extends Tile
    {
        SlippyTile(int tileX, int tileY, int zoom)
        {
            super(tileX, tileY, zoom);
        }

        public String getURL()
        {
            return "http://tile.openstreetmap.org/" + getZoom() + "/" + getTileX() + "/" + getTileY() + ".png";
        }
    }

    protected Tile makeTile(int tileX, int tileY, int zoom)
    {
        return new SlippyTile(tileX, tileY, zoom);
    }

    public String getName()
    {
        return "open_street_maps";
    }
}
