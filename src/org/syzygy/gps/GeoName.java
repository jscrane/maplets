package org.syzygy.gps;

public final class GeoName
{
    public GeoName()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getGeoNameId()
    {
        return geoNameId;
    }

    public void setGeoNameId(String geoNameId)
    {
        this.geoNameId = geoNameId;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getCountryName()
    {
        return countryName;
    }

    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }

    public String getFeatureClass()
    {
        return featureClass;
    }

    public void setFeatureClass(String featureClass)
    {
        this.featureClass = featureClass;
    }

    public String getFeatureCode()
    {
        return featureCode;
    }

    public void setFeatureCode(String featureCode)
    {
        this.featureCode = featureCode;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    private String name, latitude, longitude, geoNameId, countryCode;
    private String countryName, featureClass, featureCode, distance;
}
