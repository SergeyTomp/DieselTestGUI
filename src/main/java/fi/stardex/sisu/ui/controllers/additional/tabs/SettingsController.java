package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.injectors.InjectorChannel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class SettingsController {

    @FXML
    private ComboBox<InjectorChannel> comboInjectorConfig;

    @FXML
    public CheckBox autoResetCheckBox;

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    public ComboBox<InjectorChannel> getComboInjectorConfig() {
        return comboInjectorConfig;
    }

    @PostConstruct
    private void init() {

        autoResetCheckBox.setSelected(prefs.getBoolean("autoResetCheckBoxSelected", true));

        comboInjectorConfig.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL,InjectorChannel.MULTI_CHANNEL));
        comboInjectorConfig.getSelectionModel().selectFirst();

        autoResetCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            prefs.putBoolean("autoResetCheckBoxSelected", newValue);
        });
    }

}
