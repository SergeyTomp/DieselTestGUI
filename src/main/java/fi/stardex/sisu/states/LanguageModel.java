package fi.stardex.sisu.states;

import fi.stardex.sisu.util.i18n.Locales;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class LanguageModel {

    private final ObjectProperty<Locales> languageProperty = new SimpleObjectProperty<>();

    public ObjectProperty<Locales> languageProperty() {
        return languageProperty;
    }
}
