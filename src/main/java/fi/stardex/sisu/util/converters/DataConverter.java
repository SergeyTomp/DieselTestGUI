package fi.stardex.sisu.util.converters;

public class DataConverter {

    public static int convertDataToInt(String value) {
        return Math.round(Float.parseFloat(value));
    }

    public static double convertDataToDouble(String value) {
        return Double.parseDouble(value);
    }

    public static float convertDataToFloat(String value) {
        return Float.parseFloat(value);
    }

    public static float round(float value) {
        if (value > 1f)
            return roundToOneDecimalPlace(value);
        else
            return roundToTwoDecimalPlaces(value);
    }

    public static double round(double value) {
        if (value > 1)
            return roundToOneDecimalPlace(value);
        else
            return roundToTwoDecimalPlaces(value);
    }

    private static float roundToOneDecimalPlace(float value) {
        return (float) Math.round(value * 10) / 10;
    }

    private static double roundToOneDecimalPlace(double value) {
        return (double) Math.round(value * 10) / 10;
    }

    private static float roundToTwoDecimalPlaces(float value) {
        return (float) Math.round(value * 100) / 100;
    }

    private static double roundToTwoDecimalPlaces(double value) {
        return (double) Math.round(value * 100) / 100;
    }
}
