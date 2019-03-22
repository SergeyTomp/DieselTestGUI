package fi.stardex.sisu.settings;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.model.InjConfigurationModel;
import fi.stardex.sisu.model.InjectorTypeModel;
import fi.stardex.sisu.states.BoostUModel;
import fi.stardex.sisu.util.enums.InjectorType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.combobox_values.InjectorChannel.MULTI_CHANNEL;
import static fi.stardex.sisu.combobox_values.InjectorChannel.SINGLE_CHANNEL;

public class InjConfigurationController {

    @FXML
    private ComboBox<InjectorChannel> injectorsConfigComboBox;

    private InjConfigurationModel injConfigurationModel;

    private BoostUModel boostUModel;

    private Preferences rootPrefs;

    private static final String PREF_KEY = "injectorsConfigSelected";

    private InjectorTypeModel injectorTypeModel;

    public void setInjConfigurationModel(InjConfigurationModel injConfigurationModel) {
        this.injConfigurationModel = injConfigurationModel;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    public void setInjectorTypeModel(InjectorTypeModel injectorTypeModel) {
        this.injectorTypeModel = injectorTypeModel;
    }

    public void setBoostUModel(BoostUModel boostUModel) {
        this.boostUModel = boostUModel;
    }

    @PostConstruct
    public void init() {

        injectorsConfigComboBox.setItems(FXCollections.observableArrayList(SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));

        injConfigurationModel.injConfigurationProperty().bind(injectorsConfigComboBox.valueProperty());

        injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.valueOf(rootPrefs.get(PREF_KEY, SINGLE_CHANNEL.name())));

        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue.name()));

        SINGLE_CHANNEL.setLastValue( injectorsConfigComboBox.getSelectionModel().getSelectedItem());

        MULTI_CHANNEL.setLastValue( injectorsConfigComboBox.getSelectionModel().getSelectedItem());

        injectorTypeModel.injectorTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue.equals(InjectorType.PIEZO_DELPHI)) {
                selectChannelMode(SINGLE_CHANNEL);
            } else{
                restoreChannelMode();
            }
        });

        boostUModel.isDoubleCoilProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue) {
                selectChannelMode(MULTI_CHANNEL);
            } else {
                restoreChannelMode();
            }
        });
    }

    private void selectChannelMode(InjectorChannel mode) {

        InjectorChannel currentSelectedItem = injectorsConfigComboBox.getSelectionModel().getSelectedItem();
        injectorsConfigComboBox.getSelectionModel().select(mode);
        mode.setLastValue(currentSelectedItem);
        injectorsConfigComboBox.setDisable(true);
    }

    private void restoreChannelMode() {

        InjectorChannel previousSelectedItem = injectorsConfigComboBox.getSelectionModel().getSelectedItem().getLastValue();
        injectorsConfigComboBox.getSelectionModel().select(previousSelectedItem);
        injectorsConfigComboBox.setDisable(false);
    }
}
