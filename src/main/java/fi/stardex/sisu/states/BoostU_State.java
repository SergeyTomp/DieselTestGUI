package fi.stardex.sisu.states;

import javafx.beans.property.SimpleIntegerProperty;

//TODO FIXME to convert it into VOAP_model
public class BoostU_State {

    private SimpleIntegerProperty boostU = new SimpleIntegerProperty();

    public SimpleIntegerProperty boostU_property() {
        return boostU;
    }
}
