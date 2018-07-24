package fi.stardex.sisu.util.converters;

public class DataConverter {

    public int convertDataToInt(String value) {
        return Math.round(Float.parseFloat(value));
    }

    public double convertDataToDouble(String value) {
        return Double.parseDouble(value);
    }

    public float convertDataToFloat(String value) {
        return Float.parseFloat(value);
    }

    public float round(float value) {
        if (value > 1f)
            return roundToOneDecimalPlace(value);
        else
            return roundToTwoDecimalPlaces(value);
    }

    public double round(double value) {
        if (value > 1)
            return roundToOneDecimalPlace(value);
        else
            return roundToTwoDecimalPlaces(value);
    }

    private float roundToOneDecimalPlace(float value) {
        return (float) Math.round(value * 10) / 10;
    }

    private double roundToOneDecimalPlace(double value) {
        return (double) Math.round(value * 10) / 10;
    }

    private float roundToTwoDecimalPlaces(float value) {
        return (float) Math.round(value * 100) / 100;
    }

    private double roundToTwoDecimalPlaces(double value) {
        return (double) Math.round(value * 100) / 100;
    }
}
