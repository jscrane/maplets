package org.syzygy.gps;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A Location Bean.
 * <p/>
 * This class was written by Stephen Crane (jscrane@gmail.com)
 * and is released under the terms of the GNU GPL
 * (http://www.gnu.org/licenses/gpl.html).
 */
public final class Location
{
    public Location()
    {
    }

    public Location(Location l)
    {
        setLatitude(l.getLatitude());
        setLongitude(l.getLongitude());
        setSpeed(l.getSpeed());
        setDate(l.getDate());
        setAltitude(l.getAltitude());
        setCourse(l.getCourse());
        setIsError(l.getIsError());
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setSpeed(double knots)
    {
        this.speed = knots;
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getSpeedMph()
    {
        return speed * 1.1507771555;
    }

    public double getSpeedKmh()
    {
        return speed * 1.85198479488;
    }

    public void setUtcDate(String s)
    {
        if (s.length() == 6) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            int y = c.get(Calendar.YEAR);
            c.setTime(getDate());
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(0, 2)));
            c.set(Calendar.MONTH, Integer.parseInt(s.substring(2, 4)) - 1);
            int ty = Integer.parseInt(s.substring(4, 6)) + 2000;
            if (ty > y)
                ty -= 100;
            c.set(Calendar.YEAR, ty);
            setDate(c.getTime());
        }
    }

    public String getUtcDate()
    {
        Calendar c = Calendar.getInstance();
        c.setTime(getDate());
        return toStringWithLeadingZero(c.get(Calendar.DAY_OF_MONTH))
                + toStringWithLeadingZero(c.get(Calendar.MONTH) + 1)
                + toStringWithLeadingZero(c.get(Calendar.YEAR) % 100);
    }

    public void setGpsTime(String s)
    {
        if (s.length() >= 6) {
            Calendar c = Calendar.getInstance();
            c.setTime(getDate());
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(0, 2)));
            c.set(Calendar.MINUTE, Integer.parseInt(s.substring(2, 4)));
            c.set(Calendar.SECOND, Integer.parseInt(s.substring(4, 6)));
            setDate(c.getTime());
        }
    }

    private String toStringWithLeadingZero(int i)
    {
        String s = "";
        if (i < 10)
            s += "0";
        return s += new Integer(i).toString();
    }

    public String getGpsTime()
    {
        Calendar c = Calendar.getInstance();
        c.setTime(getDate());
        return toStringWithLeadingZero(c.get(Calendar.HOUR_OF_DAY))
                + toStringWithLeadingZero(c.get(Calendar.MINUTE))
                + toStringWithLeadingZero(c.get(Calendar.SECOND));
    }

    public void setDate(Date time)
    {
        this.time = time;
    }

    public Date getDate()
    {
        return time;
    }

    public String getIsoTime()
    {
        Calendar c = Calendar.getInstance();
        c.setTime(getDate());
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        StringBuffer b = new StringBuffer();
        b.append(c.get(Calendar.YEAR)).append('-');
        b.append(toStringWithLeadingZero(c.get(Calendar.MONTH) + 1)).append('-');
        b.append(toStringWithLeadingZero(c.get(Calendar.DAY_OF_MONTH))).append('T');
        b.append(toStringWithLeadingZero(c.get(Calendar.HOUR_OF_DAY))).append(':');
        b.append(toStringWithLeadingZero(c.get(Calendar.MINUTE))).append(':');
        b.append(toStringWithLeadingZero(c.get(Calendar.SECOND))).append('Z');
        return b.toString();
    }

    public void setAltitude(double altitude)
    {
        this.altitude = altitude;
    }

    public double getAltitude()
    {
        return altitude;
    }

    public void setCourse(double course)
    {
        this.course = course;
    }

    public double getCourse()
    {
        return course;
    }

    public void setIsError(boolean isError)
    {
        this.isError = isError;
    }

    public boolean getIsError()
    {
        return isError;
    }

    public void setSatellites(int satellites)
    {
        this.satellites = satellites;
    }

    public int getSatellites()
    {
        return satellites;
    }

    public boolean samePosition(Location other)
    {
        return other != null && latitude == other.getLatitude() && longitude == other.getLongitude() && altitude == other.getAltitude();
    }

    public String toString()
    {
        String s = getGpsTime() + " " + getLatitude() + " " + getLongitude() + " " +
                getSpeed() + " " + getAltitude() + " " + getCourse() + " " + getUtcDate() + " " + getSatellites();
        if (isError)
            s += " E";
        return s;
    }

    public static Location parse(String s)
    {
        Location l = new Location();
        s = s.trim();
        for (int i = 0; i < 8; i++) {
            int idx = s.indexOf(' ');
            String t = idx == -1 ? s : s.substring(0, idx);
            s = s.substring(idx + 1);
            if (i == 0)
                l.setGpsTime(t);
            else if (i == 1)
                l.setLatitude(Double.parseDouble(t));
            else if (i == 2)
                l.setLongitude(Double.parseDouble(t));
            else if (i == 3)
                l.setSpeed(Double.parseDouble(t));
            else if (i == 4)
                l.setAltitude(Double.parseDouble(t));
            else if (i == 5)
                l.setCourse(Double.parseDouble(t));
            else if (i == 6)
                l.setUtcDate(t);
            else if (i == 7)
                l.setSatellites(Integer.parseInt(t));
            if (idx == -1)
                break;
        }
        l.setIsError("E".equals(s));
        return l;
    }

    private double latitude, longitude, speed, altitude, course;
    private Date time = new Date();
    private boolean isError = false;
    private int satellites;
}
