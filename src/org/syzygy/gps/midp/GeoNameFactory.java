package org.syzygy.gps.midp;

import org.kxml2.io.KXmlParser;
import org.syzygy.gps.GeoName;
import org.syzygy.util.midp.HttpUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GeoNameFactory
{
    public GeoName fromDefaultURL(double lat, double lng) throws IOException, XmlPullParserException
    {
        return fromURL("http://ws.geonames.org/findNearbyPlaceName?lat=" + lat + "&lng=" + lng);
    }

    public GeoName fromURL(String url) throws IOException, XmlPullParserException
    {
        StreamConnection conn = null;
        InputStream input = null;
        try {
            conn = (StreamConnection) Connector.open(url);
            if (conn == null)
                return null;
            input = conn.openInputStream();
            return fromXML(input);
        } finally {
            HttpUtil.safeClose(input);
            HttpUtil.safeClose(conn);
        }
    }

    public GeoName fromXML(InputStream input) throws IOException, XmlPullParserException
    {
        KXmlParser parser = new KXmlParser();
        InputStreamReader reader = new InputStreamReader(input);
        parser.setInput(reader);

        GeoName name = new GeoName();
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "geonames");
        if (parser.isEmptyElementTag())
            return null;

        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "geoname");
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            String n = parser.getName().toLowerCase();
            String t = parser.nextText();
            if ("name".equals(n))
                name.setName(t);
            else if ("lat".equals(n))
                name.setLatitude(t);
            else if ("lng".equals(n))
                name.setLongitude(t);
            else if ("geonameid".equals(n))
                name.setGeoNameId(t);
            else if ("countrycode".equals(n))
                name.setCountryCode(t);
            else if ("countryname".equals(n))
                name.setCountryName(t);
            else if ("fcl".equals(n))
                name.setFeatureClass(t);
            else if ("fcode".equals(n))
                name.setFeatureCode(t);
            else if ("distance".equals(n))
                name.setDistance(t);
        }
        parser.require(XmlPullParser.END_TAG, null, "geoname");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, "geonames");

        return name;
    }
}
