package fi.stardex.sisu.ui.controllers.pumps.settings;

import fi.stardex.sisu.state.LanguageState;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.Locales;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class LanguageController {

    @FXML
    private ComboBox <Locales> languagesConfigComboBox;
    private LanguageState languageState;
    private Preferences rootPrefs;
    private I18N i18N;

    public ComboBox <Locales> getLanguagesConfigComboBox() {
        return languagesConfigComboBox;
    }

    public void setLanguageState(LanguageState languageState) {
        this.languageState = languageState;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){
        languagesConfigComboBox.setItems(FXCollections.observableArrayList(Locales.RUSSIAN, Locales.ENGLISH, Locales.KOREAN));
        languageState.languageStateProperty().bind(languagesConfigComboBox.valueProperty());
        languagesConfigComboBox.getSelectionModel().select(Locales.valueOf(rootPrefs.get("Language", Locales.ENGLISH.name())));
        languagesConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            rootPrefs.put("Language", newValue.name());
            i18N.setLocale(Locales.getLocale(newValue.name()));
        });
    }
}
