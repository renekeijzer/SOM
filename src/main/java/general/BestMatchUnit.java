package general;

import Jama.Matrix;
import ml.BasicSelfOrganizingMap;

public class BestMatchUnit {
    private final BasicSelfOrganizingMap som;

    private double worstDistance;

    public BestMatchUnit(final BasicSelfOrganizingMap som){
        this.som = som;
    }

    public int calculateBMU(final double[] input) {
        int result = 0;

        if(input.length > this.som.getInputCount()){
            throw new Error("Can't train SOM with input size of " + this.som.getInputCount()
                    + " with input data of count "
                    + input.length);
        }

        double lowestDistance = Double.MAX_VALUE;

        for(int i = 0; i < this.som.getOutputCount(); i++){
            final double distance = calculateEuclideanDistance(this.som.getWeights(), input, i);
            if(distance < lowestDistance) {
                result = i;
            }
        }

        if (lowestDistance > this.worstDistance) {
            this.worstDistance = lowestDistance;
        }

        return result;
    }


    public double calculateEuclideanDistance(final Matrix matrix, final double[] input, final int outputNeuron) {
        double result = 0;

        for(int i = 0; i < input.length; i++){
            final double diff = input[i] - matrix.get(outputNeuron, i);
            result += diff * diff;
        }
        return Math.sqrt(result);
    }

    public double getWorstDistance() {
        return this.worstDistance;
    }

    public void reset() {
        this.worstDistance = Double.MIN_VALUE;
    }
}
