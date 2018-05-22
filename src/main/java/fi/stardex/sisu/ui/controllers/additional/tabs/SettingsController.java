package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.injectors.InjectorChannel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;

public class SettingsController {

    @FXML
    public ComboBox<InjectorChannel> comboInjectorConfig;

    public ComboBox<InjectorChannel> getComboInjectorConfig() {
        return comboInjectorConfig;
    }

    @PostConstruct
    private void init() {
        comboInjectorConfig.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL,InjectorChannel.MULTI_CHANNEL));
        comboInjectorConfig.getSelectionModel().selectFirst();
    }
}
