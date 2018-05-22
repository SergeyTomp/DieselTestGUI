package fi.stardex.sisu.listeners;

import fi.stardex.sisu.injectors.InjectorSwitchManager;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;

public class InjectorTypeListener implements ChangeListener<Toggle> {

    private InjectorSwitchManager injectorSwitchManager;

    public InjectorTypeListener(InjectorSectionController injectorSectionController, InjectorSwitchManager injectorSwitchManager) {
        this.injectorSwitchManager = injectorSwitchManager;
        injectorSectionController.getPiezoCoilToggleGroup().selectedToggleProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        injectorSwitchManager.sendRefreshedLeds();
    }
}
