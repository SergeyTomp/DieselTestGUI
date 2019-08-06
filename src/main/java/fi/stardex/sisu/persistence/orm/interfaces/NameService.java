package fi.stardex.sisu.persistence.orm.interfaces;

import java.util.List;

public interface NameService {

    List<? extends Name> findAll();
}
