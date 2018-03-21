package general.fns;

public class MexicanHatFunction extends AbstractRBF {

    public MexicanHatFunction(final int theDimensions, final double[] theParams, final int theIndex) {
        super(theDimensions, theParams, theIndex);
    }


    @Override
    public double evaluate(final double[] x) {
        // calculate the "norm", but don't take square root
        // don't square because we are just going to square it
        double norm = 0;
        for (int i = 0; i < getDimensions(); i++) {
            final double center = this.getCenter(i);
            norm += Math.pow(x[i] - center, 2);
        }

        // calculate the value
        norm /= getWidth() * getWidth();

        return (1 - norm) * Math.exp(-norm / 2);

    }
}