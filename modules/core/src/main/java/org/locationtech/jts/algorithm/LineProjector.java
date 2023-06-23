/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.algorithm;

public class LineProjector {

    public static LineProjectResult project(double lon, double lat, double lon1, double lat1, double lon2, double lat2)
    {
        double lonRAD = Math.toRadians(lon);
        double latRAD = Math.toRadians(lat);
        double lon1RAD = Math.toRadians(lon1);
        double lat1RAD = Math.toRadians(lat1);
        double lon2RAD = Math.toRadians(lon2);
        double lat2RAD = Math.toRadians(lat2);

        LineProjectResult result = projectRAD(lonRAD, latRAD, lon1RAD, lat1RAD, lon2RAD, lat2RAD);

        result.lon = Math.toDegrees(result.lon);
        result.lat = Math.toDegrees(result.lat);
        return result;
    }

    public static LineProjectResult projectRAD(double lon, double lat, double lon1, double lat1, double lon2, double lat2)
    {
        LineProjectResult result = new LineProjectResult();

        // A special case.
        if (lon1 == lon2)
        {
            result.lon = lon1;
            if (lat1 > lat2)
            {
                if (lat > lat1)
                {
                    result.pos = LinePosition.OUTSIDE_START;
                    result.lat = lat1;
                }
                else if (lat < lat2)
                {
                    result.pos = LinePosition.OUTSIDE_END;
                    result.lat = lat2;
                }
                else {
                    result.pos = LinePosition.INSIDE;
                    result.lat = lat;
                }
            }
            else {
                if (lat < lat1)
                {
                    result.pos = LinePosition.OUTSIDE_START;
                    result.lat = lat1;
                }
                else if (lat > lat2)
                {
                    result.pos = LinePosition.OUTSIDE_END;
                    result.lat = lat2;
                }
                else {
                    result.pos = LinePosition.INSIDE;
                    result.lat = lat;
                }
            }

            return result;
        }


        // The algorithm is a little complicated.

        double sinLat = Math.sin(lat);
        double sinLat1 = Math.sin(lat1);
        double sinLat2 = Math.sin(lat2);
        double cosLat = Math.sqrt(1 - sinLat * sinLat);
        double cosLat1 = Math.sqrt(1 - sinLat1 * sinLat1);
        double cosLat2 = Math.sqrt(1 - sinLat2 * sinLat2);

        double deltaLonPP1 = lon - lon1;
        double deltaLonP2P = lon2 - lon;
        double deltaLonP2P1 = lon2 - lon1;
        double cosLonPP1 = Math.cos(deltaLonPP1);
        double cosLonP2P = Math.cos(deltaLonP2P);
        double cosLonP2P1 = Math.cos(deltaLonP2P1);

        double cosDistPP1 = sinLat * sinLat1 + cosLat * cosLat1 * cosLonPP1;
        double cosDistPP2 = sinLat * sinLat2 + cosLat * cosLat2 * cosLonP2P;
        double cosDistP1P2 = sinLat1 * sinLat2 + cosLat1 * cosLat2 * cosLonP2P1;
        double sinDistP1P2 = Math.sqrt(1 - cosDistP1P2 * cosDistP1P2);
        double tanDistFP1 = (cosDistPP2 / cosDistPP1 - cosDistP1P2) / sinDistP1P2;
        double distFP1 = Math.atan(tanDistFP1);


        double sinLonP2P1 = Math.sqrt(1 - cosLonP2P1 * cosLonP2P1);
        double tanLonFP1 = sinLonP2P1 / (cosLat1 / cosLat2 * (sinDistP1P2 / tanDistFP1 - cosDistP1P2) + cosLonP2P1);
        double deltaLonFP1 = Math.atan(tanLonFP1);

        double sinLonFP1 = Math.sin(deltaLonFP1);
        double sinDistFP1 = Math.sin(distFP1);
        double cosLatF = sinLonP2P1 / sinLonFP1 * sinDistFP1 / sinDistP1P2 * cosLat2;
        double latF = Math.acos(cosLatF);


        if (deltaLonFP1 < 0.0) {
            result.pos = LinePosition.OUTSIDE_START;
            result.lon = lon1;
            result.lat = lat1;
        }
        else if (deltaLonFP1 > deltaLonP2P1) {
            result.pos = LinePosition.OUTSIDE_END;
            result.lon = lon2;
            result.lat = lat2;
        }
        else {
            result.pos = LinePosition.INSIDE;
            result.lon = lon1 + deltaLonFP1;
            result.lat = latF;
        }
        return result;

        //Not finished, to be continue...
    }

}
