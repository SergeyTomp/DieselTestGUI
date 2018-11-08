package fi.stardex.sisu.state;

import fi.stardex.sisu.util.i18n.Locales;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class LanguageState {

    private final ObjectProperty<Locales> languageState = new SimpleObjectProperty<>();

    public ObjectProperty<Locales> languageStateProperty() {
        return languageState;
    }
}
