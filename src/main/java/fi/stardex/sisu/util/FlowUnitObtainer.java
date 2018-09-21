package fi.stardex.sisu.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;

public class FlowUnitObtainer {

    public static final String MILLILITRE_PER_MINUTE = "ml/min";

    public static final String LITRE_PER_HOUR = "l/h";

    public static final String MILLILITRE_PER_100RPM = "ml/100str";

    public static final String MILLILITRE_PER_200RPM = "ml/200str";

    public static final String MILLILITRE_PER_1000RPM = "ml/1000str";

    private static double deliveryCoefficient;

    private static double backFlowCoefficient;

    public static double getDeliveryCoefficient() {
        return deliveryCoefficient;
    }

    public static double getBackFlowCoefficient() {
        return backFlowCoefficient;
    }

    private static final StringProperty deliveryProperty = new SimpleStringProperty();

    private static final StringProperty backFlowProperty = new SimpleStringProperty();

    public static void createDeliveryFlowUnitBinding(ComboBox<String> comboBox) {

        deliveryProperty.bind(comboBox.getSelectionModel().selectedItemProperty());
        deliveryProperty.addListener((observable, oldValue, newValue) -> {

            switch (newValue) {
                case MILLILITRE_PER_MINUTE:
                    deliveryCoefficient = 1;
                    break;
                case LITRE_PER_HOUR:
                    deliveryCoefficient = 0.06;
                    break;
                case MILLILITRE_PER_100RPM:
                    deliveryCoefficient = 1;
                    break;
                case MILLILITRE_PER_200RPM:
                    deliveryCoefficient = 1;
                    break;
                case MILLILITRE_PER_1000RPM:
                    deliveryCoefficient = 1;
                    break;
            }

        });

    }

    public static void createBackFlowUnitBinding(ComboBox<String> comboBox) {

        backFlowProperty.bind(comboBox.getSelectionModel().selectedItemProperty());
        backFlowProperty.addListener((observable, oldValue, newValue) -> {

            switch (newValue) {
                case MILLILITRE_PER_MINUTE:
                    backFlowCoefficient = 1;
                    break;
                case LITRE_PER_HOUR:
                    backFlowCoefficient = 0.06;
                    break;
                case MILLILITRE_PER_100RPM:
                    backFlowCoefficient = 1;
                    break;
                case MILLILITRE_PER_200RPM:
                    backFlowCoefficient = 1;
                    break;
                case MILLILITRE_PER_1000RPM:
                    backFlowCoefficient = 1;
                    break;
            }

        });

    }

}
