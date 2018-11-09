package fi.stardex.sisu.states;

import fi.stardex.sisu.combobox_values.Dimension;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class FlowViewState {

    private final ObjectProperty <Dimension> flowViewState = new SimpleObjectProperty<>();

    public ObjectProperty <Dimension> flowViewStateProperty() {
        return flowViewState;
    }
}
