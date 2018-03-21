package distance;

import distance.AbstractDistance;

public class EuclideanDistance extends AbstractDistance {

    @Override
    public double calculate(final double[] position1, final int pos1, final double[] position2, final int pos2, final int length) {
        double sum = 0;
        for (int i = 0; i < length; i++) {
            final double d = position1[i + pos1] - position2[i + pos1];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
}
