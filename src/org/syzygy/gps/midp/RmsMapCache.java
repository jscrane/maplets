package org.syzygy.gps.midp;

import org.syzygy.gps.CoordinateMapper;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import java.io.*;

/**
 * RmsMapCache is a cache which uses the Record Management System
 * to store maps.
 * NOTE: this cache has no eviction policy yet, the size of the RMS
 * can grow without bound.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
class RmsMapCache
        extends NoMapCache
{
    /**
     * @return null if the map doesn't exist in the cache
     */
    public byte[] get(CoordinateMapper.Tile tile)
            throws IOException
    {
        String name = tile.toString();
        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore(rsName, true);
            RecordEnumeration e = rs.enumerateRecords(null, null, false);
            while (e.hasNextElement()) {
                byte[] rec = e.nextRecord();
                ByteArrayInputStream bais = new ByteArrayInputStream(rec);
                DataInputStream input = new DataInputStream(bais);
                String s = input.readUTF();
                if (s.equals(name)) {
                    int n = input.available(); // FIXME: ???
                    byte[] map = new byte[n];
                    input.read(map);
                    input.close();
                    bais.close();
                    System.out.println("found " + name);
                    return map;
                }
                input.close();
                bais.close();
            }
        } catch (RecordStoreException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.closeRecordStore();
            } catch (RecordStoreException e) {
                //
            }
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
            String name = tile.toString();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(baos);
            output.writeUTF(name);
            output.write(map, 0, map.length);
            output.close();
            byte[] rec = baos.toByteArray();
            baos.close();

            // FIXME: could maintain a hash of names to RS ids here
            RecordStore rs = null;
            try {
                rs = RecordStore.openRecordStore(rsName, true);
                rs.addRecord(rec, 0, rec.length);
                System.out.println("added " + name);
            } catch (RecordStoreException e) {
                e.printStackTrace();
            } finally {
                try {
                    rs.closeRecordStore();
                } catch (RecordStoreException e) {
                    //
                }
            }
        }
        return map;
    }

    /**
     * Constructs a new cache
     *
     * @param rsName the name of the Record Store which backs the cache
     */
    public RmsMapCache(String rsName)
    {
        this.rsName = rsName;
    }

    private final String rsName;
}
