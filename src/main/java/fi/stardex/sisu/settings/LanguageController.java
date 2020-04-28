package fi.stardex.sisu.settings;

import fi.stardex.sisu.model.LanguageModel;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.Locales;
import fi.stardex.sisu.util.listeners.LocaleChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class LanguageController {

    @FXML
    private ComboBox <Locales> languagesConfigComboBox;

    private LanguageModel languageModel;

    private Preferences rootPrefs;

    private I18N i18N;

    private Boolean localeChange = false;

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){

        languagesConfigComboBox.setItems(FXCollections.observableArrayList(Locales.values()));

        languageModel.languageProperty().bind(languagesConfigComboBox.valueProperty());

        languagesConfigComboBox.getSelectionModel().select(Locales.valueOf(rootPrefs.get("Language", Locales.ENGLISH.name())));

        languageModel.languageProperty().addListener((observable, oldValue, newValue) -> {

            rootPrefs.put("Language", newValue.name());
            i18N.setLocale(Locales.getLocale(newValue.name()));

        });
        i18N.localeProperty().addListener(new LocaleChangeListener(languagesConfigComboBox, localeChange));
    }

}
