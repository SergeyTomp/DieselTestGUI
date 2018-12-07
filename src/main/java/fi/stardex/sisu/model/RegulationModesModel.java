package fi.stardex.sisu.model;

import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RegulationModesModel {

    private ObjectProperty<RegActive> regulatorOneMode = new SimpleObjectProperty<>();
    private ObjectProperty<RegActive> regulatorTwoMode = new SimpleObjectProperty<>();
    private ObjectProperty<RegActive> regulatorThreeMode = new SimpleObjectProperty<>();;

    public ObjectProperty<RegActive> getRegulatorOneMode() {
        return regulatorOneMode;
    }

    public ObjectProperty<RegActive> getRegulatorTwoMode() {
        return regulatorTwoMode;
    }

    public ObjectProperty<RegActive> getRegulatorThreeMode() {
        return regulatorThreeMode;
    }
}
