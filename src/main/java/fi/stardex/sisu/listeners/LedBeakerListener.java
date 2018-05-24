package fi.stardex.sisu.listeners;

import fi.stardex.sisu.injectors.InjectorChannel;
import fi.stardex.sisu.injectors.InjectorSwitchManager;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.annotation.PostConstruct;

public class LedBeakerListener /*implements ChangeListener<Boolean>*/ {

//    private LedController ledController;
//
//    private int beakerIndex;
//
//    private InjectorSwitchManager injectorSwitchManager;
//
//    private SettingsController settingsController;
//
//    public LedBeakerListener(LedController ledController, int beakerIndex, SettingsController settingsController) {
//        this.ledController = ledController;
//        this.beakerIndex = beakerIndex;
//        this.settingsController = settingsController;
//    }
//
//    @PostConstruct
//    private void init() {
//        ledController.getLedBeaker().selectedProperty().addListener(this);
//    }
//
//    @Override
//    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//        if ((settingsController.getComboInjectorConfig().getSelectionModel().getSelectedItem() == InjectorChannel.SINGLE_CHANNEL && newValue) ||
//                (settingsController.getComboInjectorConfig().getSelectionModel().getSelectedItem() == InjectorChannel.MULTI_CHANNEL))
//            injectorSwitchManager.sendRefreshedLeds();
//    }
//
//    public void setManager(InjectorSwitchManager injectorSwitchManager) {
//        this.injectorSwitchManager = injectorSwitchManager;
//    }
}
