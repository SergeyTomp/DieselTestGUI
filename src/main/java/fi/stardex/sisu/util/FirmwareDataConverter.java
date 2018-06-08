package fi.stardex.sisu.util;

public class FirmwareDataConverter {

    public int convertDataToInt(String firmwareValue) {
        return Math.round(Float.parseFloat(firmwareValue));
    }

    public double convertDataToDouble(String firmwareValue) {
        return Double.parseDouble(firmwareValue);
    }

}
