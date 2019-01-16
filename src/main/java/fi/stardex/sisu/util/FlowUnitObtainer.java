package fi.stardex.sisu.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;

public class FlowUnitObtainer {

    public static final String MILLILITRE_PER_MINUTE = "ml/min";

    public static final String LITRE_PER_HOUR = "l/h";

//    public static final String MILLILITRE_PER_100RPM = "ml/100str";
//
//    public static final String MILLILITRE_PER_200RPM = "ml/200str";
//
//    public static final String MILLILITRE_PER_1000RPM = "ml/1000str";

    private static double deliveryCoefficient;

    private static double backFlowCoefficient;

    private static Double pumpDeliveryCoefficient;

    private static Double pumpBackFlowCoefficient;

    public static double getDeliveryCoefficient() {
        return deliveryCoefficient;
    }

    public static double getBackFlowCoefficient() {
        return backFlowCoefficient;
    }

    public static Double getPumpDeliveryCoefficient() {
        return pumpDeliveryCoefficient;
    }

    public static Double getPumpBackFlowCoefficient() {
        return pumpBackFlowCoefficient;
    }

    private static final StringProperty deliveryProperty = new SimpleStringProperty();

    private static final StringProperty backFlowProperty = new SimpleStringProperty();

    private static final StringProperty pumpDeliveryProperty = new SimpleStringProperty();

    private static final StringProperty pumpBackFlowProperty = new SimpleStringProperty();

    public static void createDeliveryFlowUnitBinding(ComboBox<String> comboBox) {

        deliveryProperty.bind(comboBox.getSelectionModel().selectedItemProperty());
        deliveryProperty.addListener((observable, oldValue, newValue) -> {
            deliveryCoefficient = calcCoefficient(newValue);
        });
    }

    public static void createBackFlowUnitBinding(ComboBox<String> comboBox) {

        backFlowProperty.bind(comboBox.getSelectionModel().selectedItemProperty());
        backFlowProperty.addListener((observable, oldValue, newValue) -> {
            backFlowCoefficient = calcCoefficient(newValue);
        });
    }

    public static void createPumpDeliveryFlowUnitBinding(ComboBox<String> comboBox) {

        pumpDeliveryProperty.bind(comboBox.getSelectionModel().selectedItemProperty());
        pumpDeliveryProperty.addListener((observable, oldValue, newValue) -> {
            pumpDeliveryCoefficient = calcCoefficient(newValue);
        });
    }

    public static void createPumpBackFlowUnitBinding(ComboBox<String> comboBox) {

        pumpBackFlowProperty.bind(comboBox.getSelectionModel().selectedItemProperty());
        pumpBackFlowProperty.addListener((observable, oldValue, newValue) -> {
            pumpBackFlowCoefficient = calcCoefficient(newValue);
        });

    }

    private static double calcCoefficient(String value){

        double coeff = 0;
        switch (value) {
            case MILLILITRE_PER_MINUTE:
                coeff = 1.0;
                break;
            case LITRE_PER_HOUR:
                coeff = 0.06;
                break;
//                case MILLILITRE_PER_100RPM:
//                    backFlowCoefficient = 1;
//                    break;
//                case MILLILITRE_PER_200RPM:
//                    backFlowCoefficient = 1;
//                    break;
//                case MILLILITRE_PER_1000RPM:
//                    backFlowCoefficient = 1;
//                    break;
        }
        return coeff;
    }
}
