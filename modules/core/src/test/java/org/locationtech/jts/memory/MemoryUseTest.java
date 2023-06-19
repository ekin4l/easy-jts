package org.locationtech.jts.memory;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.locationtech.jts.geom.impl.XYCoordinateSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryUseTest {
    public static void main(String[] args) throws InterruptedException {
        int objCreateNum  = Integer.parseInt(args[0]);

        Collection<LineString> lines = null;
        if(true){
            lines = createXYPackedLines(objCreateNum, 4);
        }else {
            lines = createLines(objCreateNum, 4);
        }
        double memorySize = toMB(Runtime.getRuntime().totalMemory());
        System.out.println("------totol create line "+lines.size()+"----");
        System.out.println(memorySize);
        Runtime.getRuntime().gc();
        double memorySizeAfterGC = toMB(Runtime.getRuntime().totalMemory());
        System.out.println(memorySizeAfterGC);
        Thread.sleep(10000000);
    }

    private static double toMB(long input) {
        double result = input/1024.0d/1024.0d;

        return ((int)result*10)/10.0d;
    }

    private static Collection<LineString> createLines(int objCreateNum,int linePointSize) {
        List<LineString> reslut = new ArrayList<>(objCreateNum);

        for(int i=0;i<objCreateNum;i++){
            if(i%1000000==0){
                System.out.println("add "+ i);
            }
            double[] xyArray = new double[linePointSize*2];
            for(int j=0;j<linePointSize*2;){
                xyArray[j]=129.344381d;
                xyArray[j+1]=33.938316d;
                j += 2;
            }
            CoordinateSequence sequence = new PackedCoordinateSequence.Double(xyArray,2,0);
            LineString line = new LineString(sequence,GeometryFactory.getDefault());
            reslut.add(line);
        }
        return reslut;
    }

    private static Collection<LineString> createXYPackedLines(int objCreateNum,int linePointSize) {
        List<LineString> reslut = new ArrayList<>(objCreateNum);
        LineString last = null;
        for(int i=0;i<objCreateNum;i++){
            if(i%1000==0){
                System.out.println("add "+ i);
            }

            double[] xyArray = new double[linePointSize*2];
            for(int j=0;j<linePointSize*2;){
               xyArray[j]=129.344381d+0.0001*j;
               xyArray[j+1]=33.938316d+0.0001*j;
               j += 2;
            }
            CoordinateSequence sequence  = new XYCoordinateSequence.DoubleXY(xyArray);
            LineString line = new LineString(sequence,GeometryFactory.getDefault());
            double len = line.getLength();
            line.getEnvelope();
            reslut.add(line);
            if(last!=null) {
//                boolean isI = line.intersects(last);
//                Geometry geo = line.intersection(last);
//                isI = line.covers(last);
//                System.out.println(isI);
//                Geometry bufLine = line.buffer(2.0);
//                System.out.println(bufLine);bufLine
            }
            last = line;
        }
        return reslut;
    }
}
