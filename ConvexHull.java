package edu.gwu.convexHull;
import java.util.*;


/**
 * @author Yiming Xu
 */
public class ConvexHull {
    /**
     * This is a function for determining the orientation of 3 points [point0, point1, point2]
     * @param point0 This is the first point
     * @param point1 This is the second point
     * @param point2 This is the third point
     * @return 1 for counterclockwise
     *        -1 for clockwise
     *         0 for collinear
     */
    private static int orientation(int[] point0, int[] point1, int[] point2){
        int x0 = point0[0];
        int y0 = point0[1];
        int x1 = point1[0];
        int y1 = point1[1];
        int x2 = point2[0];
        int y2 = point2[1];
        int crossProduct = (x1 - x0) * (y2 - y1) - (x2 - x1) * (y1 - y0);
        if(crossProduct > 0)
            return 1;
        if(crossProduct < 0)
            return -1;
        return 0;
    }


    /**
     * This is a function for merging the left convex hull and the right convex hull
     * @param leftConvexHull 
     * @param rightConvexHull
     * @return a merged convex hull of the left convex hull and the right convex hull
     */
    private static List<int[]> mergeConvexHull(List<int[]> leftConvexHull, List<int[]> rightConvexHull){
        // merge left convex hull and right convex hull
        int leftConvexHullSize = leftConvexHull.size();
        int rightConvexHullSize = rightConvexHull.size();
        // find the most right point's index of the left convex hull
        // find the most left point's index of the right convex hull
        int leftConvexHullMostRightIndex = 0;
        int rightConvexHullMostLeftIndex = 0;
        for(int i = 1; i < leftConvexHullSize; i++){
            if(leftConvexHull.get(i)[0] > leftConvexHull.get(leftConvexHullMostRightIndex)[0])
                leftConvexHullMostRightIndex = i;
        }
        for(int i = 1; i < rightConvexHullSize; i++){
            if(rightConvexHull.get(i)[0] < rightConvexHull.get(rightConvexHullMostLeftIndex)[0])
                rightConvexHullMostLeftIndex = i;
        }
        boolean isTangentFound;
        // iterationCount is set for corner case of collinearity
        int iterationCount;
        // upper Tangent
        isTangentFound = false;
        int upperTangentLeftIndex = leftConvexHullMostRightIndex;
        int upperTangentRightIndex = rightConvexHullMostLeftIndex;
        while(!isTangentFound){
            isTangentFound = true;
            // the index of point of upper tangent on the right hull
            iterationCount = 0;
            while(orientation(leftConvexHull.get(upperTangentLeftIndex), rightConvexHull.get(upperTangentRightIndex), rightConvexHull.get((upperTangentRightIndex - 1 + rightConvexHullSize) % rightConvexHullSize)) >= 0){
                iterationCount++;
                if(iterationCount == rightConvexHullSize){
                    upperTangentRightIndex = 0;
                    break;
                }
                upperTangentRightIndex = (rightConvexHullSize - 1 + rightConvexHullSize) % rightConvexHullSize;
            }
            // the index of point of upper tangent on the left hull
            iterationCount = 0;
            while(orientation(rightConvexHull.get(upperTangentRightIndex), leftConvexHull.get(upperTangentLeftIndex), leftConvexHull.get((upperTangentLeftIndex + 1) % leftConvexHullSize)) <= 0){
                iterationCount++;
                if(iterationCount == leftConvexHullSize) {
                    upperTangentLeftIndex = leftConvexHullSize - 1;
                    isTangentFound = true;
                    break;
                }
                upperTangentLeftIndex = (upperTangentLeftIndex + 1) % leftConvexHullSize;
                isTangentFound = false;
            }
        }
        // lower tangent
        isTangentFound = false;
        int lowerTangentLeftIndex = leftConvexHullMostRightIndex;
        int lowerTangentRightIndex = rightConvexHullMostLeftIndex;
        while(!isTangentFound){
            isTangentFound = true;
            // the index of point of lower tangent on the right hull
            iterationCount = 0;
            while(orientation(leftConvexHull.get(lowerTangentLeftIndex), rightConvexHull.get(lowerTangentRightIndex), rightConvexHull.get((lowerTangentRightIndex + 1) % rightConvexHullSize)) <= 0){
                iterationCount++;
                if(iterationCount == rightConvexHullSize){
                    lowerTangentRightIndex = 0;
                    break;
                }
                lowerTangentRightIndex = (lowerTangentRightIndex + 1) % rightConvexHullSize;
            }
            // the index of point of lower tangent on the right hull
            iterationCount = 0;
            while(orientation(rightConvexHull.get(lowerTangentRightIndex), leftConvexHull.get(lowerTangentLeftIndex), leftConvexHull.get((lowerTangentLeftIndex - 1 + leftConvexHullSize) % leftConvexHullSize)) >= 0){
                iterationCount++;
                if(iterationCount == leftConvexHullSize){
                    lowerTangentLeftIndex = leftConvexHullSize - 1;
                    isTangentFound = true;
                    break;
                }
                lowerTangentLeftIndex = (lowerTangentLeftIndex - 1 + leftConvexHullSize) % leftConvexHullSize;
                isTangentFound = false;
            }
        }
        // remove the right part of the left convex hull
        int removalCount = 0;
        for(int i = (lowerTangentLeftIndex + 1) % leftConvexHullSize; i != upperTangentLeftIndex; i = (i + 1) % leftConvexHullSize){
            if(i == 0)
                removalCount = 0;
            leftConvexHull.remove(i - removalCount);
            removalCount++;
        }
        // add the right part of the right convex hull
        leftConvexHull.add(rightConvexHull.get(lowerTangentRightIndex));
        for(int i = (lowerTangentRightIndex + 1) % rightConvexHullSize; i != (upperTangentRightIndex + 1) % rightConvexHullSize; i = (i + 1) % rightConvexHullSize){
            leftConvexHull.add(rightConvexHull.get(i));
        }
        return leftConvexHull;
    }


