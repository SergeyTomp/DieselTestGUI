package fi.stardex.sisu.util.listeners;

import fi.stardex.sisu.util.i18n.Locales;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.Locale;

import static fi.stardex.sisu.util.i18n.Locales.ENGLISH;

public class LocaleChangeListener implements ChangeListener<Locale> {

    private ComboBox<Locales> language;
    private Boolean shield;

    public LocaleChangeListener(ComboBox<Locales> language, Boolean shield) {
        this.language = language;
        this.shield = shield;
    }

    @Override
    public void changed(ObservableValue<? extends Locale> observable, Locale oldValue, Locale newValue) {

        shield = true;
        language.getSelectionModel().select(Arrays.stream(Locales.values())
                .filter(l -> l.toString().equals(newValue.getCountry()))
                .findFirst()
                .orElse(ENGLISH));
        shield = false;
    }
}
