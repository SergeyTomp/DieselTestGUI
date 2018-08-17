package fi.stardex.sisu.util.filters;

import java.util.Arrays;

public class FilterInputChartData {

    private static final int STEP_SIZE = 10;

    public static double[] medianFilter(double[] inputData) {

        double[] addedInputData = new double[inputData.length + STEP_SIZE];
        double[] outputData = new double[inputData.length];
        double[] stepData = new double[STEP_SIZE];
        int step;

        System.arraycopy(inputData, 0, addedInputData, 0, inputData.length);

        if (inputData.length == 0) {
            return outputData;
        }
        for (int i = inputData.length; i < addedInputData.length; i++) {
            addedInputData[i] = inputData[inputData.length - 1];
        }

        for (int i = 0; i < addedInputData.length - STEP_SIZE; i++) {
            step = 0;
            for (int j = i; j < i + STEP_SIZE; j++) {
                stepData[step] = addedInputData[j];
                step++;
            }
            Arrays.sort(stepData);
            outputData[i] = stepData[STEP_SIZE / 2];
        }
        return outputData;
    }

}
