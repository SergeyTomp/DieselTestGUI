package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.uis.InjectorUIS;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InjectorUisRepository extends CrudRepository<InjectorUIS, String> {

    List<InjectorUIS> findByManufacturer(Producer producer);
    List<InjectorUIS> findByManufacturerAndIsCustom(Producer producer, boolean isCustom);

    @EntityGraph(value = "InjectorsUIS.modelCode", type = EntityGraph.EntityGraphType.LOAD)
    InjectorUIS findByInjectorCode(String code);

    @EntityGraph(value = "InjectorsUIS.allLazy", type = EntityGraph.EntityGraphType.LOAD)
    List<InjectorUIS> findByIsCustom(boolean isCustom);

    boolean existsByInjectorCode(String modelCode);

}
