package fi.stardex.sisu.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DelayModel {
    
    private IntegerProperty addingTime = new SimpleIntegerProperty();
    
    public IntegerProperty addingTimeProperty() {
        return addingTime;
    }
}
