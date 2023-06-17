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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;

import java.io.Serializable;

public class XYCoordinateReferEvelope implements Comparable, Serializable
{
    private static final long serialVersionUID = 5873954885273109835L;
    private final XYCoordinateSequence coordSeq;

    public int hashCode() {
        //Algorithm from Effective Java by Joshua Bloch [Jon Aquino]
        int result = 17;
        result = 37 * result + Coordinate.hashCode(getMinX());
        result = 37 * result + Coordinate.hashCode(getMinY());
        result = 37 * result + Coordinate.hashCode(getMaxX());
        result = 37 * result + Coordinate.hashCode(getMaxY());
        return result;
    }

    /**
     * Test the point q to see whether it intersects the Envelope defined by p1-p2
     * @param p1 one extremal point of the envelope
     * @param p2 another extremal point of the envelope
     * @param q the point to test for intersection
     * @return <code>true</code> if q intersects the envelope p1-p2
     */
    public static boolean intersects(Coordinate p1, Coordinate p2, Coordinate q)
    {
        //OptimizeIt shows that Math#min and Math#max here are a bottleneck.
        //Replace with direct comparisons. [Jon Aquino]
        if (((q.x >= (p1.x < p2.x ? p1.x : p2.x)) && (q.x <= (p1.x > p2.x ? p1.x : p2.x))) &&
                ((q.y >= (p1.y < p2.y ? p1.y : p2.y)) && (q.y <= (p1.y > p2.y ? p1.y : p2.y)))) {
            return true;
        }
        return false;
    }

    /**
     * Tests whether the envelope defined by p1-p2
     * and the envelope defined by q1-q2
     * intersect.
     *
     * @param p1 one extremal point of the envelope P
     * @param p2 another extremal point of the envelope P
     * @param q1 one extremal point of the envelope Q
     * @param q2 another extremal point of the envelope Q
     * @return <code>true</code> if Q intersects P
     */
    public static boolean intersects(Coordinate p1, Coordinate p2, Coordinate q1, Coordinate q2)
    {
        double minq = Math.min(q1.x, q2.x);
        double maxq = Math.max(q1.x, q2.x);
        double minp = Math.min(p1.x, p2.x);
        double maxp = Math.max(p1.x, p2.x);

        if( minp > maxq )
            return false;
        if( maxp < minq )
            return false;

        minq = Math.min(q1.y, q2.y);
        maxq = Math.max(q1.y, q2.y);
        minp = Math.min(p1.y, p2.y);
        maxp = Math.max(p1.y, p2.y);

        if( minp > maxq )
            return false;
        if( maxp < minq )
            return false;
        return true;
    }

    private int minXIdx;

    private int minYIdx;

    private int maxXIdx;

    private int maxYIdx;



    /**
     *  Creates an <code>Envelope</code> for a region defined by a single Coordinate.
     *
     */
    public XYCoordinateReferEvelope(XYCoordinateSequence coordSeq)
    {
        this.coordSeq = coordSeq;
        init();
    }


    /**
     *  Initialize a  <code>Envelope</code>.
     */
    public void init()
    {
        if(this.coordSeq==null|| this.coordSeq.size()==0) {
            setToNull();
        }else{
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            int xySize = this.coordSeq.size()*2;
            for(int i=0;i<xySize;){
                if(coordSeq.getRowXY(i) < minX){
                    minXIdx = i;
                    minX = coordSeq.getRowXY(i);
                }
                if(coordSeq.getRowXY(i) > maxX){
                    maxXIdx = i;
                    maxX = coordSeq.getRowXY(i);
                }
                int nextIdx = i+1;
                if(coordSeq.getRowXY(nextIdx) < minY){
                    minYIdx = nextIdx;
                    minY = coordSeq.getRowXY(nextIdx);
                }
                if(coordSeq.getRowXY(nextIdx) > maxY){
                    maxYIdx = nextIdx;
                    maxY = coordSeq.getRowXY(nextIdx);
                }
                i += 2;
            }


        }
    }




    /**
     *  Makes this <code>Envelope</code> a "null" envelope, that is, the envelope
     *  of the empty geometry.
     */
    public void setToNull() {
        minXIdx = -1;
        minYIdx = -1;
        maxXIdx = -1;
        maxYIdx = -1;
    }

    /**
     *  Returns <code>true</code> if this <code>Envelope</code> is a "null"
     *  envelope.
     *
     *@return    <code>true</code> if this <code>Envelope</code> is uninitialized
     *      or is the envelope of the empty geometry.
     */
    public boolean isNull() {
        return minXIdx==-1;
    }

