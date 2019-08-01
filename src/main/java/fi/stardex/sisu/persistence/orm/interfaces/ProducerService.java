package fi.stardex.sisu.persistence.orm.interfaces;

import java.util.List;

public interface ProducerService {

    List<? extends Producer> findAll();
    List<? extends Producer> findByIsCustom(boolean isCustom);
    void save(Producer producer);
    void delete(Producer producer);
}