    /**
     * This is a function for sorting the vertices of a convex hull
     * according to their relative positions to the centroid of the convex hull
     * @param convexHullVertices This is the List of vertices of convex hull
     */
    private static void sortConvexHullVertices(List<int[]> convexHullVertices){
        // Sorting Based on the Relative Positions of Convex Hull Vertices to the Centroid
        double[] centroid = {0, 0};
        int convexHullVerticesSize = convexHullVertices.size();
        for(int i = 0; i < convexHullVerticesSize; i++){
            centroid[0] += convexHullVertices.get(i)[0];
            centroid[1] += convexHullVertices.get(i)[1];
        }
        centroid[0] /= convexHullVerticesSize;
        centroid[1] /= convexHullVerticesSize;
        convexHullVertices.sort(new counterclockwiseComparator(centroid));
    }


    /**
     *  This is a function for finding the convex hull recursively after sorting
     * @param points Sorted points
     * @return The convex hull
     */
    private static List<int[]> convexHullDivideConquerRecursion(int[][] points) {
        if(points.length <= 1)
            return new ArrayList<>(Arrays.asList(points));
        // convex hull finding using D&C
        if (points.length == 2)
            // points are reversed because of the implementation of merging
            return new ArrayList<>(Arrays.asList(points[1], points[0]));
        if (points.length == 3){
            // if not collinear
            if((points[0][0] != points[1][0] || points[0][0] != points[2][0]) || (points[0][1] != points[1][1] || points[0][1] != points[2][1]))
                // points are reversed because of the implementation of merging
                return new ArrayList<>(Arrays.asList(points[2], points[1], points[0]));
            // points are reversed because of the implementation of merging
            return new ArrayList<>(Arrays.asList(points[2], points[0]));
        }
        int[][] pointsLeftPart = Arrays.copyOfRange(points, 0, points.length/2);
        int[][] pointsRightPart = Arrays.copyOfRange(points, points.length/2, points.length);
        List<int[]> leftConvexHull = convexHullDivideConquerRecursion(pointsLeftPart);
        List<int[]> rightConvexHull = convexHullDivideConquerRecursion(pointsRightPart);
        return mergeConvexHull(leftConvexHull, rightConvexHull);
    }


    /**
     * This is the public static method for finding the convex hull
     * @param points
     * @return The convex hull
     */
    public static List<int[]> convexHullDivideConquer(int[][] points) {
        // convex hull finding using D&C
        if (points.length <= 3) {
            return new ArrayList<>(Arrays.asList(points));
        }
        // sort according to x coordinates if x coordinates are different
        // sort according to y coordinates if x coordinates are the same
        Arrays.sort(points, (p1, p2) -> p1[0] != p2[0] ? p1[0] - p2[0] : p1[1] - p2[1]);
        List<int[]> convexHull = convexHullDivideConquerRecursion(points);
        sortConvexHullVertices(convexHull);
        return convexHull;
    }


    /**
     * This is a function for generating random 2-dimensional points
     * @param size the number of points generated
     * @param range the range of points generated
     *              abs(x) < range && abs(y) < range
     * @return generated random points
     */
    private static int[][] randomPoints(int size, int range){
        if(range <= 0)
            return null;
        int[][] points = new int[size][2];
        Random random = new Random();
        for(int i = 0; i < size; i++){
            points[i][0] = random.nextInt(2 * range) - range;
            points[i][1] = random.nextInt(2 * range) - range;
        }
        return points;
    }


    public static void main(String[] args) {
        long startTime;
        long endTime;
        int[][] points;
        List<int[]> hull;

        for(int size = 10; size <= 29000010; size += 1000000){
            points = randomPoints(size,100);
            startTime = System.nanoTime();
            hull = convexHullDivideConquer(points);
            endTime = System.nanoTime();
            System.out.println("Running Time when n = " + size + ": " + (endTime - startTime) + "ns" );
        }
    }
}


/**
 * This is comparator for sorting the vertices of the convex hull
 */
class counterclockwiseComparator implements Comparator<int[]>{
    // Comparator for Sorting Based on the Relative Positions of Convex Hull Vertices to the Centroid
    double[] centroid;
    public counterclockwiseComparator(double[] centroid){
        this.centroid = centroid;
    }
    private int quadrant(double[] point){
        // the first quadrant
        if(point[0] >= 0 && point[1] >= 0)
            return 1;
        // the second quadrant
        if(point[0] <= 0 && point[1] >= 0)
            return 2;
        // the third quadrant
        if(point[0] <= 0)
            return 3;
        // the fourth quadrant
        return 4;
    }

    public int compare(int[] point0, int[] point1){
        double[] centroidRelative0 = {point0[0] - centroid[0], point0[1] - centroid[1]};
        double[] centroidRelative1 = {point1[0] - centroid[0], point1[1] - centroid[1]};
        int quadrant0 = quadrant(centroidRelative0);
        int quadrant1 = quadrant(centroidRelative1);
        if(quadrant0 != quadrant1){
            // according to quadrants if not the same
            return quadrant0 - quadrant1;
        }
        // according to orientation(cross product) if quadrants are different
        return (int)(centroidRelative1[0] * centroidRelative0[1] - centroidRelative0[0] * centroidRelative1[1]);
    }
}
