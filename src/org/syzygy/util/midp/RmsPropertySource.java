package org.syzygy.util.midp;

import org.syzygy.util.PropertySource;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotFoundException;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * RmsPropertySource is a PropertySource backed by the Record
 * Management System. Currently it doesn't have a very sophisticated
 * sense of what needs to be saved: it just deletes the store and
 * rewrites everything. Since it's to store application settings,
 * and these shouldn't change very often, this shouldn't be a problem.
 */
public final class RmsPropertySource implements PropertySource
{
    public RmsPropertySource(String store)
    {
        this.store = store;
    }

    public void save() throws Exception
    {
        if (dirty) {
            dirty = false;
            try {
                RecordStore.deleteRecordStore(store);
            } catch (RecordStoreNotFoundException _) {
                // no problem
            }
            RecordStore rs = null;
            try {
                rs = RecordStore.openRecordStore(store, true);
                for (Enumeration e = properties.keys(); e.hasMoreElements();) {
                    byte[] record = writeProperty((String) e.nextElement());
                    rs.addRecord(record, 0, record.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (rs != null)
                    rs.closeRecordStore();
            }
        }
    }

    public void setProperty(String key, String value)
    {
        String old = (String) properties.get(key);
        properties.put(key, value);
        dirty = dirty || old == null || !old.equals(value);
    }

    public String getProperty(String key) throws Exception
    {
        if (properties.isEmpty()) {
            RecordStore rs = null;
            try {
                rs = RecordStore.openRecordStore(store, false);
                RecordEnumeration e = rs.enumerateRecords(null, null, false);
                while (e.hasNextElement())
                    readProperty(e.nextRecord());
            } catch (RecordStoreNotFoundException _) {
                return null;
            } finally {
                if (rs != null)
                    rs.closeRecordStore();
            }
        }
        return (String) properties.get(key);
    }

    void readProperty(byte[] record) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(record);
        DataInputStream input = new DataInputStream(bais);
        String key = input.readUTF();
        String value = input.readUTF();
        properties.put(key, value);
        input.close();
    }

    byte[] writeProperty(String key) throws IOException
    {
        String value = (String) properties.get(key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(baos);
        output.writeUTF(key);
        output.writeUTF(value);
        output.close();
        return baos.toByteArray();
    }

    private final Hashtable properties = new Hashtable();
    private final String store;
    private boolean dirty = false;
}
