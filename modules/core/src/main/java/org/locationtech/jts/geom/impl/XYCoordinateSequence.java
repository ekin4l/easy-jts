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
package org.locationtech.jts.geom.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Arrays;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequences;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.CoordinateXYM;
import org.locationtech.jts.geom.CoordinateXYZM;
import org.locationtech.jts.geom.Envelope;

/**
 * A {@link CoordinateSequence} implementation based on a packed arrays only store XY field.
 * In this implementation, {@link Coordinate}s returned by #toArray and #get are copies
 * of the internal values.
 * To change the actual values, use the provided setters.
 * <p>
 * For efficiency, created Coordinate arrays
 * are cached using a soft reference.
 * The cache is cleared each time the coordinate sequence contents are
 * modified through a setter method.
 *
 * @version 1.20
 */
public abstract class XYCoordinateSequence implements CoordinateSequence, Serializable {
    private static final long serialVersionUID = -3155818192275819L;
    /**
     * The dimensions of the coordinates held in the packed array
     */
    protected static final int dimension = 2;

    /**
     * The number of measures of the coordinates held in the packed array.
     */
    protected static final int measures = 0;



    /**
     * A soft reference to the Coordinate[] representation of this sequence.
     * Makes repeated coordinate array accesses more efficient.
     */
    protected transient SoftReference<Coordinate[]> coordRef;

    /**
     * @see CoordinateSequence#getDimension()
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * @see CoordinateSequence#getMeasures()
     */
    @Override
    public int getMeasures() {
        return this.measures;
    }

    /**
     * @see CoordinateSequence#getCoordinate(int)
     */
    public Coordinate getCoordinate(int i) {
        Coordinate[] coords = getCachedCoords();
        if(coords != null)
            return coords[i];
        else
            return getCoordinateInternal(i);
    }
    /**
     * @see CoordinateSequence#getCoordinate(int)
     */
    public Coordinate getCoordinateCopy(int i) {
        return getCoordinateInternal(i);
    }

    /**
     * @see CoordinateSequence#getCoordinate(int)
     */
    public void getCoordinate(int i, Coordinate coord) {
        coord.x = getOrdinate(i, 0);
        coord.y = getOrdinate(i, 1);
        if (hasZ()) {
            coord.setZ(getZ(i));
        }
        if (hasM()) {
            coord.setM(getM(i));
        }
    }

    /**
     * @see CoordinateSequence#toCoordinateArray()
     */
    public Coordinate[] toCoordinateArray() {
        Coordinate[] coords = getCachedCoords();
// testing - never cache
        if (coords != null)
            return coords;

        coords = new Coordinate[size()];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = getCoordinateInternal(i);
        }
        coordRef = new SoftReference<Coordinate[]>(coords);

