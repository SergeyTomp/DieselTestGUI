package fi.stardex.sisu.model.pump;

import fi.stardex.sisu.util.enums.Dimension;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class FlowViewModel {

    private final ObjectProperty<Dimension> flowViewProperty = new SimpleObjectProperty<>();

    public ObjectProperty<Dimension> flowViewProperty() {
        return flowViewProperty;
    }

}
