package fi.stardex.sisu.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TabSectionModel {

    private BooleanProperty step3TabIsShowing = new SimpleBooleanProperty();
    private BooleanProperty piezoTabIsShowing = new SimpleBooleanProperty();

    public BooleanProperty step3TabIsShowingProperty() {
        return step3TabIsShowing;
    }
    public BooleanProperty piezoTabIsShowingProperty() {
        return piezoTabIsShowing;
    }
}
