package fi.stardex.sisu.util.listeners;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class VoltageLabelListener implements ChangeListener<String> {

    private Label label;
    private DoubleProperty doubleValue;
    private IntegerProperty intValue;
    private static final String RED_COLOR_STYLE = "-fx-text-fill: red";

    public VoltageLabelListener(Label label, DoubleProperty value) {
        this.label = label;
        this.doubleValue = value;
    }

    public VoltageLabelListener(Label label, IntegerProperty value) {
        this.label = label;
        this.intValue = value;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {

        if (doubleValue != null) {

            if (convertDataToDouble(newValue) != doubleValue.get()) {
                setStyle(RED_COLOR_STYLE);
            }else{
                setStyle(null);
            }

        } else if (intValue != null) {

            if (convertDataToInt(newValue) != intValue.get()) {
                setStyle(RED_COLOR_STYLE);
            }else{
                setStyle(null);
            }
        }
    }

    private void setStyle(String style) {

        label.setStyle(style);
    }
}
