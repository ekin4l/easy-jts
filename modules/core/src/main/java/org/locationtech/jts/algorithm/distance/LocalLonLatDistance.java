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
package org.locationtech.jts.algorithm.distance;

public class LocalLonLatDistance {
    public static final double R = 6371008.7714;

    public static final double PI_TIMES_2 = Math.PI * 2.0;

    public static final double PI_TIMES_3_OVER_2 = Math.PI * 1.5;

    public static final double PI_OVER_2 = Math.PI / 2.0;

    public static final double PI_OVER_4 = Math.PI / 4.0;

    public static double distanceToMeters(double distRAD)
    {
        return distRAD * R;
    }


    private static double scaleLonRAD(double lat)
    {
        return Math.cos(lat);
    }

    private static double scaleLonRAD(double lat1, double lat2)
    {
        return Math.cos((lat1 + lat2) * 0.5);
    }

    public static double distance(double lon1, double lat1, double lon2, double lat2)
    {
        double lonRAD1 = Math.toRadians(lon1);
        double latRAD1 = Math.toRadians(lat1);
        double lonRAD2 = Math.toRadians(lon2);
        double latRAD2 = Math.toRadians(lat2);

        double distanceRAD = distanceRAD(lonRAD1, latRAD1, lonRAD2, latRAD2);

        double distance = distanceToMeters(distanceRAD);
        return distance;
    }

    public static double distanceRAD(double lon1, double lat1, double lon2, double lat2)
    {
        double deltaLon = lon2 - lon1;
        double deltaLat = lat2 - lat1;
        double scaleLon = scaleLonRAD(lat1, lat2);
        deltaLon *= scaleLon;
        double squareDistance = deltaLon * deltaLon + deltaLat * deltaLat;
        double distance = Math.sqrt(squareDistance);
        return distance;
    }


}