    /**
     *  Returns the difference between the maximum and minimum x values.
     *
     *@return    max x - min x, or 0 if this is a null <code>Envelope</code>
     */
    public double getWidth() {
        if (isNull()) {
            return 0;
        }
        return getMaxX() - getMinX();
    }

    /**
     *  Returns the difference between the maximum and minimum y values.
     *
     *@return    max y - min y, or 0 if this is a null <code>Envelope</code>
     */
    public double getHeight() {
        if (isNull()) {
            return 0;
        }
        return getMaxY() - getMinY();
    }


    /**
     *  Returns the <code>Envelope</code>s minimum x-value. min x &gt; max x
     *  indicates that this is a null <code>Envelope</code>.
     *
     *@return    the minimum x-coordinate
     */
    public double getMinX() {
        return this.coordSeq.getRowXY(minXIdx);
    }

    /**
     *  Returns the <code>Envelope</code>s maximum x-value. min x &gt; max x
     *  indicates that this is a null <code>Envelope</code>.
     *
     *@return    the maximum x-coordinate
     */
    public double getMaxX() {
        return this.coordSeq.getRowXY(maxXIdx);
    }

    /**
     *  Returns the <code>Envelope</code>s minimum y-value. min y &gt; max y
     *  indicates that this is a null <code>Envelope</code>.
     *
     *@return    the minimum y-coordinate
     */
    public double getMinY() {
        return this.coordSeq.getRowXY(minYIdx);
    }

    /**
     *  Returns the <code>Envelope</code>s maximum y-value. min y &gt; max y
     *  indicates that this is a null <code>Envelope</code>.
     *
     *@return    the maximum y-coordinate
     */
    public double getMaxY() {
        return this.coordSeq.getRowXY(maxYIdx);
    }

    /**
     * Gets the area of this envelope.
     *
     * @return the area of the envelope
     * @return 0.0 if the envelope is null
     */
    public double getArea()
    {
        return getWidth() * getHeight();
    }

    /**
     * Gets the minimum extent of this envelope across both dimensions.
     *
     * @return the minimum extent of this envelope
     */
    public double minExtent()
    {
        if (isNull()) return 0.0;
        double w = getWidth();
        double h = getHeight();
        if (w < h) return w;
        return h;
    }

    /**
     * Gets the maximum extent of this envelope across both dimensions.
     *
     * @return the maximum extent of this envelope
     */
    public double maxExtent()
    {
        if (isNull()) return 0.0;
        double w = getWidth();
        double h = getHeight();
        if (w > h) return w;
        return h;
    }



    /**
     * Computes the coordinate of the centre of this envelope (as long as it is non-null
     *
     * @return the centre coordinate of this envelope
     * <code>null</code> if the envelope is null
     */
    public Coordinate centre() {
        if (isNull()) return null;
        return new Coordinate(
                (getMinX() + getMaxX()) / 2.0,
                (getMinY() + getMaxY()) / 2.0);
    }

    /**
     * Computes the intersection of two {@link Envelope}s.
     *
     * @param env the envelope to intersect with
     * @return a new Envelope representing the intersection of the envelopes (this will be
     * the null envelope if either argument is null, or they do not intersect
     */
    public Envelope intersection(Envelope env)
    {
        if (isNull() || env.isNull() || ! intersects(env)) return new Envelope();

        double x1 = getMinX() > env.getMinX() ? getMinX() : env.getMinX();
        double y1 = getMinY() > env.getMinY() ? getMinY() : env.getMinY();
        double x2 = getMaxX() < env.getMaxX() ? getMaxX() : env.getMaxX();
        double y2 = getMaxY() < env.getMaxY() ? getMaxY() : env.getMaxY();
        return new Envelope(x1,x2,y1,y2);
    }

    /**
     * Tests if the region defined by <code>other</code>
     * intersects the region of this <code>Envelope</code>.
     * <p>
     * A null envelope never intersects.
     *
     *@param  other  the <code>Envelope</code> which this <code>Envelope</code> is
     *          being checked for intersecting
     *@return        <code>true</code> if the <code>Envelope</code>s intersect
     */
    public boolean intersects(Envelope other) {
        if (isNull() || other.isNull()) { return false; }
        return !(other.getMinX() > getMaxX() ||
                other.getMaxX() < getMinX() ||
                other.getMinY() > getMaxY() ||
                other.getMaxY() <getMinY());
    }


