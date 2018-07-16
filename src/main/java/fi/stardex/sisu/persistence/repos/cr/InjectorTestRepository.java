package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InjectorTestRepository extends CrudRepository<InjectorTest, Integer> {

    @EntityGraph(value = "InjectorTest.testName", type = EntityGraph.EntityGraphType.LOAD)
    List<InjectorTest> findAllByInjector(Injector injector);


}
