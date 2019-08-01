package fi.stardex.sisu.persistence.orm.interfaces;

import java.util.List;

public interface TestService {

    List<? extends Test> findAllByInjector(Model model);
    List<? extends Test> findAllByIsCustom(boolean isCustom);

}
