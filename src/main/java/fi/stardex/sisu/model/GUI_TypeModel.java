package fi.stardex.sisu.model;

import fi.stardex.sisu.ui.controllers.GUI_TypeController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GUI_TypeModel {

    private ObjectProperty<GUI_TypeController.GUIType> guiType = new SimpleObjectProperty<>();

    public ObjectProperty<GUI_TypeController.GUIType> guiTypeProperty() {
        return guiType;
    }
}
