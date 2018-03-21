package general.fns;

import java.text.NumberFormat;

public abstract class AbstractRBF implements FnRBF {

    /**
     * The parameter vector.  Holds the RBF width and centers.  This vector may hold multiple RBF's.
     */
    private final double[] params;

    /**
     * The index to the widths.
     */
    private final int indexWidth;

    /**
     * The index to the centers.
     */
    private final int indexCenters;

    /**
     * The dimensions.
     */
    private final int dimensions;

    /**
     * Construct the RBF. Each RBF will require space equal to (dimensions + 1) in the params vector.
     *
     * @param theDimensions The number of dimensions.
     * @param theParams     A vector to hold the paramaters.
     * @param theIndex      The index into the params vector.  You can store multiple RBF's in a vector.
     */
    public AbstractRBF(final int theDimensions, final double[] theParams, final int theIndex) {
        this.dimensions = theDimensions;
        this.params = theParams;
        this.indexWidth = theIndex;
        this.indexCenters = theIndex + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double getCenter(final int dimension) {
        return this.params[this.indexCenters + dimension];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getDimensions() {
        return this.dimensions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double getWidth() {
        return this.params[this.indexWidth];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setWidth(final double theWidth) {
        this.params[this.indexWidth] = theWidth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final NumberFormat f = NumberFormat.getNumberInstance();
        f.setMinimumFractionDigits(2);

        final StringBuilder result = new StringBuilder();
        result.append("[");
        result.append(this.getClass().getSimpleName());
        result.append(":width=");
        result.append(f.format(this.getWidth()));
        result.append(",center=");
        for (int i = 0; i < this.dimensions; i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append(f.format(this.params[this.indexCenters + i]));

        }

        result.append("]");
        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCenter(final int dimension, final double value) {
        this.params[this.indexCenters + dimension] = value;
    }
}