package fi.stardex.sisu.util.i18n;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class I18N {

    private final UTF8Control utf8Control;

    private ObjectProperty<Locale> locale;

    private Preferences rootPrefs;

    public I18N(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
        this.utf8Control = new UTF8Control();
    }

    @PostConstruct
    private void init(){
        locale = new SimpleObjectProperty<>(Locales.getLocale(rootPrefs.get("Language", Locales.ENGLISH.name())));
    }


    public Locale getLocale() {
        return locale.get();
    }

    public void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }

    private ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    private String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("properties.labels", getLocale(), utf8Control);
        return MessageFormat.format(bundle.getString(key), args);
    }

    public StringBinding createStringBinding(final String key) {
        return Bindings.createStringBinding(() -> get(key), locale);
    }

}
