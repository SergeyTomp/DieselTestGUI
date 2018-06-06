package fi.stardex.sisu.util.filters;

import java.util.Arrays;

public class FilterInputChartData {

    public static double[] medianFilter(double[] inputData, int stepSize) {

        double[] addedInputData = new double[inputData.length + stepSize];
        double[] outputData = new double[inputData.length];
        double[] stepData = new double[stepSize];
        int step;

        System.arraycopy(inputData, 0, addedInputData, 0, inputData.length);

        if (inputData.length == 0) {
            return outputData;
        }
        for (int i = inputData.length; i < addedInputData.length; i++) {
            addedInputData[i] = inputData[inputData.length - 1];
        }

        for (int i = 0; i < addedInputData.length - stepSize; i++) {
            step = 0;
            for (int j = i; j < i + stepSize; j++) {
                stepData[step] = addedInputData[j];
                step++;
            }
            Arrays.sort(stepData);
            outputData[i] = stepData[stepSize / 2];
        }
        return outputData;
    }

}
