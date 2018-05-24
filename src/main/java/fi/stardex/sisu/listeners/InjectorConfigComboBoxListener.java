package fi.stardex.sisu.listeners;

import fi.stardex.sisu.injectors.InjectorChannel;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.wrappers.LedControllerWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleGroup;

import java.util.List;

public class InjectorConfigComboBoxListener implements ChangeListener<InjectorChannel> {

    private ToggleGroup toggleGroup = new ToggleGroup();

    private ObservableList<LedController> ledControllers;

    public InjectorConfigComboBoxListener(InjectorSectionController injectorSectionController, SettingsController settingsController) {
        ledControllers = injectorSectionController.getLedControllers();
        settingsController.getComboInjectorConfig().getSelectionModel().selectedItemProperty().addListener(this);
        setToggleGroupToLeds(toggleGroup);
    }

    @Override
    public void changed(ObservableValue<? extends InjectorChannel> observable, InjectorChannel oldValue, InjectorChannel newValue) {
        setToggleGroupToLeds(newValue == InjectorChannel.SINGLE_CHANNEL ? toggleGroup : null);
    }

    private void setToggleGroupToLeds(ToggleGroup toggleGroup) {
        System.err.println(toggleGroup);
        ledControllers.forEach(s -> s.getLedBeaker().setToggleGroup(toggleGroup));
    }
}
