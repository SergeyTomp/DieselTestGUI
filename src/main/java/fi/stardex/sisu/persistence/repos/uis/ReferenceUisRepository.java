package fi.stardex.sisu.persistence.repos.uis;

import fi.stardex.sisu.persistence.orm.uis.ReferenceUIS;
import org.springframework.data.repository.CrudRepository;

public interface ReferenceUisRepository extends CrudRepository<ReferenceUIS, String> {

    // Usage of the method is in UisModelService, commented until becomes relevant.

    ReferenceUIS findByKeyInjector(String modelCode);

    // можно ещё попробовать вот так
//     @Override
//     Optional<ReferenceUIS> findById(String modelCode);
}
