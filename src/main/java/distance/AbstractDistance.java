package distance;

import distance.CalculateDistance;

public abstract class AbstractDistance implements CalculateDistance {

    @Override
    public double calculate(final double[] position1, final double[] position2) {
        return calculate(position1, 0, position2, 0, position1.length);
    }
}