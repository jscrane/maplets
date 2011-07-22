package org.syzygy.gps;

import junit.framework.TestCase;

public class TestMercatorProjection extends TestCase
{
    MercatorProjection projection = new MercatorProjection(256, 21);

    public void testGetLongitude()
    {
        assertEquals(-180, projection.getLongitude(0, 0), 0.01);
        assertEquals(0, projection.getLongitude(0.5, 0), 0.01);
        assertEquals(180, projection.getLongitude(1, 0), 0.01);
    }

    public void testGetLatitude()
    {
        assertEquals(82.74, projection.getLatitude(0, 0), 0.01);
        assertEquals(0, projection.getLatitude(0.5, 0), 0.01);
        assertEquals(-82.74, projection.getLatitude(1, 0), 0.01);
    }

    public void testMapLongitude()
    {
        assertEquals(0, projection.mapLongitude(-180, 0), 0.01);
        assertEquals(0.5, projection.mapLongitude(0, 0), 0.01);
        assertEquals(1.0, projection.mapLongitude(180, 0), 0.01);

        assertEquals(0, projection.mapLongitude(-180, 1), 0.01);
        assertEquals(1.0, projection.mapLongitude(0, 1), 0.01);
        assertEquals(2.0, projection.mapLongitude(180, 1), 0.01);
    }

    public void testMapLatitude()
    {
        assertEquals(0, projection.mapLatitude(90, 0), 0.1);
        assertEquals(0.5, projection.mapLatitude(0, 0), 0.01);
        assertEquals(1.0, projection.mapLatitude(-90, 0), 0.1);

        assertEquals(0, projection.mapLatitude(90, 1), 0.2);
        assertEquals(1.0, projection.mapLatitude(0, 1), 0.01);
        assertEquals(2.0, projection.mapLatitude(-90, 1), 0.2);
    }

    public void testLongitudinalDistance()
    {
        assertEquals(0, projection.getLongitudinalDistance(0, 0), 0.0);
        assertEquals(4.008e7, projection.getLongitudinalDistance(0, 360), 5e4);
        assertEquals(1.113e5, projection.getLongitudinalDistance(0, 1), 500);
        assertEquals(1855, projection.getLongitudinalDistance(0, 1.0 / 60), 5);

        assertEquals(1.095e5, projection.getLongitudinalDistance(10, 1), 500);
        assertEquals(1.04e5, projection.getLongitudinalDistance(20, 1), 500);
        assertEquals(9.6e4, projection.getLongitudinalDistance(30, 1), 500);
        assertEquals(8.5e4, projection.getLongitudinalDistance(40, 1), 500);
        assertEquals(7.1e4, projection.getLongitudinalDistance(50, 1), 500);
        assertEquals(5.6e4, projection.getLongitudinalDistance(60, 1), 500);
    }

    public void testLatitudinalDistance()
    {
        assertEquals(0, projection.getLatitudinalDistance(0), 0.0);
        assertEquals(1.111e5, projection.getLatitudinalDistance(1), 100.0);
        assertEquals(1.111e6, projection.getLatitudinalDistance(10), 1000.0);
        assertEquals(2.222e6, projection.getLatitudinalDistance(20), 1e4);
    }
}

