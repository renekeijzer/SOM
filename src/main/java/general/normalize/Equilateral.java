package general.normalize;

import java.io.Serializable;

public class Equilateral implements Serializable {


    public static final int MIN_EQ = 3;


    private final double[][] matrix;


    public Equilateral(final int count, final double low, final double high) {
        if (count < MIN_EQ) {
            throw new Error("Must have at least three classes.");
        }
        this.matrix = equilat(count, low, high);
    }


    public final int decode(final double[] activations) {
        double minValue = Double.POSITIVE_INFINITY;
        int minSet = -1;

        for (int i = 0; i < this.matrix.length; i++) {
            final double dist = getDistance(activations, i);
            if (dist < minValue) {
                minValue = dist;
                minSet = i;
            }
        }
        return minSet;
    }


    public final double[] encode(final int set) {
        if (set < 0 || set > this.matrix.length) {
            throw new Error("Class out of range for equilateral: " + set);
        }
        return this.matrix[set];
    }


    private double[][] equilat(final int n,
                               final double low, final double high) {
        double r, f;
        final double[][] result = new double[n][n - 1];

        result[0][0] = -1;
        result[1][0] = 1.0;

        for (int k = 2; k < n; k++) {
            // scale the matrix so far
            r = k;
            f = Math.sqrt(r * r - 1.0) / r;
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k - 1; j++) {
                    result[i][j] *= f;
                }
            }

            r = -1.0 / r;
            for (int i = 0; i < k; i++) {
                result[i][k - 1] = r;
            }

            for (int i = 0; i < k - 1; i++) {
                result[k][i] = 0.0;
            }
            result[k][k - 1] = 1.0;
        }


        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[0].length; col++) {
                final double min = -1;
                final double max = 1;
                result[row][col] = ((result[row][col] - min) / (max - min))
                        * (high - low) + low;
            }
        }

        return result;
    }


    public final double getDistance(final double[] data, final int set) {
        double result = 0;
        for (int i = 0; i < data.length; i++) {
            result += Math.pow(data[i] - this.matrix[set][i], 2);
        }
        return Math.sqrt(result);
    }
}

