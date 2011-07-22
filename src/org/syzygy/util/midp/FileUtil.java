package org.syzygy.util.midp;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Gauge;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;

public class FileUtil extends StreamUtil
{
    public static FileConnection open(String fileName, boolean overwrite) throws IOException
    {
        FileConnection file = (FileConnection) Connector.open(fileName, Connector.WRITE);
        if (overwrite && file.exists()) {
            try {
                file.delete();
                file.close();
            } catch (IOException _) {
                //
            }
            file = (FileConnection) Connector.open(fileName, Connector.WRITE);
        }
        return file;
    }

    public static void save(String fileName, String string, boolean overwrite) throws IOException
    {
        FileConnection file = open(fileName, overwrite);
        PrintStream out = null;
        try {
            file.create();
            out = new PrintStream(file.openOutputStream());
            out.println(string);
        } finally {
            safeClose(out);
            safeClose(file);
        }
    }

    public static void save(Gauge gauge, String fileName, Enumeration points, boolean overwrite) throws IOException
    {
        FileConnection file = open(fileName, overwrite);
        PrintStream out = null;
        try {
            file.create();
            out = new PrintStream(file.openOutputStream());
            for (int i = 0; points.hasMoreElements(); i++) {
                out.println((String) points.nextElement());
                gauge.setValue(i);
            }
        } finally {
            safeClose(out);
            safeClose(file);
        }
    }

    public static void createDirectory(String path) throws IOException
    {
        FileConnection dir = (FileConnection) Connector.open(path, Connector.READ);
        if (!dir.exists()) {
            safeClose(dir);
            dir = (FileConnection) Connector.open(path, Connector.WRITE);
            dir.mkdir();
        }
        safeClose(dir);
    }
}
