# ConvexHullDivideConquer

public static List<int[]> convexHullDivideConquer(int[][] points)\
This static method is used to find the convex hull of a set of points.

## Algorithm Description
1. Sort points based on their x-coordinates (and if x-coordinates are equal, compare y-coordinates);
2. Recursively find the left convex hulll
3. Recursively find the right convex hull;
4. Merge the left and right convex hulls.

## Implementation Details
1. The base case of the recursion is points.length <= 3. And when 3 points is collinear, the middle point is excluded. The order is also reversed to make the indexes increase counterclock for the implementation of merging the left and right convex hulls;
2. When merge the left and right convex hulls, start with the rightmost point of the left convex hull and the leftmost point of the right hull. Keep updating the index of the vertices of the right convex hull clockwise such that left[leftIndex], right[rightIndex], right[(rightIndex - 1 + rightSize) % rightSize] clockwise. And keep updating the index of the vertices of the left convex hull counterclockwise such that right[rightIndex], left[leftIndex], left[(leftIndex + 1) % leftSize] counterclockwise. To determine the orientation of a series of 3 points, cross product of [x_1  - x_0, y_1  - y_0] and [x_2  - x_1, y_2  - y_1] is calculated;
3. A corner case is that when merging the left and right hulls, the cross product is always equal to 0 because of collinearity. Therefore, a variable iterationCount is used to exit dead loop and find the tangent correctly.