    /**
     * Tests if the extent defined by two extremal points
     * intersects the extent of this <code>Envelope</code>.
     *
     *@param a a point
     *@param b another point
     *@return   <code>true</code> if the extents intersect
     */
    public boolean intersects(Coordinate a, Coordinate b) {
        if (isNull()) { return false; }

        double envMinX = (a.x < b.x) ? a.x : b.x;
        if (envMinX > getMaxX()) return false;

        double envMaxX = (a.x > b.x) ? a.x : b.x;
        if (envMaxX < getMinX()) return false;

        double envMinY = (a.y < b.y) ? a.y : b.y;
        if (envMinY > getMaxY()) return false;

        double envMaxY = (a.y > b.y) ? a.y : b.y;
        if (envMaxY < getMinY()) return false;

        return true;
    }

    /**
     * Tests if the region defined by <code>other</code>
     * is disjoint from the region of this <code>Envelope</code>.
     * <p>
     * A null envelope is always disjoint.
     *
     *@param  other  the <code>Envelope</code> being checked for disjointness
     *@return        <code>true</code> if the <code>Envelope</code>s are disjoint
     *
     *@see #intersects(Envelope)
     */
    public boolean disjoint(Envelope other) {
        return ! intersects(other);
    }

    /**
     * @deprecated Use #intersects instead. In the future, #overlaps may be
     * changed to be a true overlap check; that is, whether the intersection is
     * two-dimensional.
     */
    public boolean overlaps(Envelope other) {
        return intersects(other);
    }

    /**
     * Tests if the point <code>p</code>
     * intersects (lies inside) the region of this <code>Envelope</code>.
     *
     *@param  p  the <code>Coordinate</code> to be tested
     *@return <code>true</code> if the point intersects this <code>Envelope</code>
     */
    public boolean intersects(Coordinate p) {
        return intersects(p.x, p.y);
    }
    /**
     * @deprecated Use #intersects instead.
     */
    public boolean overlaps(Coordinate p) {
        return intersects(p);
    }
    /**
     *  Check if the point <code>(x, y)</code>
     *  intersects (lies inside) the region of this <code>Envelope</code>.
     *
     *@param  x  the x-ordinate of the point
     *@param  y  the y-ordinate of the point
     *@return        <code>true</code> if the point overlaps this <code>Envelope</code>
     */
    public boolean intersects(double x, double y) {
        if (isNull()) return false;
        return ! (x > getMaxX() ||
                x < getMinX() ||
                y > getMaxY() ||
                y < getMinY());
    }
    /**
     * @deprecated Use #intersects instead.
     */
    public boolean overlaps(double x, double y) {
        return intersects(x, y);
    }

    /**
     * Tests if the <code>Envelope other</code>
     * lies wholely inside this <code>Envelope</code> (inclusive of the boundary).
     * <p>
     * Note that this is <b>not</b> the same definition as the SFS <tt>contains</tt>,
     * which would exclude the envelope boundary.
     *
     *@param  other the <code>Envelope</code> to check
     *@return true if <code>other</code> is contained in this <code>Envelope</code>
     *
     *@see #covers(Envelope)
     */
    public boolean contains(Envelope other) {
        return covers(other);
    }

    /**
     * Tests if the given point lies in or on the envelope.
     * <p>
     * Note that this is <b>not</b> the same definition as the SFS <tt>contains</tt>,
     * which would exclude the envelope boundary.
     *
     *@param  p  the point which this <code>Envelope</code> is
     *      being checked for containing
     *@return    <code>true</code> if the point lies in the interior or
     *      on the boundary of this <code>Envelope</code>.
     *
     *@see #covers(Coordinate)
     */
    public boolean contains(Coordinate p) {
        return covers(p);
    }

    /**
     * Tests if the given point lies in or on the envelope.
     * <p>
     * Note that this is <b>not</b> the same definition as the SFS <tt>contains</tt>,
     * which would exclude the envelope boundary.
     *
     *@param  x  the x-coordinate of the point which this <code>Envelope</code> is
     *      being checked for containing
     *@param  y  the y-coordinate of the point which this <code>Envelope</code> is
     *      being checked for containing
     *@return    <code>true</code> if <code>(x, y)</code> lies in the interior or
     *      on the boundary of this <code>Envelope</code>.
     *
     *@see #covers(double, double)
     */
    public boolean contains(double x, double y) {
        return covers(x, y);
    }

