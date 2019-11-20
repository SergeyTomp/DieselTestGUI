package fi.stardex.sisu.model;

import fi.stardex.sisu.util.enums.GUI_type;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GUI_TypeModel {

    private ObjectProperty<GUI_type> guiType = new SimpleObjectProperty<>();

    public ObjectProperty<GUI_type> guiTypeProperty() {
        return guiType;
    }
}
