package org.locationtech.jts.algorithm;

import org.locationtech.jts.algorithm.distance.LocalLonLatDistance;
import org.locationtech.jts.geom.Coordinate;

import static org.locationtech.jts.algorithm.distance.LocalLonLatDistance.scaleLonRAD;

public class Azimuth {

    // Error codes
    public static final double NO_AZIMUTH = -1.0;

    /**
     * Calculate the azimuth of a vector.
     * @param lon1 longitude of the begin point.
     * @param lat1 latitude of the begin point.
     * @param lon2 longitude of the end point.
     * @param lat2 latitude of the end point.
     * @return Azimuth of the vector. Return CoordSysUtil.NO_AZIMUTH
     *         if two locations are the same.
     */
    public static double azimuth(double lon1, double lat1, double lon2, double lat2)
    {
        if (lon1 == lon2 && lat1 == lat2)
            return NO_AZIMUTH;

        double lonRAD1 = Math.toRadians(lon1);
        double latRAD1 = Math.toRadians(lat1);
        double lonRAD2 = Math.toRadians(lon2);
        double latRAD2 = Math.toRadians(lat2);

        double azimuthRAD = azimuthRAD(lonRAD1, latRAD1, lonRAD2, latRAD2);

        double azimuth = Math.toDegrees(azimuthRAD);
        return azimuth;
    }

    /**
     * Calculate the azimuth of tow coordinate.
     * @param a begin point.
     * @param b end point
     * @return Azimuth of the vector. Return CoordSysUtil.NO_AZIMUTH
     *         if two locations are the same.
     */
    public static double azimuth(Coordinate a,Coordinate b){
        return azimuth(a.x,a.y,b.x,b.y);
    }

    /**
     * Calculate the azimuth of a vector.
     * @param lon1 longitude of the begin point.
     * @param lat1 latitude of the begin point.
     * @param lon2 longitude of the end point.
     * @param lat2 latitude of the end point.
     * @return Azimuth of the vector. Return CoordSysUtil.NO_AZIMUTH
     *         if two locations are the same.
     */
    public static double azimuthRAD(double lon1, double lat1, double lon2, double lat2)
    {
        if (lat1 == lat2)
        {
            if (lon1 == lon2)
                return NO_AZIMUTH;
            else if (lon1 > lon2)
                return LocalLonLatDistance.PI_TIMES_3_OVER_2;
            else
                return LocalLonLatDistance.PI_OVER_2;
        }

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;
        deltaLon *= scaleLonRAD(lat1, lat2);
        double angle = Math.atan(deltaLon / deltaLat);
        double azimuth = 0.0;
        if (deltaLat > 0)
        {
            if (deltaLon > 0)
                azimuth = angle; // the 1st quadrant
            else
                azimuth = LocalLonLatDistance.PI_TIMES_2 + angle; // the 2nd quadrant
        }
        else {
            azimuth = Math.PI + angle; // the 3rd and the 4th quadrant
        }
        return azimuth;
    }

}
