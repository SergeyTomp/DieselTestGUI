package fi.stardex.sisu.model.uis;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class UisTabSectionModel {

    private BooleanProperty isTabVoltageShowing = new SimpleBooleanProperty();

    public BooleanProperty isTabVoltageShowingProperty() {
        return isTabVoltageShowing;
    }
}
