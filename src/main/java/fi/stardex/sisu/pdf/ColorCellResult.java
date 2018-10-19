package fi.stardex.sisu.pdf;

import fi.stardex.sisu.ui.data.TestResult;

import java.awt.*;
import java.util.Objects;

public class ColorCellResult {

    private ColorCellResult(){
        //do not instantiate
    }

    public static Color getColorCellOfResult(double beakerValue, TestResult result){

        double nominalRight = result.getNominalRight();
        double nominalLeft = result.getNominalLeft();
        double nominalExceptRight = result.getNominalExceptRight();
        double nominalExceptLeft = result.getNominalExceptLeft();

        /*
         * Change color if data out of borders : 5% - in borders - yellow, out of borders - red
         */
        Color cellColor = Color.WHITE;

        if(Double.compare(beakerValue, nominalRight) == 0 ||
                beakerValue > nominalRight ||
                Double.compare(beakerValue, nominalLeft) == 0 ||
                beakerValue < nominalLeft){
            cellColor = Color.ORANGE;
        }

        if(Double.compare(beakerValue, nominalExceptRight) == 0 ||
                beakerValue > nominalExceptRight ||
                Double.compare(beakerValue, nominalExceptLeft) == 0 ||
                beakerValue < nominalExceptLeft) {
            cellColor = Color.RED;
        }

        if(beakerValue < 0){
            cellColor = Color.WHITE;
        }

        return evalueteForPump(cellColor, beakerValue, result);
    }

    private static Color evalueteForPump(Color color, double beakerValue, TestResult result){
        if((Objects.equals(result.getTestName(), "Efficiency Test1")
                || Objects.equals(result.getTestName(), "Efficiency Test2"))
                && Objects.equals(result.getDeliveryRecovery(), "Back Flow")){
            if(beakerValue < result.getNominalValue()) {            // TODO: 06.08.16 ask whether to make or not red for pump
                return Color.RED;
            } else {
                return Color.WHITE;
            }
        }
        return color;
    }
}
