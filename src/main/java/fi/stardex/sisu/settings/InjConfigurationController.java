package fi.stardex.sisu.settings;

import fi.stardex.sisu.combobox_values.InjectorChannel;
import fi.stardex.sisu.states.InjConfigurationModel;
import fi.stardex.sisu.states.InjectorTypeModel;
import fi.stardex.sisu.util.enums.InjectorType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class InjConfigurationController {

    @FXML
    private ComboBox<InjectorChannel> injectorsConfigComboBox;

    private InjConfigurationModel injConfigurationModel;

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

    @PostConstruct
    public void init() {

        injectorsConfigComboBox.setItems(FXCollections.observableArrayList(InjectorChannel.SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));

        injConfigurationModel.injConfigurationProperty().bind(injectorsConfigComboBox.valueProperty());

        injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.valueOf(rootPrefs.get(PREF_KEY, InjectorChannel.SINGLE_CHANNEL.name())));

        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue.name()));

        injectorTypeModel.injectorTypeProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals(InjectorType.PIEZO_DELPHI))
                injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.SINGLE_CHANNEL);
        });

    }

}
