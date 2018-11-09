package fi.stardex.sisu.model;

import fi.stardex.sisu.spring.DbConfig;
import fi.stardex.sisu.spring.JavaFXSpringConfigure;
import fi.stardex.sisu.spring.JavaFXSpringConfigurePumps;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {JavaFXSpringConfigure.class, JavaFXSpringConfigurePumps.class, DbConfig.class})
public abstract class ModelTest {
}
