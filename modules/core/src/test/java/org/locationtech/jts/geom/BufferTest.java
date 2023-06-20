package org.locationtech.jts.geom;

import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

public class BufferTest {
    static String testCoordStr = "115.71463,39.57041;115.71466,39.56952;115.71468,39.57041";
    public static void main(String[] args) {
        LineString line = createLine(testCoordStr);
        System.out.println(line);
        Geometry buf = line.buffer(18.0d);
        String bufCoordStr = buf.toString();
        String bufStr = bufCoordStr.replace("POLYGON ((","")
                        .replace("))","")
                                .replace(" ",",")
                                        .replace(",,",";");
        System.out.println(bufStr);
    }

    private static LineString createLine(String testCoordStr) {
        String[] points = testCoordStr.split(";");
        double[] xyArray = new double[points.length*2];
        for(int i=0;i<points.length;i++){

            String[] xy= points[i].split(",");
            xyArray[i*2] = Double.parseDouble(xy[0]);
            xyArray[i*2+1] = Double.parseDouble(xy[1]);

        }
        CoordinateSequence sequence = new PackedCoordinateSequence.Double(xyArray,2,0);
        LineString line = new LineString(sequence,GeometryFactory.getDefault());
        return line;
    }

}
