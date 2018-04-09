package fi.stardex.sisu.spring;

import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringJavaConfig {

    @Bean
    public ApplicationConfigHandler applicationConfigHandler() {
        return new ApplicationConfigHandler();
    }

    @Bean
    public UTF8Control utf8Control() {
        return new UTF8Control();
    }

    @Bean
    public I18N i18N() {
        return new I18N(applicationConfigHandler(), utf8Control());
    }
}