        return coords;
    }

    private Coordinate[] getCachedCoords() {
        if (coordRef != null) {
            Coordinate[] coords = (Coordinate[]) coordRef.get();
            if (coords != null) {
                return coords;
            } else {
                // System.out.print("-");
                coordRef = null;
                return null;
            }
        } else {
            // System.out.print("-");
            return null;
        }

    }

    /**
     * @see CoordinateSequence#getX(int)
     */
    public double getX(int index) {
        return getOrdinate(index, 0);
    }

    /**
     * @see CoordinateSequence#getY(int)
     */
    public double getY(int index) {
        return getOrdinate(index, 1);
    }

    /**
     * @see CoordinateSequence#getOrdinate(int, int)
     */
    public abstract double getOrdinate(int index, int ordinateIndex);

    /**
     * Sets the first ordinate of a coordinate in this sequence.
     *
     * @param index  the coordinate index
     * @param value  the new ordinate value
     */
    public void setX(int index, double value) {
        coordRef = null;
        setOrdinate(index, 0, value);
    }

    /**
     * Sets the second ordinate of a coordinate in this sequence.
     *
     * @param index  the coordinate index
     * @param value  the new ordinate value
     */
    public void setY(int index, double value) {
        coordRef = null;
        setOrdinate(index, 1, value);
    }

    public String toString()
    {
        return CoordinateSequences.toString(this);
    }

    protected Object readResolve() throws ObjectStreamException {
        coordRef = null;
        return this;
    }

    /**
     * Returns a Coordinate representation of the specified coordinate, by always
     * building a new Coordinate object
     *
     * @param index  the coordinate index
     * @return  the {@link Coordinate} at the given index
     */
    protected abstract Coordinate getCoordinateInternal(int index);

    /**
     * @see java.lang.Object#clone()
     * @see CoordinateSequence#clone()
     * @deprecated
     */
    public abstract Object clone();

    public abstract XYCoordinateSequence copy();

    /**
     * Sets the ordinate of a coordinate in this sequence.
     * <br>
     * Warning: for performance reasons the ordinate index is not checked
     * - if it is over dimensions you may not get an exception but a meaningless value.
     *
     * @param index
     *          the coordinate index
     * @param ordinate
     *          the ordinate index in the coordinate, 0 based, smaller than the
     *          number of dimensions
     * @param value
     *          the new ordinate value
     */
    public abstract void setOrdinate(int index, int ordinate, double value);

    /**
     * Packed coordinate sequence implementation based on doubles
     */
    public static class DoubleXY extends XYCoordinateSequence {
        private static final long serialVersionUID = 5346950686367998723L;
        /**
         * The packed coordinate array
         */
        double[] coords;

        /**
         * Builds a new packed coordinate sequence
         *
         * @param coords  an array of <code>double</code> values that contains the ordinate values of the sequence
         */
        public DoubleXY(double[] coords) {

            if (coords.length % dimension != 0) {
                throw new IllegalArgumentException("Packed array does not contain "
                        + "an integral number of coordinates");
            }
            this.coords = coords;
        }

        /**
         * Builds a new packed coordinate sequence out of a float coordinate array
         *
         * @param coords  an array of <code>float</code> values that contains the ordinate values of the sequence
         */
        public DoubleXY(float[] coords) {

            this.coords = new double[coords.length];
            for (int i = 0; i < coords.length; i++) {
                this.coords[i] = coords[i];
            }
        }

        /**
         * Builds a new packed coordinate sequence out of a coordinate array
         *
         * @param coordinates an array of {@link Coordinate}s
         * @param dimension the total number of ordinates that make up a {@link Coordinate} in this sequence.
         */
        public DoubleXY(Coordinate[] coordinates, int dimension) {
            this( coordinates, dimension,  Math.max(0,dimension-3));
        }
        /**
         * Builds a new packed coordinate sequence out of a coordinate array
         *
         * @param coordinates an array of {@link Coordinate}s
         * @param dimension the total number of ordinates that make up a {@link Coordinate} in this sequence.
         * @param measures the number of measure-ordinates each {@link Coordinate} in this sequence has.
         */
        public DoubleXY(Coordinate[] coordinates, int dimension, int measures) {

            if (coordinates == null)
                coordinates = new Coordinate[0];

            coords = new double[coordinates.length * this.dimension];
            for (int i = 0; i < coordinates.length; i++) {
                int offset = i * dimension;
                coords[offset] = coordinates[i].x;
                coords[offset + 1] = coordinates[i].y;
                if (dimension >= 3)
                    coords[offset + 2] = coordinates[i].getOrdinate(2); // Z or M
                if (dimension >= 4)
                    coords[offset + 3] = coordinates[i].getOrdinate(3); // M
            }
        }
        /**
         * Builds a new packed coordinate sequence out of a coordinate array
         *
         * @param coordinates an array of {@link Coordinate}s
         */
        public DoubleXY(Coordinate[] coordinates) {
            this(coordinates, 3, 0);
        }

        /**
         * Builds a new empty packed coordinate sequence of a given size and dimension
         *
         * @param size the number of coordinates in this sequence
         */
        public DoubleXY(int size) {

            coords = new double[size * this.dimension];
        }

        /**
         * @see PackedCoordinateSequence#getCoordinate(int)
         */
        public Coordinate getCoordinateInternal(int i) {
            double x = coords[i * dimension];
            double y = coords[i * dimension + 1];
            if( dimension == 2 && measures == 0 ) {
                return new CoordinateXY(x,y);
            }
            else if (dimension == 3 && measures == 0) {
                double z = coords[i * dimension + 2];
                return new Coordinate(x,y,z);
            }
            else if (dimension == 3 && measures == 1) {
                double m = coords[i * dimension + 2];
                return new CoordinateXYM(x,y,m);
            }
            else if (dimension == 4) {
                double z = coords[i * dimension + 2];
                double m = coords[i * dimension + 3];
                return new CoordinateXYZM(x,y,z,m);
            }
            return new Coordinate(x, y);
        }


        /**
         * @see CoordinateSequence#size()
         */
        public int size() {
            return coords.length / dimension;
        }

        /**
         * @see java.lang.Object#clone()
         * @see PackedCoordinateSequence#clone()
         * @deprecated
         */
        public Object clone() {
            return copy();
        }

        /**
         * @see XYCoordinateSequence#copy()
         */
        public DoubleXY copy() {
            double[] clone = Arrays.copyOf(coords, coords.length);
            return new DoubleXY(clone);
        }

        /**
         * @see XYCoordinateSequence#getOrdinate(int, int)
         *      Beware, for performance reasons the ordinate index is not checked, if
         *      it's over dimensions you may not get an exception but a meaningless
         *      value.
         */
        public double getOrdinate(int index, int ordinate) {
            return coords[index * dimension + ordinate];
        }

        /**
         * @see XYCoordinateSequence#setOrdinate(int, int, double)
         */
        public void setOrdinate(int index, int ordinate, double value) {
            coordRef = null;
            coords[index * dimension + ordinate] = value;
        }

        /**
         * @see CoordinateSequence#expandEnvelope(Envelope)
         */
        public Envelope expandEnvelope(Envelope env)
        {
            for (int i = 0; i < coords.length; i += dimension ) {
                // added to make static code analysis happy
                if (i + 1 < coords.length) {
                    env.expandToInclude(coords[i], coords[i + 1]);
                }
            }
            return env;
        }

        public double getRowXY(int idx){
            return this.coords[idx];
        }
    }

    /**
     * Packed coordinate sequence implementation based on floats
     */
    public static class FloatXY extends XYCoordinateSequence {
        private static final long serialVersionUID = -2912346401328954421L;
        /**
         * The packed coordinate array
         */
        float[] coords;

        /**
         * Constructs a packed coordinate sequence from an array of <code>float</code>s
         *
         * @param coords  an array of <code>float</code> values that contains the ordinate values of the sequence
         */
        public FloatXY(float[] coords) {
            if (coords.length % dimension != 0) {
                throw new IllegalArgumentException("Packed array does not contain "
                        + "an integral number of coordinates");
            }
            this.coords = coords;
        }

        /**
         * Constructs a packed coordinate sequence from an array of <code>double</code>s
         *
         * @param coords  an array of <code>double</code> values that contains the ordinate values of the sequence
         */
        public FloatXY(double[] coords) {

            this.coords = new float[coords.length];

            for (int i = 0; i < coords.length; i++) {
                this.coords[i] = (float) coords[i];
            }
        }

        /**
         * Builds a new packed coordinate sequence out of a coordinate array
         *
         * @param coordinates an array of {@link Coordinate}s
         * @param dimension the total number of ordinates that make up a {@link Coordinate} in this sequence.
         */
        public FloatXY(Coordinate[] coordinates, int dimension) {
            this( coordinates, dimension, Math.max(0,dimension-3));
        }

        /**
         * Constructs a packed coordinate sequence out of a coordinate array
         *
         * @param coordinates an array of {@link Coordinate}s
         * @param dimension the total number of ordinates that make up a {@link Coordinate} in this sequence.
         * @param measures the number of measure-ordinates each {@link Coordinate} in this sequence has.
         */
        public FloatXY(Coordinate[] coordinates, int dimension, int measures) {

            if (coordinates == null)
                coordinates = new Coordinate[0];

            coords = new float[coordinates.length * dimension];
            for (int i = 0; i < coordinates.length; i++) {
                int offset = i * dimension;
                coords[offset] = (float) coordinates[i].x;
                coords[offset + 1] = (float) coordinates[i].y;
                if (dimension >= 3)
                    coords[offset + 2] = (float) coordinates[i].getOrdinate(2); // Z or M
                if (dimension >= 4)
                    coords[offset + 3] = (float) coordinates[i].getOrdinate(3); // M
            }
        }

        /**
         * Constructs an empty packed coordinate sequence of a given size and dimension
         *
         * @param size the number of coordinates in this sequence
         */
        public FloatXY(int size) {
            coords = new float[size * this.dimension];
        }

        /**
         * @see XYCoordinateSequence#getCoordinate(int)
         */
        public Coordinate getCoordinateInternal(int i) {
            double x = coords[i * dimension];
            double y = coords[i * dimension + 1];
            if( dimension == 2 && measures == 0 ) {
                return new CoordinateXY(x,y);
            }
            else if (dimension == 3 && measures == 0) {
                double z = coords[i * dimension + 2];
                return new Coordinate(x,y,z);
            }
            else if (dimension == 3 && measures == 1) {
                double m = coords[i * dimension + 2];
                return new CoordinateXYM(x,y,m);
            }
            else if (dimension == 4) {
                double z = coords[i * dimension + 2];
                float m = coords[i * dimension + 3];
                return new CoordinateXYZM(x,y,z,m);
            }
            return new Coordinate(x, y);
        }

        /**
         * Gets the underlying array containing the coordinate values.
         *
         * @return the array of coordinate values
         */
        public float[] getRawCoordinates()
        {
            return coords;
        }

        /**
         * @see CoordinateSequence#size()
         */
        public int size() {
            return coords.length / dimension;
        }

        /**
         * @see java.lang.Object#clone()
         * @see XYCoordinateSequence#clone()
         * @deprecated
         */
        public Object clone() {
            return copy();
        }

        /**
         * @see XYCoordinateSequence#copy()
         */
        public FloatXY copy() {
            float[] clone = Arrays.copyOf(coords, coords.length);
            return new FloatXY(clone);
        }

        /**
         * @see XYCoordinateSequence#getOrdinate(int, int)
         *      For performance reasons the ordinate index is not checked.
         *      If it is larger than the dimension a meaningless
         *      value may be returned.
         */
        public double getOrdinate(int index, int ordinate) {
            return coords[index * dimension + ordinate];
        }

        /**
         * @see XYCoordinateSequence#setOrdinate(int, int, double)
         */
        public void setOrdinate(int index, int ordinate, double value) {
            coordRef = null;
            coords[index * dimension + ordinate] = (float) value;
        }

        /**
         * @see CoordinateSequence#expandEnvelope(Envelope)
         */
        public Envelope expandEnvelope(Envelope env)
        {
            for (int i = 0; i < coords.length; i += dimension ) {
                // added to make static code analysis happy
                if (i + 1 < coords.length) {
                    env.expandToInclude(coords[i], coords[i + 1]);
                }
            }
            return env;
        }

        public double getRowXY(int idx){
            return this.coords[idx];
        }
    }
    public abstract double getRowXY(int idx);
}
