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

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import geocode.kdtree.KDNodeComparator;

import java.util.Comparator;

/**
 * This class works with a postal code file from http://download.geonames.org/export/zip/
 */
@SuppressWarnings({"localvariablename","parametername","membername","PMD.UselessParentheses"})
public class PostalCode extends KDNodeComparator<PostalCode> {
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    public String countryCode;
    public String postalCode;
    public String placeName;
    public String adminName1;
    public String adminCode1;
    public String adminName2;
    public String adminCode2;
    public String adminName3;
    public String adminCode3;
    public double latitude;
    public double longitude;
    public int accuracy;
    public double[] point = new double[3]; // The 3D coordinates of the point

    PostalCode(String row) {
        String[] data = row.split("\t");
        countryCode = data[0];
        postalCode = data[1];
        placeName = data[2];
        adminName1 = data[3];
        adminCode1 = data[4];
        adminName2 = data[5];
        adminCode2 = data[6];
        adminName3 = data[7];
        adminCode3 = data[8];
        latitude = Double.parseDouble(data[9]);
        longitude = Double.parseDouble(data[10]);
        setPoint();

        // Accuracy not always provided
        if (data.length == 12) {
            accuracy = Integer.parseInt(data[11]);
        }
    }

    PostalCode(Double latitude, Double longitude) {
        this.placeName = "Search";
        this.latitude = latitude;
        this.longitude = longitude;
        setPoint();
    }

    /*
     * Converts lat/lon to a vector (https://www.movable-type.co.uk/scripts/latlong-vectors.html)
     */
    private void setPoint() {
        point[X] = cos(toRadians(latitude)) * cos(toRadians(longitude));
        point[Y] = cos(toRadians(latitude)) * sin(toRadians(longitude));
        point[Z] = sin(toRadians(latitude));
    }

    @Override
    public String toString() {
        return placeName;
    }

    @Override
    protected double squaredDistance(PostalCode other) {
        double x = this.point[X] - other.point[X];
        double y = this.point[Y] - other.point[Y];
        double z = this.point[Z] - other.point[Z];
        return (x * x) + (y * y) + (z * z);
    }

    @Override
    protected double axisSquaredDistance(PostalCode other, int axis) {
        double distance = point[axis] - other.point[axis];
        return distance * distance;
    }

    @Override
    protected double haversineDistance(PostalCode other, double radius) {
        double dLat = toRadians(other.latitude - this.latitude);
        double dLon = toRadians(other.longitude - this.longitude);
        double lat1 = toRadians(this.latitude);
        double lat2 = toRadians(other.latitude);

        double a = pow(sin(dLat / 2),2) + pow(sin(dLon / 2),2) * cos(lat1) * cos(lat2);
        return 2 * asin(sqrt(a)) * radius;
    }

    @Override
    protected Comparator<PostalCode> getComparator(int axis) {
        return GeoNameComparator.values()[axis];
    }

    protected enum GeoNameComparator implements Comparator<PostalCode> {
        x {
            @Override
            public int compare(PostalCode a, PostalCode b) {
                return Double.compare(a.point[0], b.point[0]);
            }
        },
        y {
            @Override
            public int compare(PostalCode a, PostalCode b) {
                return Double.compare(a.point[1], b.point[1]);
            }
        },
        z {
            @Override
            public int compare(PostalCode a, PostalCode b) {
                return Double.compare(a.point[2], b.point[2]);
            }
        }
    }
}
