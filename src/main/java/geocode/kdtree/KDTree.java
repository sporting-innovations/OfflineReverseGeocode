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

package geocode.kdtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * @author Daniel Glasson
 * A KD-Tree implementation to quickly find nearest points
 * Currently implements createKDTree and findNearest as that's all that's required here
 */
@SuppressWarnings({"abbreviationaswordinname","PMD.UselessParentheses","PMD.CollapsibleIfStatements",
        "PMD.AvoidDeeplyNestedIfStmts"})
public class KDTree<T extends KDNodeComparator<T>> {
    public static int EARTH_RADIUS_IN_KM = 6371;

    private final KDNode<T> root;

    public KDTree( List<T> items ) {
        root = createKDTree(items, 0);
    }

    public T findNearest( T search ) {
        return findNearest(search, null);
    }

    /**
     * Finds the nearest 3D point within an optional maximum distance in kilometers.
     * @param search        The object containing the lat/long values that we're searching for
     * @param maxDistance   An optional maximum distance in kilometers between the two points
     * @return  The object that is nearest to our provided search object
     */
    public T findNearest(T search, Double maxDistance) {
        T nearest = findNearest(root, search, 0).location;
        if (null != maxDistance) {
            // distance = (squared distance between the nearest point and original point) * radius of the earth
            double distanceInKm = Math.sqrt(nearest.squaredDistance(search)) * EARTH_RADIUS_IN_KM;
            if (distanceInKm > maxDistance) {
                nearest = null;
            }
        }
        return nearest;
    }

    private KDNode<T> findNearest(KDNode<T> currentNode, T search, int depth) {
        int direction = search.getComparator(depth % 3).compare(search, currentNode.location);
        KDNode<T> next = (direction < 0) ? currentNode.left : currentNode.right;
        KDNode<T> other = (direction < 0) ? currentNode.right : currentNode.left;
        KDNode<T> best = (next == null) ? currentNode : findNearest(next, search, depth + 1); // Go to a leaf
        if (currentNode.location.squaredDistance(search) < best.location.squaredDistance(search)) {
            best = currentNode; // Set best as required
        }
        if (other != null) {
            if (currentNode.location.axisSquaredDistance(search, depth % 3) < best.location.squaredDistance(search)) {
                KDNode<T> possibleBest = findNearest(other, search, depth + 1);
                if (possibleBest.location.squaredDistance(search) < best.location.squaredDistance(search)) {
                    best = possibleBest;
                }
            }
        }
        return best; // Work back up
    }
        
    // Only ever goes to log2(items.length) depth so lack of tail recursion is a non-issue
    private KDNode<T> createKDTree(List<T> items, int depth) {
        if (items.isEmpty()) {
            return null;
        }
        Collections.sort(items, items.get(0).getComparator(depth % 3));
        int currentIndex = items.size() / 2;
        return new KDNode<T>(createKDTree(new ArrayList<T>(items.subList(0, currentIndex)), depth + 1),
                createKDTree(new ArrayList<T>(items.subList(currentIndex + 1, items.size())), depth + 1),
                items.get(currentIndex));
    }
}
