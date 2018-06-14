package fi.stardex.sisu.util.converters;

public class FirmwareDataConverter {

    public int convertDataToInt(String firmwareValue) {
        return Math.round(Float.parseFloat(firmwareValue));
    }

    public double convertDataToDouble(String firmwareValue) {
        return Double.parseDouble(firmwareValue);
    }

    public float convertDataToFloat(String firmwareValue) {
        return Float.parseFloat(firmwareValue);
    }

    public float roundToOneDecimalPlace(float value) {
        return (float) Math.round(value * 10) / 10;
    }
}
