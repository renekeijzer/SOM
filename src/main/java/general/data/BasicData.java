package general.data;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class BasicData {

    private final double[] input;
    private final double[] ideal;
    private String label;

    public double[] getInput() {
        return input;
    }

    public double[] getIdeal() {
        return ideal;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BasicData(final int inputDimensions) {
        this(inputDimensions, 0 , null);
    }

    public BasicData(final int inputDimension, final int idealDimension){
        this(inputDimension, idealDimension, null);
    }

    public BasicData(final int inputDimension, final int idealDimension, final String label) {
        this.input = new double[inputDimension];
        this.ideal = new double[idealDimension];
        this.label = label;
    }

    public BasicData(final double[] InputData, final double[] IdealData, final String Label) {
        this.label = Label;
        this.input = InputData;
        this.ideal = IdealData;
    }

    public BasicData(final double[] InputData, final String Label) {
        this(InputData, new double[0], Label);
    }

    public BasicData(final double[] InputData) {
        this(InputData, null);
    }

    public String toString() {
        String result = "[BasicData: input:" +
                Arrays.toString(this.input) +
                ", ideal:" +
                Arrays.toString(this.ideal) +
                ", label:" +
                this.label +
                "]";

        return result;
    }

    public static List<BasicData> convertArrays(final double[][] inputData, final double[][] idealData) {
        // create the list
        final List<BasicData> result = new ArrayList<>();

        // get the lengths
        final int inputCount = inputData[0].length;
        final int idealCount = idealData[0].length;

        // build the list
        for (int row = 0; row < inputData.length; row++) {
            final BasicData dataRow = new BasicData(inputCount, idealCount);
            System.arraycopy(inputData[row], 0, dataRow.getInput(), 0, inputCount);
            System.arraycopy(idealData[row], 0, dataRow.getIdeal(), 0, idealCount);
            result.add(dataRow);
        }

        return result;
    }

    public static List<BasicData> combineXY(double[][] theInput, double[][] theIdeal) {
        if (theInput.length != theIdeal.length) {
            throw new Error("The element count of the input and ideal element must match: "
                    + theInput.length + " != " + theIdeal.length);
        }

        List<BasicData> result = new ArrayList<>();
        for (int i = 0; i < theInput.length; i++) {
            result.add(new BasicData(theInput[i], theIdeal[i], null));
        }
        return result;
    }
}
