package fi.stardex.sisu.persistence.repos.cr;

import fi.stardex.sisu.persistence.orm.denso_info.DensoInjectors;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DensoRepository extends CrudRepository<DensoInjectors, String> {
    @Query("SELECT di FROM DensoInjectors di WHERE REPLACE(REPLACE(REPLACE(REPLACE(di.code1, \'#\', \'\'), \'-\', \'\'), \'HU\', \'\'), \'SM\', \'\') = :word")
    List<DensoInjectors> search(@Param("word") String word);

    @Query("SELECT di FROM DensoInjectors di WHERE REPLACE(REPLACE(REPLACE(REPLACE(di.code2, \'#\', \'\'), \'-\', \'\'), \'HU\', \'\'), \'SM\', \'\') = :word")
    List<DensoInjectors> searchCode2(@Param("word") String word);
}
