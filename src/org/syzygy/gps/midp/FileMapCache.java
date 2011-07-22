package org.syzygy.gps.midp;

import org.syzygy.gps.CoordinateMapper;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FileMapCache is a cache which uses the File Connection API (JSR-75)
 * to store maps.
 * NOTE: this cache has no eviction policy yet, it can grow without bound.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
class FileMapCache
        extends NoMapCache
{
    private FileConnection getFile(CoordinateMapper.Tile tile, int mode)
            throws IOException
    {
        String name = tile.toString();
        return (FileConnection) Connector.open(dirName + "/" + name, mode);
    }

    /**
     * @return null if the map doesn't exist in the cache
     */
    public byte[] get(CoordinateMapper.Tile tile)
            throws IOException
    {
        FileConnection file = getFile(tile, Connector.READ);
        if (file.exists()) {
            byte[] buf = new byte[(int) file.fileSize()];
            InputStream input = file.openInputStream();
            try {
                input.read(buf);
            } finally {
                input.close();
            }
            return buf;
        }
        return null;
    }

    /**
     * Fetches a new map for the cache, evicting an existing entry
     * if necessary.
     */
    public byte[] fetch(CoordinateMapper.Tile tile)
            throws IOException
    {
        byte[] map = super.fetch(tile);
        if (map != null) {
            FileConnection file = getFile(tile, Connector.WRITE);
            file.create();
            OutputStream output = file.openOutputStream();
            output.write(map);
            output.close();
        }
        return map;
    }

    /**
     * Constructs a new cache
     *
     * @param dirName          the name of the directory in which the cache resides
     * @param coordinateMapper a converter of (lat, lon) to map indices
     */
    public FileMapCache(String dirName, CoordinateMapper coordinateMapper)
    {
        this.dirName = dirName + "/" + coordinateMapper.getName();
    }

    private final String dirName;
}