    /**
     * Tests if an envelope is properly contained in this one.
     * The envelope is properly contained if it is contained
     * by this one but not equal to it.
     *
     * @param other the envelope to test
     * @return true if the envelope is properly contained
     */
    public boolean containsProperly(Envelope other) {
        if (equals(other))
            return false;
        return covers(other);
    }

    /**
     * Tests if the given point lies in or on the envelope.
     *
     *@param  x  the x-coordinate of the point which this <code>Envelope</code> is
     *      being checked for containing
     *@param  y  the y-coordinate of the point which this <code>Envelope</code> is
     *      being checked for containing
     *@return    <code>true</code> if <code>(x, y)</code> lies in the interior or
     *      on the boundary of this <code>Envelope</code>.
     */
    public boolean covers(double x, double y) {
        if (isNull()) return false;
        return x >= getMinX() &&
                x <= getMaxX() &&
                y >= getMinY() &&
                y <= getMaxY();
    }

    /**
     * Tests if the given point lies in or on the envelope.
     *
     *@param  p  the point which this <code>Envelope</code> is
     *      being checked for containing
     *@return    <code>true</code> if the point lies in the interior or
     *      on the boundary of this <code>Envelope</code>.
     */
    public boolean covers(Coordinate p) {
        return covers(p.x, p.y);
    }

    /**
     * Tests if the <code>Envelope other</code>
     * lies wholely inside this <code>Envelope</code> (inclusive of the boundary).
     *
     *@param  other the <code>Envelope</code> to check
     *@return true if this <code>Envelope</code> covers the <code>other</code>
     */
    public boolean covers(Envelope other) {
        if (isNull() || other.isNull()) { return false; }
        return other.getMinX() >= getMinX() &&
                other.getMaxX() <= getMaxX() &&
                other.getMinY() >= getMinY() &&
                other.getMaxY() <= getMaxY();
    }

    /**
     * Computes the distance between this and another
     * <code>Envelope</code>.
     * The distance between overlapping Envelopes is 0.  Otherwise, the
     * distance is the Euclidean distance between the closest points.
     */
    public double distance(Envelope env)
    {
        if (intersects(env)) return 0;

        double dx = 0.0;
        if (getMaxX() < env.getMinX())
            dx = env.getMinX() - getMaxX();
        else if (getMinX() > env.getMaxX())
            dx = getMinX() - env.getMaxX();

        double dy = 0.0;
        if (getMaxY() < env.getMinY())
            dy = env.getMinY() - getMaxY();
        else if (getMinY() > env.getMaxY()) dy = getMinY() - env.getMaxY();

        // if either is zero, the envelopes overlap either vertically or horizontally
        if (dx == 0.0) return dy;
        if (dy == 0.0) return dx;
        return Math.hypot(dx, dy);
    }

    public boolean equals(Object other) {
        if (!(other instanceof Envelope)) {
            return false;
        }
        Envelope otherEnvelope = (Envelope) other;
        if (isNull()) {
            return otherEnvelope.isNull();
        }
        return getMaxX() == otherEnvelope.getMaxX() &&
                getMaxY() == otherEnvelope.getMaxY() &&
                getMinX() == otherEnvelope.getMinX() &&
                getMinY() == otherEnvelope.getMinY();
    }

    public String toString()
    {
        return "Env[" + getMinX() + " : " + getMaxX() + ", " + getMinY() + " : " + getMaxY() + "]";
    }

    /**
     * Compares two envelopes using lexicographic ordering.
     * The ordering comparison is based on the usual numerical
     * comparison between the sequence of ordinates.
     * Null envelopes are less than all non-null envelopes.
     *
     * @param o an Envelope object
     */
    public int compareTo(Object o) {
        Envelope env = (Envelope) o;
        // compare nulls if present
        if (isNull()) {
            if (env.isNull()) return 0;
            return -1;
        }
        else {
            if (env.isNull()) return 1;
        }
        // compare based on numerical ordering of ordinates
        if (getMinX() < env.getMinX()) return -1;
        if (getMinX() > env.getMinX()) return 1;
        if (getMinY() < env.getMinY()) return -1;
        if (getMinY() > env.getMinY()) return 1;
        if (getMaxX() < env.getMaxX()) return -1;
        if (getMaxX() > env.getMaxX()) return 1;
        if (getMaxY() < env.getMaxY()) return -1;
        if (getMaxY() > env.getMaxY()) return 1;
        return 0;


    }
}