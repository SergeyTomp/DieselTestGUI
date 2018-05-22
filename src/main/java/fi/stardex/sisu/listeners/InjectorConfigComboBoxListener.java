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
    }

    @Override
    public void changed(ObservableValue<? extends InjectorChannel> observable, InjectorChannel oldValue, InjectorChannel newValue) {
        if (newValue == InjectorChannel.SINGLE_CHANNEL) {
            injectorSectionController.getLedBeaker4Controller().getLedBeaker().setToggleGroup(toggleGroup);
            injectorSectionController.getLedBeaker3Controller().getLedBeaker().setToggleGroup(toggleGroup);
            injectorSectionController.getLedBeaker2Controller().getLedBeaker().setToggleGroup(toggleGroup);
            injectorSectionController.getLedBeaker1Controller().getLedBeaker().setToggleGroup(toggleGroup);
        } else {
            injectorSectionController.getLedBeaker1Controller().getLedBeaker().setToggleGroup(null);
            injectorSectionController.getLedBeaker2Controller().getLedBeaker().setToggleGroup(null);
            injectorSectionController.getLedBeaker3Controller().getLedBeaker().setToggleGroup(null);
            injectorSectionController.getLedBeaker4Controller().getLedBeaker().setToggleGroup(null);
        }
    }
}
