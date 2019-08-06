package fi.stardex.sisu.persistence.orm.interfaces;

import java.util.List;

public interface ModelService {

    List<? extends Model> findByProducer(Producer producer);
    List<? extends Model> findByProducerAndIsCustom(Producer producer, boolean isCustom);
    Model findByModelCode(String modelCode);
    List<? extends Model> findByIsCustom(boolean isCustom);
    boolean existsByModelCode(String code);
    void save (Model model);
    void delete (Model model);

}
