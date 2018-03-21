package ml;

import Jama.Matrix;
import distance.CalculateDistance;
import distance.EuclideanDistance;
import general.BestMatchUnit;
import general.random.GenerateRandom;
import general.random.MersenneTwisterGenerateRandom;

public class BasicSelfOrganizingMap {

    private Matrix weights;

    private final CalculateDistance calcDist = new EuclideanDistance();

    public BasicSelfOrganizingMap(final int inputCount, final int outputCount){
        this.weights = new Matrix(outputCount, inputCount);
    }

    public double calculateError(final double [][] data)  {
        final BestMatchUnit bmu = new BestMatchUnit(this);
        bmu.reset();

        for(final double[] pair : data ){
            final double[] input = pair;
            bmu.calculateBMU(input);
        }

        return bmu.getWorstDistance() / 100.0;
    }

    public int classify(final double[] input) {
        if (input.length > getInputCount()) {
            throw new Error("Can't classify SOM with input size of " + getInputCount()
                    + " with input data of count " + input.length);
        }

        double minDist = Double.POSITIVE_INFINITY;
        int result = -1;

        for (int i = 0; i < getOutputCount(); i++) {
            double dist = this.calcDist.calculate(input, this.weights.getArray()[i]);
            if(dist < minDist) {
                minDist = dist;
                result = i;
            }
        }

        return result;
    }

    public int getInputCount() {
        return this.weights.getColumnDimension();
    }

    public int getOutputCount() {
        return this.weights.getRowDimension();
    }

    public Matrix getWeights() {
        return this.weights;
    }

    public void reset(GenerateRandom rnd) {
        for(int i = 0; i < this.getOutputCount(); i++) {
            for( int j = 0; j < this.getInputCount(); j++){
                this.weights.set(i, j, rnd.nextDouble(-1,1));
            }
        }
    }

    public void reset() {
        reset(new MersenneTwisterGenerateRandom());
    }

    public void setWeights(final Matrix weights) {
        this.weights = weights;
    }

    public int winner(final double[] input) {
        return classify(input);
    }
}
