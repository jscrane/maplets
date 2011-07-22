package org.syzygy.gps.mappers;

import org.syzygy.gps.CoordinateMapper;

/**
 * MicrosoftHybridMapper is a CoordinateMapper for Microsoft hybrid images
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public final class MicrosoftHybridMapper extends CoordinateMapper
{
    public MicrosoftHybridMapper()
    {
        super(256, 18);
    }

    protected Tile makeTile(int x, int y, int zoom)
    {
        return new MicrosoftTile(x, y, zoom);
    }

    private final class MicrosoftTile extends Tile
    {
        MicrosoftTile(int x, int y, int zoom)
        {
            super(x, y, zoom);
        }

        public String getURL()
        {
            return "http://h1.ortho.tiles.virtualearth.net/tiles/h" + getMapName() + ".png?g=45";
        }

        private String getMapName()
        {
            MicrosoftTile n = (MicrosoftTile) zoomOut();
            if (n == this)
                return "";
            int rx = getTileX() - n.getTileX() * 2, ry = getTileY() - n.getTileY() * 2;
            String s;
            if (rx == 0)
                s = ry == 0 ? "0" : "2";
            else
                s = ry == 0 ? "1" : "3";
            return n.getMapName() + s;
        }
    }

    public String getName()
    {
        return "msoft_hybrid";
    }
}