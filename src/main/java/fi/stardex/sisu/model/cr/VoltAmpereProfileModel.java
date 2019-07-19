package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class VoltAmpereProfileModel {

    private ObjectProperty<VoltAmpereProfile> voltAmpereProfile = new SimpleObjectProperty<>();

    public ObjectProperty<VoltAmpereProfile> voltAmpereProfileProperty() {
        return voltAmpereProfile;
    }
}
