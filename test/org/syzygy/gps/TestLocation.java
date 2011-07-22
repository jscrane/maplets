package org.syzygy.gps;

import junit.framework.TestCase;

import java.util.Calendar;

public class TestLocation extends TestCase
{
    private final double delta = 0.000001;

    public void testParseWithError()
    {
        Location l = Location.parse("160940 53.344345000000004 -6.249651666666667 0.0 0.0 100.0 290565 5 E\n");
        assertEquals("160940", l.getGpsTime());
        assertEquals(53.344345, l.getLatitude(), delta);
        assertEquals(-6.249652, l.getLongitude(), delta);
        assertEquals(0.0, l.getSpeed(), delta);
        assertEquals(0.0, l.getAltitude(), delta);
        assertTrue(l.getIsError());
        assertEquals(100.0, l.getCourse(), delta);
        assertEquals("290565", l.getUtcDate());
        assertEquals(5, l.getSatellites());
    }

    public void testParseWithoutError()
    {
        Location l = Location.parse("161234 53.339938333333336 -6.247275 10.744023 55.9 150.0 010101 4\n");
        assertEquals("161234", l.getGpsTime());
        assertEquals(53.339938, l.getLatitude(), delta);
        assertEquals(-6.247275, l.getLongitude(), delta);
        assertEquals(10.744023, l.getSpeed(), delta);
        assertEquals(55.9, l.getAltitude(), delta);
        assertEquals(150.0, l.getCourse(), delta);
        assertEquals("010101", l.getUtcDate());
        assertEquals(4, l.getSatellites());
        assertFalse(l.getIsError());
    }

    public void testSetUtcDate()
    {
        Calendar c = Calendar.getInstance();
        Location l = new Location();
        l.setUtcDate("290565");
        c.setTime(l.getDate());
        assertEquals(1965, c.get(Calendar.YEAR));
        l.setUtcDate("010100");
        c.setTime(l.getDate());
        assertEquals(2000, c.get(Calendar.YEAR));
        assertEquals(0, c.get(Calendar.MONTH));
    }
}
				   