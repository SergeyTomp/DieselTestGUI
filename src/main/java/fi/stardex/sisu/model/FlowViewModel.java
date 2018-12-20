package fi.stardex.sisu.model;

import fi.stardex.sisu.combobox_values.Dimension;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class FlowViewModel {

    private final ObjectProperty<Dimension> flowViewProperty = new SimpleObjectProperty<>();

    public ObjectProperty<Dimension> flowViewProperty() {
        return flowViewProperty;
    }

}
