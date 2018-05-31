package fi.stardex.sisu.styles;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FontColour {

    private static StringProperty fontColourProperty = new SimpleStringProperty();

    static {
        fontColourProperty.set(null);
    }

    public static String getFontColourProperty() {
        return fontColourProperty.get();
    }

    public static StringProperty fontColourPropertyProperty() {
        return fontColourProperty;
    }

    public static void setFontColourProperty(String fontColourProperty) {
        FontColour.fontColourProperty.set(fontColourProperty);
    }
}
