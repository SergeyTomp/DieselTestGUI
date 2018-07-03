package fi.stardex.sisu.util.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public enum Locales {
    ENGLISH(new Locale("en","English")),
    RUSSIAN(new Locale("ru","Russian")),
    KOREAN(new Locale("kor", "Korean"));

    private static final Logger logger = LoggerFactory.getLogger(Locales.class);

    private Locale locale;

    Locales(Locale locale) {
        this.locale = locale;
    }

    public static Locale getLocale(String lang){
        for (Locales l: values()) {
            if (l.name().equalsIgnoreCase(lang)) {
                return l.locale;
            }
        }
        logger.warn("Cannot find language '{}'. English locale will be used.", lang);
        return ENGLISH.locale;
    }
}
