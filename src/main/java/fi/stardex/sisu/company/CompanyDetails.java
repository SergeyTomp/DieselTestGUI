package fi.stardex.sisu.company;

import java.util.prefs.Preferences;

public enum CompanyDetails {

    LOGO_URL("logo"),
    NAME("name"),
    ADDRESS("address"),
    PHONE1("phone1"),
    PHONE2("phone2"),
    EMAIL("email"),
    URL_FIELD("url");

    private Preferences preferences;
    private String key;
    private static final String COMPANY_DETAILS = "companydetails";
    public static final String DEFAULT_VALUE = "__";

    CompanyDetails(String key) {
        this.key = key;
        preferences =  Preferences.userRoot().node(COMPANY_DETAILS);
    }

    public void put(String value) {
        preferences.put(key, value);
    }

    public String get(String defaultValue) {
        return preferences.get(key, defaultValue);
    }

    public String get() {
        return get(DEFAULT_VALUE);
    }

}
