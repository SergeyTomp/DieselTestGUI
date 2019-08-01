package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InjectorUisTestRepository extends CrudRepository<InjectorUisTest, Integer> {

    @EntityGraph(value = "InjectorUisTest.testName", type = EntityGraph.EntityGraphType.LOAD)
    List<InjectorUisTest> findAllByInjector(Model model);

    @EntityGraph(value = "InjectorTest.allLazy", type = EntityGraph.EntityGraphType.LOAD)
    List<InjectorUisTest> findAllByIsCustom(boolean isCustom);

}
