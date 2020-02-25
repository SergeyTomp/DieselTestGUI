package fi.stardex.sisu.util.enums;

import javafx.beans.property.ObjectProperty;

import java.util.Set;

public interface ControlsService {

    Set<? extends Controls> getControls();
    ObjectProperty<Object> controlsChangeProperty();
}
