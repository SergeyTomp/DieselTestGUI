package fi.stardex.sisu.ui.wrappers;

import fi.stardex.sisu.util.converters.StringNumberConverter;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.Objects;

public class CleverField {

    private DoubleProperty doubleProperty;

    private TextField textField;

    CleverField(TextField textField) {
        this.textField = textField;
        doubleProperty = new SimpleDoubleProperty();
        textField.textProperty().bindBidirectional(doubleProperty, new StringNumberConverter(Double.class));
        setEditable(false);
    }

    public void clear() {
        doubleProperty.set(-1);
    }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
        if (!editable) {
            clear();
        }
    }

    public boolean isEditable() {
        return textField.isEditable();
    }

    public double get() {
        return doubleProperty.get();
    }

    public void set(double value) {
        doubleProperty.set(value);
    }

    public void addListener(ChangeListener<Double> changeListener) {
        doubleProperty.addListener(changeListener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CleverField that = (CleverField) o;
        return Objects.equals(textField, that.textField);
    }

    @Override
    public int hashCode() {

        return Objects.hash(textField);
    }


}
