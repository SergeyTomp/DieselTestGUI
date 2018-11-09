package fi.stardex.sisu.settings;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.states.InjConfigurationState;
import fi.stardex.sisu.states.InjectorTypeToggleState;
import fi.stardex.sisu.util.enums.InjectorType;
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
    private InjectorTypeToggleState injectorTypeToggleState;

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

    public void setInjectorTypeToggleState(InjectorTypeToggleState injectorTypeToggleState) {
        this.injectorTypeToggleState = injectorTypeToggleState;
    }

    @PostConstruct
    public void init(){
        injectorsConfigComboBox.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));

        injConfigurationState.injConfigurationStateProperty().bind(injectorsConfigComboBox.valueProperty());

        injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.valueOf(rootPrefs.get(PREF_KEY, InjectorChannel.SINGLE_CHANNEL.name())));

        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue.name()));

        injectorTypeToggleState.injectorTypeObjectPropertyProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue.equals(InjectorType.PIEZO_DELPHI))
            injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.SINGLE_CHANNEL);});
    }
}
