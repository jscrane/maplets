package org.syzygy.gps;

public final class MercatorProjection
{
    private final double wa = Math.PI / 180.0;
    private final int tileSize;
    private final double[] pixelsPerLonDegree;
    private final double[] pixelsPerLonRadian;
    private final double[] bitmapOrigo;

    public MercatorProjection(int tileSize, int zoomLevels)
    {
        this.tileSize = tileSize;
        this.pixelsPerLonDegree = new double[zoomLevels];
        this.pixelsPerLonRadian = new double[zoomLevels];
        this.bitmapOrigo = new double[zoomLevels];

        int c = tileSize;
        double bc = 2 * Math.PI;

        for (int z = 0; z < zoomLevels; z++) {
            pixelsPerLonDegree[z] = c / (double) 360;
            pixelsPerLonRadian[z] = c / bc;
            bitmapOrigo[z] = c / 2;
            c *= 2;
        }
    }

    public double getLongitude(double x, int z)
    {
        return (x * tileSize - bitmapOrigo[z]) / pixelsPerLonDegree[z];
    }

    public double getLatitude(double y, int z)
    {
        double a = 2 * (bitmapOrigo[z] - y * tileSize) / pixelsPerLonRadian[z];
        double ab = 1;
        for (int i = 29; i > 0; i--)
            ab = 1 + a / (double) i * ab;

        double b = ab;
        //double b = Math.pow(Math.E, a);
        double e = (b - 1) / (b + 1);
        //double exact = Math.asin(e);
        double e2 = e * e;
        double term = e, approx = e;
        // term_n / term_n-1 = (2n-1)^2.e^2 / 2n(2n+1); t_0 = e;
        // asin(e) = term_0 + term_1 + term_2 + ...
        for (int i = 1; i < 29; i++) {
            double n2 = i + i;
            term = term * e2 * (n2 - 1) * (n2 - 1) / n2 / (n2 + 1);
            approx += term;
        }
        //        return exact / wa;
        return approx / wa;
    }

    public double mapLongitude(double lon, int z)
    {
        return (bitmapOrigo[z] + lon * pixelsPerLonDegree[z]) / tileSize;
    }

    public double mapLatitude(double lat, int z)
    {
        // computes an approximation to the inverse gudermannian function
        // see (6), http://mathworld.wolfram.com/GudermannianFunction.html
        double as = Math.sin(lat * wa);
        double as2 = as * as;
        // inv_gd = ln((1 + s) / 1 - s) / 2;
        //        =~ s + s^3/3 + s^5/5 + ...
        double approx = 1;
        for (int d = 49; d > 1;) {
            int n = d - 2;
            approx = 1 + n * as2 / (double) d * approx;
            d = n;
        }
        approx *= as;
        return (bitmapOrigo[z] - approx * pixelsPerLonRadian[z]) / tileSize;
    }

    public double getLongitudinalDistance(double lat, double dlon)
    {
        return MEAN_RADIUS * Math.cos(Math.toRadians(lat)) * Math.toRadians(dlon);
    }

    public double getLatitudinalDistance(double dlat)
    {
        return MEAN_RADIUS * Math.toRadians(dlat);
    }

    private static final double MEAN_RADIUS = 6.371e6;
}