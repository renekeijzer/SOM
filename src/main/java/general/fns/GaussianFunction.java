package general.fns;

public class GaussianFunction extends AbstractRBF {


    /**
     * Construct the Gaussian RBF. Each RBF will require space equal to (dimensions + 1) in the params vector.
     *
     * @param theDimensions The number of dimensions.
     * @param theParams     A vector to hold the parameters.
     * @param theIndex      The index into the params vector.  You can store multiple RBF's in a vector.
     */
    public GaussianFunction(final int theDimensions, final double[] theParams, final int theIndex) {
        super(theDimensions, theParams, theIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double evaluate(final double[] x) {
        double value = 0;
        final double width = Math.abs(getWidth());

        for (int i = 0; i < getDimensions(); i++) {
            final double center = this.getCenter(i);
            value += Math.pow(x[i] - center, 2) / (2.0 * width * width);
        }
        return Math.exp(-value);
    }
}