package fi.stardex.sisu.ui.controllers.pumps.settings;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.state.InjConfigurationState;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class InjConfigurationController {

    @FXML
    private ComboBox<InjectorChannel> injectorsConfigComboBox;
    private InjConfigurationState injConfigurationState;
    private I18N i18N;
    private Preferences rootPrefs;
    private final String PREF_KEY = "injectorsConfigSelected";

    public ComboBox<InjectorChannel> getInjectorsConfigComboBox() {
        return injectorsConfigComboBox;
    }

    public void setInjConfigurationState(InjConfigurationState injConfigurationState) {
        this.injConfigurationState = injConfigurationState;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){
        injectorsConfigComboBox.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));
        injConfigurationState.injConfigurationStateProperty().bind(injectorsConfigComboBox.valueProperty());
        injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.valueOf(rootPrefs.get(PREF_KEY, InjectorChannel.SINGLE_CHANNEL.name())));
        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue.name()));
    }
}
