package fi.stardex.sisu.company;

import java.util.prefs.Preferences;

/**
 * Created by stardex on 27.01.2016.
 */
public class PreferenceProvider {

    private static final String COMPANY_DETAILS = "companydetails";

    public Preferences getCompanyDetails() {
        return Preferences.userRoot().node(COMPANY_DETAILS);
    }
}
