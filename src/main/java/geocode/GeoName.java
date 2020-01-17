/*
The MIT License (MIT)
[OSI Approved License]
The MIT License (MIT)

Copyright (c) 2014 Daniel Glasson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package geocode;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import geocode.kdtree.KDNodeComparator;

import java.util.Comparator;

/**
 * Created by Daniel Glasson on 18/05/2014.
 * This class works with a placenames files from http://download.geonames.org/export/dump/
 */
@SuppressWarnings({"localvariablename","parametername", "PMD.UselessParentheses"})
public class GeoName extends KDNodeComparator<GeoName> {
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    public String name;
    public boolean majorPlace; // Major or minor place
    public double latitude;
    public double longitude;
    public double[] point = new double[3]; // The 3D coordinates of the point
    public String country;

    GeoName(String data) {
        String[] names = data.split("\t");
        name = names[1];
        majorPlace = names[6].equals("P");
        latitude = Double.parseDouble(names[4]);
        longitude = Double.parseDouble(names[5]);
        setPoint();
        country = names[8];
    }

    GeoName(Double latitude, Double longitude) {
        name = country = "Search";
        this.latitude = latitude;
        this.longitude = longitude;
        setPoint();
    }

    private void setPoint() {
        point[X] = cos(toRadians(latitude)) * cos(toRadians(longitude));
        point[Y] = cos(toRadians(latitude)) * sin(toRadians(longitude));
        point[Z] = sin(toRadians(latitude));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected double squaredDistance(GeoName other) {
        double x = this.point[X] - other.point[X];
        double y = this.point[Y] - other.point[Y];
        double z = this.point[Z] - other.point[Z];
        return (x * x) + (y * y) + (z * z);
    }

    @Override
    protected double axisSquaredDistance(GeoName other, int axis) {
        double distance = point[axis] - other.point[axis];
        return distance * distance;
    }

    /*
     * Distance formulas taken from https://www.movable-type.co.uk/scripts/latlong-vectors.html
     */
    @Override
    protected double distance(GeoName other, double radius) {
        // distance = atan2(cross product, dot product) * radius
        return atan2(cross(other), dot(other)) * radius;
    }

    @Override
    protected Comparator<GeoName> getComparator(int axis) {
        return GeoNameComparator.values()[axis];
    }

    protected enum GeoNameComparator implements Comparator<GeoName> {
        x {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[0], b.point[0]);
            }
        },
        y {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[1], b.point[1]);
            }
        },
        z {
            @Override
            public int compare(GeoName a, GeoName b) {
                return Double.compare(a.point[2], b.point[2]);
            }
        };
    }

    /**
     * Multiplies the 3D point by the supplied 3D point using cross (vector) product.
     * Formula taken https://www.movable-type.co.uk/scripts/latlong-vectors.html
     * @param other - GeoName containing 3D point to be crossed with this GeoName's 3D point.
     * @return cross product's length
     */
    private double cross(GeoName other) {
        // x = (this.y * other.z) - (this.z * other.y)
        // y = (this.z * other.x) - (this.x * other.z)
        // z = (this.x * other.y) - (this.y * other.x)
        double x = (this.point[Y] * other.point[Z]) - (this.point[Z] * other.point[Y]);
        double y = (this.point[Z] * other.point[X]) - (this.point[X] * other.point[Z]);
        double z = (this.point[X] * other.point[Y]) - (this.point[Y] * other.point[X]);

        // length = Math.sqrt((x * x) + (y * y) + (z * z))
        return sqrt((x * x) + (y * y) + (z * z));
    }

    /**
     * Multiplies the 3D point by the supplied 3D point using dot (scalar) product.
     * Formula taken https://www.movable-type.co.uk/scripts/latlong-vectors.html
     * @param other - GeoName containing 3D point to be dotted with this GeoName's 3D point.
     * @return dot product
     */
    private double dot(GeoName other) {
        // dot = (this.x * other.x) + (this.y * other.y) + (this.z * other.z)
        return (this.point[X] * other.point[X]) + (this.point[Y] * other.point[Y]) + (this.point[Z] * other.point[Z]);
    }
}
