package org.syzygy.gps.midp;

import org.syzygy.gps.CoordinateMapper;
import org.syzygy.gps.MapCache;
import org.syzygy.util.midp.HttpUtil;

import java.io.IOException;

/**
 * NoMapCache is a non-cache which never stores anything, fetching
 * maps when requested.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public class NoMapCache
        implements MapCache
{
    public byte[] fetch(CoordinateMapper.Tile tile)
            throws IOException
    {
        return HttpUtil.get(tile.getURL());
    }

    public byte[] get(CoordinateMapper.Tile tile)
            throws IOException
    {
        return null;
    }
}


