package fi.stardex.sisu.model.cr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO this model is currently auxiliary, use it as separate during FlowController model-concept implementation
public class FlowValuesModel {

    private StringProperty delivery1Property = new SimpleStringProperty();

    private StringProperty delivery2Property = new SimpleStringProperty();

    private StringProperty delivery3Property = new SimpleStringProperty();

    private StringProperty delivery4Property = new SimpleStringProperty();

    private StringProperty backFlow1Property = new SimpleStringProperty();

    private StringProperty backFlow2Property = new SimpleStringProperty();

    private StringProperty backFlow3Property = new SimpleStringProperty();

    private StringProperty backFlow4Property = new SimpleStringProperty();

    public StringProperty delivery1Property() {
        return delivery1Property;
    }

    public StringProperty delivery2Property() {
        return delivery2Property;
    }

    public StringProperty delivery3Property() {
        return delivery3Property;
    }

    public StringProperty delivery4Property() {
        return delivery4Property;
    }

    public StringProperty backFlow1Property() {
        return backFlow1Property;
    }

    public StringProperty backFlow2Property() {
        return backFlow2Property;
    }

    public StringProperty backFlow3Property() {
        return backFlow3Property;
    }

    public StringProperty backFlow4Property() {
        return backFlow4Property;
    }

    public List<StringProperty> getDeliveryList() {
        return new ArrayList<>(Arrays.asList(
                delivery1Property(), delivery2Property(), delivery3Property(), delivery4Property()));
    }

    public List<StringProperty> getBackFlowList() {
        return new ArrayList<>(Arrays.asList(
                backFlow1Property(), backFlow2Property(), backFlow3Property(), backFlow4Property()));
    }
}
