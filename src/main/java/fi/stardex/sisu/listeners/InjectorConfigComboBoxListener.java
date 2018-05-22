package fi.stardex.sisu.listeners;

import fi.stardex.sisu.injectors.InjectorChannel;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToggleGroup;

public class InjectorConfigComboBoxListener implements ChangeListener<InjectorChannel> {

    private InjectorSectionController injectorSectionController;

    private ToggleGroup toggleGroup = new ToggleGroup();

    public InjectorConfigComboBoxListener(InjectorSectionController injectorSectionController, SettingsController settingsController) {
        this.injectorSectionController = injectorSectionController;
        settingsController.getComboInjectorConfig().getSelectionModel().selectedItemProperty().addListener(this);
        setToggleGroupToLeds(toggleGroup);
    }

    @Override
    public void changed(ObservableValue<? extends InjectorChannel> observable, InjectorChannel oldValue, InjectorChannel newValue) {
        setToggleGroupToLeds(newValue == InjectorChannel.SINGLE_CHANNEL ? toggleGroup : null);
    }

    private void setToggleGroupToLeds(ToggleGroup toggleGroup) {
        injectorSectionController.getLedBeaker4Controller().getLedBeaker().setToggleGroup(toggleGroup);
        injectorSectionController.getLedBeaker3Controller().getLedBeaker().setToggleGroup(toggleGroup);
        injectorSectionController.getLedBeaker2Controller().getLedBeaker().setToggleGroup(toggleGroup);
        injectorSectionController.getLedBeaker1Controller().getLedBeaker().setToggleGroup(toggleGroup);
    }
}
