package general.fns;

public class InverseMultiquadricFunction extends AbstractRBF {


    public InverseMultiquadricFunction(final int theDimensions, final double[] theParams, final int theIndex) {
        super(theDimensions, theParams, theIndex);
    }

    @Override
    public double evaluate(final double[] x) {
        double value = 0;

        final double width = getWidth();

        for (int i = 0; i < getDimensions(); i++) {
            final double center = getCenter(i);
            value += Math.pow(x[i] - center, 2) + (width * width);
        }
        return 1 / Math.sqrt(value);

    }
}