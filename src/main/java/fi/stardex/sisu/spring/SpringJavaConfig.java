package fi.stardex.sisu.spring;

import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.i18n.I18N;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JavaFXSpringConfigure.class)
public class SpringJavaConfig {

    @Bean
    public ApplicationConfigHandler applicationConfigHandler() {
        return new ApplicationConfigHandler();
    }

    @Bean
    @Autowired
    public I18N i18N(ApplicationConfigHandler applicationConfigHandler) {
        return new I18N(applicationConfigHandler);
    }
}
