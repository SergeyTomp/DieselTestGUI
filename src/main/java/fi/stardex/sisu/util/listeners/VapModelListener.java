package fi.stardex.sisu.util.listeners;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;

public class VapModelListener implements ChangeListener<Number> {

    private Label valueLabel;
    private static final String RED_COLOR_STYLE = "-fx-text-fill: red";

    public VapModelListener(Label valueLabel) {
        this.valueLabel = valueLabel;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

        if (newValue instanceof Double) {

            setStyle(newValue.doubleValue() == convertDataToDouble(valueLabel.getText()));
        } else if (newValue instanceof Integer) {

            setStyle(newValue.intValue() == convertDataToInt(valueLabel.getText()));
        }
    }
    private void setStyle(boolean equal) {

        if(equal) valueLabel.setStyle(null);
        else valueLabel.setStyle(RED_COLOR_STYLE);
    }
}
