package org.syzygy.gps;

import junit.framework.TestCase;
import org.syzygy.util.WrappedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestNmeaSource extends TestCase
{
    protected void setUp() throws Exception
    {
        this.nmea = new NmeaSource(null, 12);
    }

    public void testChecksum()
    {
        String s = "GPGGA,171332,0000.0000,N,00000.0000,E,0,00,50.0,0.0,M,0.0,M,0.0,0000*6D";
        assertEquals(0x6d, NmeaSource.checksum(s, 0, s.length() - 3));
        s = "GPWPL,4807.0380,N,01131.000,E,WPTNME*6C";
        assertEquals(0x6c, NmeaSource.checksum(s, 0, s.length() - 3));
        s = "GPRMC,100142,V,5320.0138,N,00614.7191,W,0.000000,0,050805,,*3B";
        assertEquals(0x3B, NmeaSource.checksum(s, 0, s.length() - 3));
    }

    public void testChecksum2()
    {
        String s = "GPGLL,5300.97914,N,00259.98174,E,125926,A";
        assertEquals(0x28, NmeaSource.checksum(s, 0, s.length()));
    }

    public void testDecimalize()
    {
        double e = 0.000005;
        assertEquals(53.33356, NmeaSource.decimalize("5320.0138", false), e);
        assertEquals(48.1173, NmeaSource.decimalize("4807.038", false), e);
        assertEquals(11.51667, NmeaSource.decimalize("01131.000", false), e);
    }

    public void testUndecimalize()
    {
        assertEquals("5320.0138", NmeaSource.unDecimalize(53.3335633333, false));
        assertEquals("05320.013", NmeaSource.unDecimalize(53.3335633333, true));
    }

    public void testWaypoint()
    {
        assertEquals("GPWPL,4807.0380,N,01131.000,E,WPTNME*6C",
                NmeaSource.waypoint(48.1173, 11.51667, "WPTNME"));
        // FIXME: test truncation of string to 80 chars
        String e = "GPWPL,4807.0380,N,01131.000,E,longlonglonglonglonglonglonglonglonglonglonglo*70";
        assertEquals(79, e.length());
        assertEquals(e, NmeaSource.waypoint(48.1173, 11.51667, "longlonglonglonglonglonglonglonglonglonglonglonglong"));
    }

    public void testParseGprmcSentence() throws IOException
    {
        String sentence = "GPRMC,220516,A,5133.82,N,00042.24,W,173.8,231.8,130694,004.2,W*70";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals(NmeaSource.decimalize("5133.82", false), location.getLatitude(), delta);
        assertEquals(NmeaSource.decimalize("00042.24", true), location.getLongitude(), delta);
        assertEquals("220516", location.getGpsTime());
        assertFalse(location.getIsError());
        assertEquals(173.8, location.getSpeed(), delta);
        assertEquals(231.8, location.getCourse(), delta);
        assertEquals("130694", location.getUtcDate());
    }

    public void testParseGprmcSentence2() throws IOException
    {
        String sentence = "GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals("123519", location.getGpsTime());
        assertEquals(NmeaSource.decimalize("4807.038", false), location.getLatitude(), delta);
        assertEquals(NmeaSource.decimalize("01131.000", false), location.getLongitude(), delta);
        assertFalse(location.getIsError());
        assertEquals(22.4, location.getSpeed(), delta);
        assertEquals(84.4, location.getCourse(), delta);
        assertEquals("230394", location.getUtcDate());
    }

    public void testParseBadGprmcSentence() throws IOException
    {
        String sentence = "GPRMC,235947.000,V,0000.0000,N,00000.0000,E,,,041299,,*1D";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals("235947", location.getGpsTime());
        assertEquals(0.0, location.getLatitude(), delta);
        assertEquals(0.0, location.getLongitude(), delta);
        assertTrue(location.getIsError());
        assertEquals(0.0, location.getSpeed(), delta);
        assertEquals(0.0, location.getCourse(), delta);
        assertEquals("041299", location.getUtcDate());
    }

    public void testParseGprmcSentence3() throws IOException
    {
        String sentence = "GPRMC,092204.999,A,4250.5589,S,14718.5084,E,0.00,89.68,211200,,*25";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals("092204", location.getGpsTime());
        assertEquals(NmeaSource.decimalize("4250.5589", true), location.getLatitude(), delta);
        assertEquals(NmeaSource.decimalize("14718.5084", false), location.getLongitude(), delta);
        assertFalse(location.getIsError());
        assertEquals(0.0, location.getSpeed(), delta);
        assertEquals(89.68, location.getCourse(), delta);
        assertEquals("211200", location.getUtcDate());
    }

    public void testParseGpggaSentence() throws IOException
    {
        String sentence = "GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals("123519", location.getGpsTime());
        assertEquals(NmeaSource.decimalize("4807.038", false), location.getLatitude(), delta);
        assertEquals(NmeaSource.decimalize("01131.000", false), location.getLongitude(), delta);
        assertFalse(location.getIsError());
        assertEquals(8, location.getSatellites());
        assertEquals(545.4, location.getAltitude(), delta);
    }

    public void testParseBadGpggaSentence() throws IOException
    {
        String sentence = "GPGGA,235947.000,0000.0000,N,00000.0000,E,0,00,0.0,0.0,M,,,,0000*00";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals("235947", location.getGpsTime());
        assertEquals(0.0, location.getLatitude(), delta);
        assertEquals(0.0, location.getLongitude(), delta);
        assertTrue(location.getIsError());
        assertEquals(0, location.getSatellites());
        assertEquals(0.0, location.getAltitude(), delta);
    }

    public void testParseGpggaSentence2() throws IOException
    {
        String sentence = "GPGGA,092204.999,4250.5589,S,14718.5084,E,1,04,24.4,19.7,M,,,,0000*1F";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals("092204", location.getGpsTime());
        assertEquals(NmeaSource.decimalize("4250.5589", true), location.getLatitude(), delta);
        assertEquals(NmeaSource.decimalize("14718.5084", false), location.getLongitude(), delta);
        assertFalse(location.getIsError());
        assertEquals(4, location.getSatellites());
        assertEquals(19.7, location.getAltitude(), delta);
    }

    private static class GpgsvListener implements LocationListener
    {
        public void notifyLocation(Location l)
        {
        }

        public void notifySignals(int[] s)
        {
            signals = new int[s.length];
            for (int i = 0; i < s.length; i++)
                signals[i] = s[i];
        }

        public void notifyError(WrappedException e)
        {
        }

        public int[] signals;
    }

    public void testParseGpgsvSentence() throws IOException
    {
        String sentence = "GPGSV,1,1,08,01,40,083,46,02,17,308,41,12,07,344,39,14,22,228,45*76";
        GpgsvListener l = new GpgsvListener();
        nmea.addListener(l);
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals(46, l.signals[0]);
        assertEquals(41, l.signals[1]);
        assertEquals(39, l.signals[2]);
        assertEquals(45, l.signals[3]);
    }

    public void testParseBadGpgsvSentence() throws IOException
    {
        String sentence = "GPGSV,1,1,01,21,00,000,*4B";
        GpgsvListener l = new GpgsvListener();
        nmea.addListener(l);
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals(12, l.signals.length);
        assertEquals(0, l.signals[0]);
    }

    public void testParseBadGpgllSentence() throws IOException
    {
        String sentence = "GPGLL,0000.0000,N,00000.0000,E,235947.000,V*2D";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals(0.0, location.getLatitude(), delta);
        assertEquals(0.0, location.getLongitude(), delta);
        assertEquals("235947", location.getGpsTime());
        assertTrue(location.getIsError());
    }

    public void testParseGpgllSentence() throws IOException
    {
        String sentence = "GPGLL,4250.5589,S,14718.5084,E,092204.999,A*2D";
        assertTrue(nmea.parseSentence(sentence, location));
        assertEquals(NmeaSource.decimalize("4250.5589", true), location.getLatitude(), delta);
        assertEquals(NmeaSource.decimalize("14718.5084", false), location.getLongitude(), delta);
        assertEquals("092204", location.getGpsTime());
        assertFalse(location.getIsError());
    }

    public void testReadSentence() throws IOException
    {
        String expect = "GPGLL,4250.5589,S,14718.5084,E,092204.999,A*2D";
        assertEquals(expect, nmea.readSentence(input("qwerty$" + expect + "\r\n")));
    }

    public void testParseBadSentence() throws IOException
    {
        String bad = "GPGSV,3,1,09,20,82,207,37,3175,W,1,04,4.7,106.3,M,54.9,M,0.0,0000*7E";
        assertFalse(nmea.parseSentence(bad, location));
    }

    public void testParseShortGPGSVSentence() throws IOException
    {
        String bad = "GPGSV,3,1,09,20,78,081,36,15,24*41";
        assertTrue(nmea.parseSentence(bad, location));
    }

    public void testGprmcNumberFormatException() throws Exception
    {
        String bad = "$GPRMC,081405,V,0000.0000,N,00000.0000,E,0.000000,0,1205$GPGSA,A,1,,,,,,,,,,,,,50.0,50.0,50.0*05\r\n";
        String expect = "GPGSA,A,1,,,,,,,,,,,,,50.0,50.0,50.0*05";
        assertEquals(expect, nmea.readSentence(input(bad)));
        assertFalse(nmea.parseSentence(expect, location));
    }

    public void testGpggaSentenceTooLong() throws Exception
    {
        // bigger than the 80 char limit and its checksum is wrong
        String bad = "GPGGA,082329,0000.0000,N,00000.0000,E,0,00,,12,07,43,000,,22,41,000,00,32,40,000,00,08,37,000,*77";
        assertFalse(nmea.parseSentence(bad, location));
    }

    public void testGpgsvWithMoreThan20Fields() throws Exception
    {
        String bad = "GPGSV,3,2,12,09,38,000,34,18,21,000,39,08,12000,28,17,44,000,,28,42,000,00*78";
        assertFalse(nmea.parseSentence(bad, location));
    }

    public void testGpgsvWithNoChecksum() throws Exception
    {
        String bad = "GPGSV,3,2,09,18,39,271,30,22,27,306,30,17,27,082,38,12,";
        assertFalse(nmea.parseSentence(bad, location));
    }

    public void testGpgsvOK() throws Exception
    {
        String meh = "GPGSV,3,1,12,18,90,000,,02,70,000,,21,70,000,,15,66,000,*7D";
        assertTrue(nmea.parseSentence(meh, location));
    }

    public void testGprmcNFE() throws Exception
    {
        String bad = "GPRMC,175950,A,5315.8856,N,00614.9935,W,4.716760F";
        assertFalse(nmea.parseSentence(bad, location));
    }

    public void testLineTooShort() throws Exception
    {
        String meh = "E*7D";
        assertFalse(nmea.parseSentence(meh, location));
    }

    public void testEmptyDateField() throws Exception
    {
        String bad = "GPRMC,194305,V,0000.0000,N,00000.0000,E,0.0000,,,,,,50.0,50.0,50.0*05";
        assertTrue(nmea.parseSentence(bad, location));
        assertTrue(location.getIsError());
    }

    private InputStream input(String s)
    {
        return new ByteArrayInputStream(s.getBytes());
    }

    private final double delta = 0.00000001;
    private final Location location = new Location();
    private NmeaSource nmea;
}
