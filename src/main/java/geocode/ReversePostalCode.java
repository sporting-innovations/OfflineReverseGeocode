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

import geocode.kdtree.KDTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Uses KD-trees to quickly find the nearest point
 *
 * ReversePostalCode reversePostalCode = new ReversePostalCode(new FileInputStream("c:\\AU.txt"), true);
 * System.out.println("Nearest to -23.456, 123.456 is " + reversePostalCode.nearestPostalCode(-23.456, 123.456));
 */
public class ReversePostalCode {
    KDTree<PostalCode> kdTree;

    // Get postal code file from http://download.geonames.org/export/zip/
    /**
     * Parse the zipped postal code file.
     * @param zippedPostalCodes a {@link ZipInputStream} zip file downloaded from http://download.geonames.org/export/zip/; can not be null.
     *
     * @throws IOException if there is a problem reading the {@link ZipInputStream}.
     * @throws NullPointerException if zippedPostalCodes is {@code null}.
     */
    public ReversePostalCode(ZipInputStream zippedPostalCodes) throws IOException {
        // Depends on which zip file is given -
        // Country specific zip files have readme files that we should ignore
        ZipEntry entry;
        do {
            entry = zippedPostalCodes.getNextEntry();
        } while (entry.getName().equals("readme.txt"));

        createKdTree(zippedPostalCodes);
    }

    /**
     * Parse the raw text postal codes file.
     * @param postalCodes the text file downloaded from http://download.geonames.org/export/zip/; can not be null.
     *
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if postalCodes is {@code null}.
     */
    public ReversePostalCode(InputStream postalCodes) throws IOException {
        createKdTree(postalCodes);
    }
    private void createKdTree(InputStream postalCodes) throws IOException {
        ArrayList<PostalCode> postalCodesList = new ArrayList<>();
        // Read the postal codes file in the directory
        try (BufferedReader in = new BufferedReader(new InputStreamReader(postalCodes))) {
            String str;
            while ((str = in.readLine()) != null) {
                postalCodesList.add(new PostalCode(str));
            }
        }
        kdTree = new KDTree<>(postalCodesList);
    }

    public PostalCode nearestPostalCode(double latitude, double longitude, Double maxDistance) {
        return kdTree.findNearest(new PostalCode(latitude, longitude), maxDistance);
    }
}