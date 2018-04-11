package fi.stardex.sisu.util.i18n;

import fi.stardex.sisu.util.ApplicationConfigHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

    private final ApplicationConfigHandler applicationConfigHandler;
    private final UTF8Control utf8Control;

    private ObjectProperty<Locale> locale;

    public I18N(ApplicationConfigHandler applicationConfigHandler) {
        this.applicationConfigHandler = applicationConfigHandler;
        this.utf8Control = new UTF8Control();
    }

    @PostConstruct
    private void init(){
        locale = new SimpleObjectProperty<>(Locales.getLocale(applicationConfigHandler.get("Language")));
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
