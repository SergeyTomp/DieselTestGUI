package fi.stardex.sisu.ui.wrappers;

import javafx.scene.control.TextField;

public class Rescaler {

//    private double DEFAULT_MAX_FUEL_LEVEL = 0.5;
//
//    private BeanList<TextField> textFields;
//    private double beakerHeight;
//    private double standardFuel;
//    private double standardVisualFuel;
//
//    public Rescaler(BeanList<TextField> textFields) {
//        this.textFields = textFields;
//    }
//
//    public Rescaler(TextField textField) {
//        this(new BeanList<>(textField));
//    }
//
//    public void setBeakerHeight(double beakerHeight) {
//        this.beakerHeight = beakerHeight;
//    }
//
//    public double getFxLevel(TextField textField) {
//        double value = textField.get();
//        if (value <= 0) {
//            return 0;
//        }
//        recalculateStandardLevel();
//        return value * standardVisualFuel / standardFuel;
//    }
//
//    private void recalculateStandardLevel() {
//        double value = textFields.max().get();
//        double newVisualValue = value * standardVisualFuel / standardFuel;
//        if (Double.valueOf(standardVisualFuel).compareTo(0d) == 0
//                || newVisualValue > standardVisualFuel + (beakerHeight - standardVisualFuel) * 0.75
//                || newVisualValue < standardVisualFuel - (beakerHeight - standardVisualFuel) * 0.25) {
//            standardFuel = value;
//            standardVisualFuel = beakerHeight * DEFAULT_MAX_FUEL_LEVEL;
//        }
//
//    }
}
