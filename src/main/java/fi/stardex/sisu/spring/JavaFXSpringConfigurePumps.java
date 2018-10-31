package fi.stardex.sisu.spring;

import fi.stardex.sisu.util.i18n.I18N;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "fi.stardex.sisu")
public class JavaFXSpringConfigurePumps extends ViewLoader{

    public JavaFXSpringConfigurePumps(I18N i18N) {
        super(i18N);
    }
}
