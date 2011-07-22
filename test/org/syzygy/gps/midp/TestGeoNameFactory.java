package org.syzygy.gps.midp;

import junit.framework.TestCase;
import org.syzygy.gps.GeoName;

import java.io.ByteArrayInputStream;

public class TestGeoNameFactory extends TestCase
{
    public void testParseXML()
            throws Exception
    {
        String xml =
                "<geonames><geoname>" +
                        "<name>Rathgar</name>" +
                        "<lat>53.3145699871472</lat>" +
                        "<lng>-6.27499580383301</lng>" +
                        "<geonameId>3315287</geonameId>" +
                        "<countryCode>IE</countryCode>" +
                        "<countryName>Ireland</countryName>" +
                        "<fcl>P</fcl>" +
                        "<fcode>PPL</fcode>" +
                        "<distance>0.3725</distance>" +
                        "</geoname></geonames>";

        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
        GeoName name = fact.fromXML(input);

        assertNotNull(name);
        assertEquals("Rathgar", name.getName());
        assertEquals("53.3145699871472", name.getLatitude());
        assertEquals("-6.27499580383301", name.getLongitude());
        assertEquals("3315287", name.getGeoNameId());
        assertEquals("IE", name.getCountryCode());
        assertEquals("Ireland", name.getCountryName());
        assertEquals("P", name.getFeatureClass());
        assertEquals("PPL", name.getFeatureCode());
        assertEquals("0.3725", name.getDistance());
    }

    private final GeoNameFactory fact = new GeoNameFactory();
}
