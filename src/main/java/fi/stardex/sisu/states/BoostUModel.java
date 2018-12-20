package fi.stardex.sisu.states;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

//TODO FIXME to convert it into VOAP_model
public class BoostUModel {

    private IntegerProperty boostUProperty = new SimpleIntegerProperty();

    public IntegerProperty boostUProperty() {
        return boostUProperty;
    }

}
