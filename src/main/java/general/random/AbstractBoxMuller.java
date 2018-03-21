package general.random;

public abstract class AbstractBoxMuller extends AbstractGenerateRandom {

    private double y2;

    private boolean useLast;

    /**
     * mean
     */
    public static final double MU = 0;

    /**
     * standard deviation.
     */
    private static final double SIGMA = 1;


    @Override
    public double nextGaussian() {
        double x1;
        double x2;
        double w;
        final double y1;

        // use value from previous call
        if (this.useLast) {
            y1 = this.y2;
            this.useLast = false;
        } else {
            do {
                x1 = 2.0 * nextDouble() - 1.0;
                x2 = 2.0 * nextDouble() - 1.0;
                w = x1 * x1 + x2 * x2;
            } while (w >= 1.0);

            w = Math.sqrt((-2.0 * Math.log(w)) / w);
            y1 = x1 * w;
            this.y2 = x2 * w;
            this.useLast = true;
        }

        return (AbstractBoxMuller.MU + y1 * AbstractBoxMuller.SIGMA);
    }
}
