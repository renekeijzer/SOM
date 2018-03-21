package ml;

import java.util.List;

import Jama.Matrix;
import general.BestMatchUnit;
import general.VectorAlgebra;
import general.data.BasicData;
import ml.Neighborhood.NeighborhoodFunction;
import general.BestMatchUnit;

public class BasicTrainSom {
    private final NeighborhoodFunction neighborhood;
    private double learningRate;
    private final BasicSelfOrganizingMap network;
    private final int inputNeuronCount;
    private final int outputNeuronCount;
    private final BestMatchUnit bmuUtil;
    private final Matrix correctionMatrix;
    private boolean forceWinner;
    private double startRate;
    private double endRate;
    private double startRadius;
    private double endRadius;
    private double autoDecayRate;
    private double autoDecayRadius;
    private double radius;
    private final List<BasicData> training;
    private double error;

    public BasicTrainSom(final BasicSelfOrganizingMap network, final double learningRate, final List<BasicData> training, final NeighborhoodFunction neighborhood) {
        this.neighborhood = neighborhood;
        this.training = training;
        this.learningRate = learningRate;
        this.network = network;
        this.inputNeuronCount = network.getInputCount();
        this.outputNeuronCount = network.getOutputCount();
        this.forceWinner = false;
        this.error = 0;

        // setup the correction matrix
        this.correctionMatrix = new Matrix(this.outputNeuronCount,this.inputNeuronCount);

        // create the BMU class
        this.bmuUtil = new BestMatchUnit(network);
    }

    private void applyCorrection() {

        for(int row=0;row<this.correctionMatrix.getRowDimension();row++) {
            for(int col=0;col<this.correctionMatrix.getColumnDimension();col++) {
                this.network.getWeights().set(row,col,this.correctionMatrix.get(row,col));
            }
        }
    }

    public void autoDecay() {
        if (this.radius > this.endRadius) {
            this.radius += this.autoDecayRadius;
        }

        if (this.learningRate > this.endRate) {
            this.learningRate += this.autoDecayRate;
        }
        getNeighborhood().setRadius(this.radius);
    }

    private void copyInputPattern(final Matrix matrix, final int outputNeuron, final double[] input) {
        for (int inputNeuron = 0; inputNeuron < this.inputNeuronCount; inputNeuron++) {
            matrix.set(outputNeuron,inputNeuron, input[inputNeuron]);
        }
    }

    public void decay(final double d) {
        this.radius *= (1.0 - d);
        this.learningRate *= (1.0 - d);
    }

    public void decay(final double decayRate, final double decayRadius) {
        this.radius *= (1.0 - decayRadius);
        this.learningRate *= (1.0 - decayRate);
        getNeighborhood().setRadius(this.radius);
    }

    private double determineNewWeight(final double weight, final double input,
                                      final int currentNeuron, final int bmu) {

        final double newWeight = weight
                + (this.neighborhood.function(currentNeuron, bmu)
                * this.learningRate * (input - weight));
        return newWeight;
    }

    private boolean forceWinners(final Matrix matrix, final int[] won,
                                 final double[] leastRepresented) {

        double maxActivation = Double.MIN_VALUE;
        int maxActivationNeuron = -1;

        final double[] output = compute(this.network, leastRepresented);

         for (int outputNeuron = 0; outputNeuron < won.length; outputNeuron++) {
            if (won[outputNeuron] == 0) {
                if ((maxActivationNeuron == -1)
                        || (output[outputNeuron] > maxActivation)) {
                    maxActivation = output[outputNeuron];
                    maxActivationNeuron = outputNeuron;
                }
            }
        }

         if (maxActivationNeuron != -1) {
            copyInputPattern(matrix, maxActivationNeuron, leastRepresented);
            return true;
        } else {
            return false;
        }
    }

    public int getInputNeuronCount() {
        return this.inputNeuronCount;
    }

    public double getLearningRate() {
        return this.learningRate;
    }

    public NeighborhoodFunction getNeighborhood() {
        return this.neighborhood;
    }

    public int getOutputNeuronCount() {
        return this.outputNeuronCount;
    }

    public boolean isForceWinner() {
        return this.forceWinner;
    }

    public void iteration() {
        this.bmuUtil.reset();
        final int[] won = new int[this.outputNeuronCount];
        double leastRepresentedActivation = Double.MAX_VALUE;
        double[] leastRepresented = null;

        for (final BasicData input : this.training) {
            final int bmu = this.bmuUtil.calculateBMU(input.getInput());
            won[bmu]++;

            if (this.forceWinner) {
                 final double[] output = compute(this.network,input.getInput());

                if (output[bmu] < leastRepresentedActivation) {
                    leastRepresentedActivation = output[bmu];
                    leastRepresented = input.getInput();
                }
            }

            train(bmu, this.network.getWeights(), input.getInput());

            if (this.forceWinner) {
                if (!forceWinners(this.network.getWeights(), won,
                        leastRepresented)) {
                    applyCorrection();
                }
            } else {
                applyCorrection();
            }
        }

        this.error = this.bmuUtil.getWorstDistance() / 100.0;
    }

    public void setAutoDecay(final int plannedIterations,
                             final double startRate, final double endRate,
                             final double startRadius, final double endRadius) {
        this.startRate = startRate;
        this.endRate = endRate;
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        this.autoDecayRadius = (endRadius - startRadius) / plannedIterations;
        this.autoDecayRate = (endRate - startRate) / plannedIterations;
        setParams(this.startRate, this.startRadius);
    }

    public void setForceWinner(final boolean forceWinner) {
        this.forceWinner = forceWinner;
    }

    public void setLearningRate(final double rate) {
        this.learningRate = rate;
    }
    public void setParams(final double rate, final double radius) {
        this.radius = radius;
        this.learningRate = rate;
        getNeighborhood().setRadius(radius);
    }

    @Override
    public String toString() {
        String result = "Rate=" +
                this.learningRate +
                ", Radius=" +
                this.radius;
        return result;
    }

    private void train(final int bmu, final Matrix matrix, final double[] input) {
        // adjust the weight for the BMU and its neighborhood
        for (int outputNeuron = 0; outputNeuron < this.outputNeuronCount; outputNeuron++) {
            trainPattern(matrix, input, outputNeuron, bmu);
        }
    }

    private void trainPattern(final Matrix matrix, final double[] input,
                              final int current, final int bmu) {

        for (int inputNeuron = 0; inputNeuron < this.inputNeuronCount; inputNeuron++) {

            final double currentWeight = matrix.get(current,inputNeuron);
            final double inputValue = input[inputNeuron];

            final double newWeight = determineNewWeight(currentWeight,
                    inputValue, current, bmu);

            this.correctionMatrix.set(current,inputNeuron,newWeight);
        }
    }

    public void trainPattern(final double[] pattern) {
        final int bmu = this.bmuUtil.calculateBMU(pattern);
        train(bmu, this.network.getWeights(), pattern);
        applyCorrection();
    }

    private double[] compute(final BasicSelfOrganizingMap som, final double[] input) {

        final double[] result = new double[som.getOutputCount()];

        for (int i = 0; i < som.getOutputCount(); i++) {
            final double[] optr = som.getWeights().getArray()[i];

            final Matrix matrixA = new Matrix(input.length,1);
            for(int j=0;j<input.length;j++) {
                matrixA.getArray()[0][j] = input[j];
            }


            final Matrix matrixB = new Matrix(1,input.length);
            for(int j=0;j<optr.length;j++) {
                matrixB.getArray()[0][j] = optr[j];
            }

            result[i] = VectorAlgebra.dotProduct(matrixA, matrixB);
        }

        return result;
    }
}
