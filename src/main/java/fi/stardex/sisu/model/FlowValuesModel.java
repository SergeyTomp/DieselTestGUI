package fi.stardex.sisu.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FlowValuesModel {

    private StringProperty delivery1 = new SimpleStringProperty();

    private StringProperty delivery2 = new SimpleStringProperty();

    private StringProperty delivery3 = new SimpleStringProperty();

    private StringProperty delivery4 = new SimpleStringProperty();

    private StringProperty backFlow1 = new SimpleStringProperty();

    private StringProperty backFlow2 = new SimpleStringProperty();

    private StringProperty backFlow3 = new SimpleStringProperty();

    private StringProperty backFlow4 = new SimpleStringProperty();

    public StringProperty delivery1Property() {
        return delivery1;
    }

    public StringProperty delivery2Property() {
        return delivery2;
    }

    public StringProperty delivery3Property() {
        return delivery3;
    }

    public StringProperty delivery4Property() {
        return delivery4;
    }

    public StringProperty backFlow1Property() {
        return backFlow1;
    }

    public StringProperty backFlow2Property() {
        return backFlow2;
    }

    public StringProperty backFlow3Property() {
        return backFlow3;
    }

    public StringProperty backFlow4Property() {
        return backFlow4;
    }
}
