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

    public float roundToOneDecimalPlace(float value) {
        return (float) Math.round(value * 10) / 10;
    }

    public double roundToOneDecimalPlace(double value) {
        return (double) Math.round(value * 10) / 10;
    }
}
