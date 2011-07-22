package org.syzygy.gps;

import junit.framework.TestCase;

public abstract class CoordinateMapperTests extends TestCase
{
    CoordinateMapper mapper;

    protected abstract CoordinateMapper createMapper();

    protected void setUp()
    {
        mapper = createMapper();
    }

    private CoordinateMapper.Tile origin()
    {
        return mapper.getTile(0, 0, 0);
    }

    public void testInitial()
    {
        CoordinateMapper.Tile tile = origin();
        assertEquals(-180.0, mapper.getLongitude(tile.getTileX(), 0), 0.00001);
        assertEquals(82.74, mapper.getLatitude(tile.getTileY(), 0), 0.001);
        assertEquals(0, tile.getZoom());
    }

    public void testEquals()
    {
        CoordinateMapper.Tile tile = origin();
        CoordinateMapper.Tile other = mapper.getTile(0.0, 0.0, 0);
        assertNotSame(other, tile);
        assertEquals(other, tile);
    }

    public void testCantZoomOutFromInitial()
    {
        CoordinateMapper.Tile tile = origin();
        CoordinateMapper.Tile other = tile.zoomOut();
        assertEquals(other, tile);
    }

    public void testCanZoomInFromInitial()
    {
        CoordinateMapper.Tile first = origin();
        CoordinateMapper.Tile second = first.zoomIn(1, 0);
        CoordinateMapper.Tile third = first.zoomIn(0, 1);
        assertFalse(first.equals(second));
        assertFalse(first.equals(third));
        assertFalse(second.equals(third));
        assertEquals(first, second.zoomOut());
        assertEquals(first, third.zoomOut());
    }

    public void testTileBoundingBoxes()
    {
        // FIXME: latitude
        CoordinateMapper.Tile dublin = mapper.getTile(53.33306, -6.24889, 0);
        assertEquals(-180.0, mapper.getLongitude(dublin.getTileX(), dublin.getZoom()), 0.0001);
        assertEquals(180.0, mapper.getLongitude(dublin.getTileX() + 1, dublin.getZoom()), 0.0001);

        CoordinateMapper.Tile dublin1 = dublin.zoomIn(0, 0);
        assertEquals(-180.0, mapper.getLongitude(dublin1.getTileX(), dublin1.getZoom()), 0.0001);
        assertEquals(0.0, mapper.getLongitude(dublin1.getTileX() + 1, dublin1.getZoom()), 0.0001);

        CoordinateMapper.Tile dublin2 = dublin1.zoomIn(1, 1);
        assertEquals(-90.0, mapper.getLongitude(dublin2.getTileX(), dublin2.getZoom()), 0.0001);
        assertEquals(0.0, mapper.getLongitude(dublin2.getTileX() + 1, dublin2.getZoom()), 0.0001);

        CoordinateMapper.Tile dublin3 = dublin2.zoomIn(1, 1);
        assertEquals(-45.0, mapper.getLongitude(dublin3.getTileX(), dublin3.getZoom()), 0.0001);
        assertEquals(0.0, mapper.getLongitude(dublin3.getTileX() + 1, dublin3.getZoom()), 0.0001);
    }

    public void testGetTileAtZoom()
    {
        double lat = Util.decimalize(5128.27), lon = -Util.decimalize(4.10);
        int zoom = 12;
        CoordinateMapper.Tile peckham = mapper.getTile(lat, lon, zoom);
        assertEquals(2047, peckham.getTileX());
        assertEquals(1362, peckham.getTileY());
        assertEquals(-0.08789, mapper.getLongitude(peckham.getTileX(), zoom), 0.001);
        assertEquals(51.5087, mapper.getLatitude(peckham.getTileY(), zoom), 0.001);
    }

    public void testGetScreenPosition()
    {
        double longitude = -6.250817, latitude = 53.25561;
        CoordinateMapper.Tile tile = mapper.getTile(latitude, longitude, 11);
        assertEquals(988, tile.getTileX());
        assertEquals(664, tile.getTileY());
        assertEquals(11, tile.getZoom());
        assertEquals(112, mapper.getScreenX(tile, longitude, 256));
        assertEquals(183, mapper.getScreenY(tile, latitude, 256));
    }
}
