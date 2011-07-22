package org.syzygy.gps.mappers;

import org.syzygy.gps.CoordinateMapper;

/**
 * GoogleMapper is a CoordinateMapper which understands v2 of the Google "staticmaps" protocol.
 * http://code.google.com/apis/maps/documentation/staticmaps
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
abstract class GoogleStaticMapper extends CoordinateMapper
{
    private final String key = "ABQIAAAAR8skPe7CdL5Ol5pgOfYJMxRTok7fsBrusmuD5JD6Z3dn5fUn3xR4_LfuKT7j5OsZ_nK9AW-RyLBygg";

    public GoogleStaticMapper()
    {
        super(256, 21);
    }

    private final class GoogleTile extends Tile
    {
        GoogleTile(int tileX, int tileY, int zoom)
        {
            super(tileX, tileY, zoom);
        }

        public String getURL()
        {
            return "http://maps.google.com/maps/api/staticmap?center=" +
                    getLatitude(getTileY() + 0.5, getZoom()) + "," + getLongitude(getTileX() + 0.5, getZoom()) +
                    "&zoom=" + getZoom() + "&size=" + getTileSize() + "x" + getTileSize() +
                    "&maptype=" + mapType() + "&sensor=true&key=" + key;
        }
    }

    protected Tile makeTile(int tileX, int tileY, int zoom)
    {
        return new GoogleTile(tileX, tileY, zoom);
    }

    protected abstract String mapType();

    public String getName()
    {
        return "google_" + mapType();
    }
}
