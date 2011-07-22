package org.syzygy.gps;

/**
 * A helper class to convert (latitude,longitude)
 * to screen coordinates (and vice-versa).
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public class ScreenMapper
{
    public ScreenMapper()
    {
        reset();
    }

    public boolean contains(int x, int y)
    {
        return x >= 0 && x < w && y >= 0 && y < h;
    }

    /*
     * Returns the current screen coordinate for the given latitude
     */
    public int mapY(double lat)
    {
        return (int) (h - (lat - latMin) * sy + 0.5);
    }

    /*
     * Returns the current screen ordinate for the given latitude
     */
    public int mapX(double lon)
    {
        return (int) ((lon - lonMin) * sx + 0.5);
    }

    /*
     * Returns the latitude for the given screen coordinate
     */
    public double mapLat(int y)
    {
        return latMin + (h - y) / sy;
    }

    /*
     * Returns the longitude for the given screen ordinate
     */
    public double mapLon(int x)
    {
        return lonMin + x / sx;
    }

    /*
     * Scales the current map to the given screen width and height
     */
    public void scaleTo(int w, int h)
    {
        this.h = h;
        this.w = w;
        double dlat = latMax - latMin, dlon = lonMax - lonMin;
        this.sx = w / dlon;
        this.sy = h / dlat;
    }

    /*
     * Sets the current map's bounds if the given point lies outside.
     */
    public void setBounds(double lon, double lat)
    {
        if (lat < latMin)
            latMin = lat;
        if (lat > latMax)
            latMax = lat;
        if (lon < lonMin)
            lonMin = lon;
        if (lon > lonMax)
            lonMax = lon;
    }

    /*
     * Sets the (latitude, longitude) bounds based on the values
     * for the new screen origin (x,y) and dimensions (wn,hn)
     */
    public void setViewport(int x, int y, int wn, int hn)
    {
        double dlon = lonMax - lonMin, dlat = latMax - latMin;
        lonMin = lonMin + x * dlon / w;
        lonMax = lonMax - (w - wn - x) * dlon / w;
        latMin = latMin + (h - hn - y) * dlat / h;
        latMax = latMax - y * dlat / h;
    }

    /*
     * Resets the (latitude, longitude) bounds to impossible values.
     */
    public void reset()
    {
        latMin = lonMin = 180;
        latMax = lonMax = -180;
    }

    private double latMin, latMax, lonMin, lonMax;
    private int w, h;
    private double sx, sy;
}
