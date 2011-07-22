package org.syzygy.gps;

import org.syzygy.gps.mappers.GoogleRoadMapper;

public class TestGoogleStaticMapper extends CoordinateMapperTests
{
    protected CoordinateMapper createMapper()
    {
        return new GoogleRoadMapper();
    }
}
