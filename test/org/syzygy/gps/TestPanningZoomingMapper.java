package org.syzygy.gps;

import junit.framework.TestCase;
import org.syzygy.gps.mappers.OpenStreetMapper;

public class TestPanningZoomingMapper extends TestCase
{
    private CoordinateMapper coords;
    private PanningZoomingMapper panning;
    private Location location;
    private CoordinateMapper.Tile start;

    protected void setUp()
    {
        coords = new OpenStreetMapper();
        panning = new PanningZoomingMapper(coords, 12);
        location = new Location();
        location.setLatitude(53.267);
        location.setLongitude(-6.25);
        start = panning.fromLocation(location);
    }

    public void testNoPanningOrZooming()
    {
        assertEquals(start, coords.getTile(location.getLatitude(), location.getLongitude(), start.getZoom()));
    }

    public void testPanByNothing()
    {
        assertEquals(start, panning.pan(0, 0));
        assertFalse(panning.isPanning());
    }

    public void testImplicitPanningAndUnpanning()
    {
        assertFalse(start.equals(panning.pan(1, 1)));
        assertEquals(start, panning.pan(-1, -1));
        assertFalse(panning.isPanning());
    }

    public void testPanningAndUnpanning()
    {
        assertFalse(start.equals(panning.pan(1, 1)));
        assertEquals(start, panning.panOff());
        assertFalse(panning.isPanning());
    }

    public void testPanningZoomingAndUnpanning()
    {
        assertFalse(start.equals(panning.pan(-1, 1)));
        assertTrue(panning.isPanning());
        assertFalse(start.equals(panning.zoomOut()));
        assertFalse(start.equals(panning.zoomIn()));
        assertEquals(start, panning.pan(1, -1));
        assertFalse(panning.isPanning());
    }
}
