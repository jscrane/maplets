package org.syzygy.gps;

import java.io.IOException;

/**
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public interface MapCache
{
    public byte[] get(CoordinateMapper.Tile t) throws IOException;

    public byte[] fetch(CoordinateMapper.Tile t) throws IOException;
}
    
