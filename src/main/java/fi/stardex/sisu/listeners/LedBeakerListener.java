package fi.stardex.sisu.listeners;

import fi.stardex.sisu.injectors.InjectorSwitchManager;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.annotation.PostConstruct;

public class LedBeakerListener implements ChangeListener<Boolean> {

    private LedController ledController;

    private int beakerIndex;

    private InjectorSwitchManager injectorSwitchManager;

    public LedBeakerListener(LedController ledController, int beakerIndex) {
        this.ledController = ledController;
        this.beakerIndex = beakerIndex;
    }

    @PostConstruct
    private void init() {
        ledController.getLedBeaker().selectedProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        injectorSwitchManager.sendRefreshedLeds();
    }

    public void setManager(InjectorSwitchManager injectorSwitchManager) {
        this.injectorSwitchManager = injectorSwitchManager;
    }
}
