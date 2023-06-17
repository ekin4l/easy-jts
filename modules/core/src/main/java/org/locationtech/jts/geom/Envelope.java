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
package org.locationtech.jts.geom;

import java.io.Serializable;

/**
 *  Defines a rectangular region of the 2D coordinate plane.
 *  It is often used to represent the bounding box of a {@link Geometry},
 *  e.g. the minimum and maximum x and y values of the {@link Coordinate}s.
 *  <p>
 *  Envelopes support infinite or half-infinite regions, by using the values of
 *  <code>Double.POSITIVE_INFINITY</code> and <code>Double.NEGATIVE_INFINITY</code>.
 *  Envelope objects may have a null value.
 *  <p>
 *  When Envelope objects are created or initialized,
 *  the supplies extent values are automatically sorted into the correct order.
 *
 *@version 1.7
 */
public class Envelope
    implements Comparable, Serializable
{
    private static final long serialVersionUID = 5873921885273102420L;

    public int hashCode() {
        //Algorithm from Effective Java by Joshua Bloch [Jon Aquino]
        int result = 17;
        result = 37 * result + Coordinate.hashCode(bound[0]);
        result = 37 * result + Coordinate.hashCode(bound[2]);
        result = 37 * result + Coordinate.hashCode(bound[1]);
        result = 37 * result + Coordinate.hashCode(bound[3]);
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

  private double[] bound = new double[4];
  /**
   *  the minimum x-coordinate
   */
//  private double bound[0];

  /**
   *  the maximum x-coordinate
   */
//  private double bound[2];

  /**
   *  the minimum y-coordinate
   */
//  private double bound[1];

  /**
   *  the maximum y-coordinate
   */
//  private double bound[3];

  /**
   *  Creates a null <code>Envelope</code>.
   */
  public Envelope() {
    init();
  }

  /**
   *  Creates an <code>Envelope</code> for a region defined by maximum and minimum values.
   *
   *@param  x1  the first x-value
   *@param  x2  the second x-value
   *@param  y1  the first y-value
   *@param  y2  the second y-value
   */
  public Envelope(double x1, double x2, double y1, double y2)
  {
    init(x1, x2, y1, y2);
  }

  /**
   *  Creates an <code>Envelope</code> for a region defined by two Coordinates.
   *
   *@param  p1  the first Coordinate
   *@param  p2  the second Coordinate
   */
  public Envelope(Coordinate p1, Coordinate p2)
  {
    init(p1.x, p2.x, p1.y, p2.y);
  }

  /**
   *  Creates an <code>Envelope</code> for a region defined by a single Coordinate.
   *
   *@param  p  the Coordinate
   */
  public Envelope(Coordinate p)
  {
    init(p.x, p.x, p.y, p.y);
  }

  /**
   *  Create an <code>Envelope</code> from an existing Envelope.
   *
   *@param  env  the Envelope to initialize from
   */
  public Envelope(Envelope env)
  {
    init(env);
  }

  /**
   *  Initialize to a null <code>Envelope</code>.
   */
  public void init()
  {
    setToNull();
  }

  /**
   *  Initialize an <code>Envelope</code> for a region defined by maximum and minimum values.
   *
   *@param  x1  the first x-value
   *@param  x2  the second x-value
   *@param  y1  the first y-value
   *@param  y2  the second y-value
   */
  public void init(double x1, double x2, double y1, double y2)
  {
    if (x1 < x2) {
//      bound[0] = x1;
//      bound[2] = x2;
      bound[0] = x1;
      bound[2] = x2;
    }
    else {
//      bound[0] = x2;
//      bound[2] = x1;
      bound[0] = x2;
      bound[2] = x1;
    }
    if (y1 < y2) {
//      bound[1] = y1;
//      bound[3] = y2;
      bound[1] = y1;
      bound[3] = y2;
    }
    else {
//      bound[1] = y2;
//      bound[3] = y1;
      bound[1] = y2;
      bound[3] = y1;
    }
  }

  /**
   * Creates a copy of this envelope object.
   * 
   * @return a copy of this envelope
   */
  public Envelope copy() {
    return new Envelope(this);
  }
  
  /**
   *  Initialize an <code>Envelope</code> to a region defined by two Coordinates.
   *
   *@param  p1  the first Coordinate
   *@param  p2  the second Coordinate
   */
  public void init(Coordinate p1, Coordinate p2)
  {
    init(p1.x, p2.x, p1.y, p2.y);
  }

  /**
   *  Initialize an <code>Envelope</code> to a region defined by a single Coordinate.
   *
   *@param  p  the coordinate
   */
  public void init(Coordinate p)
  {
    init(p.x, p.x, p.y, p.y);
  }

  /**
   *  Initialize an <code>Envelope</code> from an existing Envelope.
   *
   *@param  env  the Envelope to initialize from
   */
  public void init(Envelope env)
  {
    this.bound[0] = env.bound[0];
    this.bound[1] = env.bound[1];
    this.bound[2] = env.bound[2];
    this.bound[3] = env.bound[3];
    
  }


  /**
   *  Makes this <code>Envelope</code> a "null" envelope, that is, the envelope
   *  of the empty geometry.
   */
  public void setToNull() {
    bound[0] = 0;
    bound[2] = -1;
    bound[1] = 0;
    bound[3] = -1;
  }

  /**
   *  Returns <code>true</code> if this <code>Envelope</code> is a "null"
   *  envelope.
   *
   *@return    <code>true</code> if this <code>Envelope</code> is uninitialized
   *      or is the envelope of the empty geometry.
   */
  public boolean isNull() {
//    return bound[2] < bound[0];
    return bound[2] < bound[0];
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
    return bound[2] - bound[0];
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
    return bound[3] - bound[1];
  }

  /**
   * Gets the length of the diameter (diagonal) of the envelope.
   * 
   * @return the diameter length
   */
  public double getDiameter() {
    if (isNull()) {
      return 0;
    }
    double w = getWidth();
    double h = getHeight();
    return Math.hypot(w, h);
  }
  /**
   *  Returns the <code>Envelope</code>s minimum x-value. min x &gt; max x
   *  indicates that this is a null <code>Envelope</code>.
   *
   *@return    the minimum x-coordinate
   */
  public double getMinX() {
    return bound[0];
  }

  /**
   *  Returns the <code>Envelope</code>s maximum x-value. min x &gt; max x
   *  indicates that this is a null <code>Envelope</code>.
   *
   *@return    the maximum x-coordinate
   */
  public double getMaxX() {
    return bound[2];
  }

  /**
   *  Returns the <code>Envelope</code>s minimum y-value. min y &gt; max y
   *  indicates that this is a null <code>Envelope</code>.
   *
   *@return    the minimum y-coordinate
   */
  public double getMinY() {
    return bound[1];
  }

  /**
   *  Returns the <code>Envelope</code>s maximum y-value. min y &gt; max y
   *  indicates that this is a null <code>Envelope</code>.
   *
   *@return    the maximum y-coordinate
   */
  public double getMaxY() {
    return bound[3];
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
   *  Enlarges this <code>Envelope</code> so that it contains
   *  the given {@link Coordinate}. 
   *  Has no effect if the point is already on or within the envelope.
   *
   *@param  p  the Coordinate to expand to include
   */
  public void expandToInclude(Coordinate p)
  {
    expandToInclude(p.x, p.y);
  }

  /**
   * Expands this envelope by a given distance in all directions.
   * Both positive and negative distances are supported.
   *
   * @param distance the distance to expand the envelope
   */
  public void expandBy(double distance)
  {
    expandBy(distance, distance);
  }

  /**
   * Expands this envelope by a given distance in all directions.
   * Both positive and negative distances are supported.
   *
   * @param deltaX the distance to expand the envelope along the the X axis
   * @param deltaY the distance to expand the envelope along the the Y axis
   */
  public void expandBy(double deltaX, double deltaY)
  {
    if (isNull()) return;

    bound[0] -= deltaX;
    bound[2] += deltaX;
    bound[1] -= deltaY;
    bound[3] += deltaY;

    // check for envelope disappearing
    if (bound[0] > bound[2] || bound[1] > bound[3])
      setToNull();
  }

  /**
   *  Enlarges this <code>Envelope</code> so that it contains
   *  the given point. 
   *  Has no effect if the point is already on or within the envelope.
   *
   *@param  x  the value to lower the minimum x to or to raise the maximum x to
   *@param  y  the value to lower the minimum y to or to raise the maximum y to
   */
  public void expandToInclude(double x, double y) {
    if (isNull()) {
      bound[0] = x;
      bound[2] = x;
      bound[1] = y;
      bound[3] = y;
    }
    else {
      if (x < bound[0]) {
        bound[0] = x;
      }
      if (x > bound[2]) {
        bound[2] = x;
      }
      if (y < bound[1]) {
        bound[1] = y;
      }
      if (y > bound[3]) {
        bound[3] = y;
      }
    }
  }

  /**
   *  Enlarges this <code>Envelope</code> so that it contains
   *  the <code>other</code> Envelope. 
   *  Has no effect if <code>other</code> is wholly on or
   *  within the envelope.
   *
   *@param  other  the <code>Envelope</code> to expand to include
   */
  public void expandToInclude(Envelope other) {
    if (other.isNull()) {
      return;
    }
    if (isNull()) {
      bound[0] = other.bound[0];
      bound[2] = other.bound[2];
      bound[1] = other.bound[1];
      bound[3] = other.bound[3];
    }
    else {
      if (other.bound[0] < bound[0]) {
        bound[0] = other.bound[0];
      }
      if (other.bound[2] > bound[2]) {
        bound[2] = other.bound[2];
      }
      if (other.bound[1] < bound[1]) {
        bound[1] = other.bound[1];
      }
      if (other.bound[3] > bound[3]) {
        bound[3] = other.bound[3];
      }
    }
  }

  /**
   * Translates this envelope by given amounts in the X and Y direction.
   *
   * @param transX the amount to translate along the X axis
   * @param transY the amount to translate along the Y axis
   */
  public void translate(double transX, double transY) {
    if (isNull()) {
      return;
    }
    init(bound[0] + transX, bound[2] + transX,
         bound[1] + transY, bound[3] + transY);
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
        (bound[0] + bound[2]) / 2.0,
        (bound[1] + bound[3]) / 2.0);
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

    double x1 = bound[0] > env.bound[0] ? bound[0] : env.bound[0];
    double y1 = bound[1] > env.bound[1] ? bound[1] : env.bound[1];
    double x2 = bound[2] < env.bound[2] ? bound[2] : env.bound[2];
    double y2 = bound[3] < env.bound[3] ? bound[3] : env.bound[3];
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
    return !(other.bound[0] > bound[2] ||
        other.bound[2] < bound[0] ||
        other.bound[1] > bound[3] ||
        other.bound[3] < bound[1]);
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
    if (envMinX > bound[2]) return false;
    
    double envMaxX = (a.x > b.x) ? a.x : b.x;
    if (envMaxX < bound[0]) return false;
    
    double envMinY = (a.y < b.y) ? a.y : b.y;
    if (envMinY > bound[3]) return false;
    
    double envMaxY = (a.y > b.y) ? a.y : b.y;
    if (envMaxY < bound[1]) return false;
    
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
    return ! (x > bound[2] ||
        x < bound[0] ||
        y > bound[3] ||
        y < bound[1]);
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
    return x >= bound[0] &&
        x <= bound[2] &&
        y >= bound[1] &&
        y <= bound[3];
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
    return other.bound[0] >= bound[0] &&
        other.bound[2] <= bound[2] &&
        other.bound[1] >= bound[1] &&
        other.bound[3] <= bound[3];
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
    if (bound[2] < env.bound[0]) 
      dx = env.bound[0] - bound[2];
    else if (bound[0] > env.bound[2]) 
      dx = bound[0] - env.bound[2];
    
    double dy = 0.0;
    if (bound[3] < env.bound[1]) 
      dy = env.bound[1] - bound[3];
    else if (bound[1] > env.bound[3]) dy = bound[1] - env.bound[3];

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
    return bound[2] == otherEnvelope.bound[2] &&
        bound[3] == otherEnvelope.bound[3] &&
        bound[0] == otherEnvelope.bound[0] &&
        bound[1] == otherEnvelope.bound[1];
  }

  public String toString()
  {
    return "Env[" + bound[0] + " : " + bound[2] + ", " + bound[1] + " : " + bound[3] + "]";
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
    if (bound[0] < env.bound[0]) return -1;
    if (bound[0] > env.bound[0]) return 1;
    if (bound[1] < env.bound[1]) return -1;
    if (bound[1] > env.bound[1]) return 1;
    if (bound[2] < env.bound[2]) return -1;
    if (bound[2] > env.bound[2]) return 1;
    if (bound[3] < env.bound[3]) return -1;
    if (bound[3] > env.bound[3]) return 1;
    return 0;
    
    
  }
}

