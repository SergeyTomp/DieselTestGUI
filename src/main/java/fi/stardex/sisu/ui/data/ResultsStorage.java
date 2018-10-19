package fi.stardex.sisu.ui.data;


import fi.stardex.sisu.persistence.orm.interfaces.Model;

import java.util.List;

/**
 * Created
 * by eduard
 * on 06.04.17.
 */
public interface ResultsStorage {

    List<? extends Result> getResultsList(Model model);
    Boolean containsKey(Model model);
}
