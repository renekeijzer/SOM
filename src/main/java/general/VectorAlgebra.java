package general;

import Jama.Matrix;
import general.random.GenerateRandom;

public class VectorAlgebra {
    public static void add(double[] v1, double[] v2) {
        for (int i = 0; i < v1.length; i++) {
            v1[i] += v2[i];
        }
    }

    public static void sub(double[] v1, double[] v2) {
        for (int i = 0; i < v1.length; i++) {
            v1[i] -= v2[i];
        }
    }

    public static void neg(double[] v) {
        for (int i = 0; i < v.length; i++) {
            v[i] = -v[i];
        }
    }

    public static void mulRand(GenerateRandom rnd, double[] v, double k) {
        for (int i = 0; i < v.length; i++) {
            v[i] *= k * rnd.nextDouble();
        }
    }

    public static void mul(double[] v, double k) {
        for (int i = 0; i < v.length; i++) {
            v[i] *= k;
        }
    }

    public static void copy(double[] dst, double[] src) {
        System.arraycopy(src, 0, dst, 0, src.length);
    }

    public static void randomise(GenerateRandom rnd, double[] v) {
        randomise(rnd, v, 0.1);
    }

    public static void randomise(GenerateRandom rnd, double[] v, double maxValue) {
        for (int i = 0; i < v.length; i++) {
            v[i] = (2 * rnd.nextDouble() - 1) * maxValue;
        }
    }

    public static void clampComponents(double[] v, double maxValue) {
        if (maxValue != -1) {
            for (int i = 0; i < v.length; i++) {
                if (v[i] > maxValue) v[i] = maxValue;
                if (v[i] < -maxValue) v[i] = -maxValue;
            }
        }
    }

    public static double dotProduct(double[] v1, double[] v2) {
        double d = 0;
        for (int i = 0; i < v1.length; i++) {
            d += v1[i] * v2[i];
        }
        return Math.sqrt(d);
    }

    public static boolean isVector(Matrix matrix) {
        return(matrix.getRowDimension()==1 || matrix.getColumnDimension()==1);
    }

    public static double dotProduct(final Matrix a, final Matrix b) {
        if (!isVector(a) || !isVector(b)) {
            throw new Error("To take the dot product, both matrices must be vectors.");
        }

        final double[][] aArray = a.getArray();
        final double[][] bArray = b.getArray();

        final int aLength = aArray.length == 1 ? aArray[0].length : aArray.length;
        final int bLength = bArray.length == 1 ? bArray[0].length : bArray.length;

        if (aLength != bLength) {
            throw new Error("To take the dot product, both matrices must be of the same length.");
        }

        double result = 0;
        if (aArray.length == 1 && bArray.length == 1) {
            for (int i = 0; i < aLength; i++) {
                result += aArray[0][i] * bArray[0][i];
            }
        }
        else if (aArray.length == 1 && bArray[0].length == 1) {
            for (int i = 0; i < aLength; i++) {
                result += aArray[0][i] * bArray[i][0];
            }
        }
        else if (aArray[0].length == 1 && bArray.length == 1) {
            for (int i = 0; i < aLength; i++) {
                result += aArray[i][0] * bArray[0][i];
            }
        }
        else if (aArray[0].length == 1 && bArray[0].length == 1) {
            for (int i = 0; i < aLength; i++) {
                result += aArray[i][0] * bArray[i][0];
            }
        }

        return result;
    }

    public static Matrix createRowMatrix(double[] d) {
        Matrix result = new Matrix(1,d.length);
        for(int i=0;i<d.length;i++) {
            result.set(0,i,d[i]);
        }
        return result;
    }

    public static Matrix createColumnMatrix(double[] d) {
        Matrix result = new Matrix(d.length,1);
        for(int i=0;i<d.length;i++) {
            result.set(i,0,d[i]);
        }
        return result;
    }

    public static Matrix identityMatrix(final int size) {
        Matrix result = new Matrix(size,size);
        for(int i=0;i<size;i++) {
            result.set(i,i,1.0);
        }

        return result;
    }


}
