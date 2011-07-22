package org.syzygy.gps;

import org.syzygy.gps.mappers.OpenStreetMapper;

public class TestOpenStreetMapper extends CoordinateMapperTests
{
    protected CoordinateMapper createMapper()
    {
        return new OpenStreetMapper();
    }
}
