package fi.stardex.sisu.model;

import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RegulationModesModel {

    private ObjectProperty<RegActive> regulatorOneModeProperty = new SimpleObjectProperty<>();

    private ObjectProperty<RegActive> regulatorTwoModeProperty = new SimpleObjectProperty<>();

    private ObjectProperty<RegActive> regulatorThreeModeProperty = new SimpleObjectProperty<>();

    public ObjectProperty<RegActive> regulatorOneModeProperty() {
        return regulatorOneModeProperty;
    }

    public ObjectProperty<RegActive> regulatorTwoModeProperty() {
        return regulatorTwoModeProperty;
    }

    public ObjectProperty<RegActive> regulatorThreeModeProperty() {
        return regulatorThreeModeProperty;
    }

}
