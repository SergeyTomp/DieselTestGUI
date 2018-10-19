package fi.stardex.sisu.ui.data;

import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.persistence.orm.interfaces.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by oleg on 07.04.16.
 */
public interface SavedData {
    Map<Model, Map<Test, ArrayList<Double>>> getSavedDataForBeakers();
    List<TestResult> getTestResult(Model model);
}
