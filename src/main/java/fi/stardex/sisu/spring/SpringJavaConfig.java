package fi.stardex.sisu.spring;

import fi.stardex.sisu.connect.ConnectProcessor;
import fi.stardex.sisu.connect.InetAddressWrapper;
import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.ui.controllers.additional.tabs.ConnectionController;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import(JavaFXSpringConfigure.class)
@EnableScheduling
public class SpringJavaConfig {

    @Bean
    public ConnectProcessor connectProcessor() {
        return new ConnectProcessor();
    }

    @Bean
    public InetAddressWrapper inetAddressWrapper() {
        return new InetAddressWrapper();
    }

    @Bean
    @Autowired
    public ModbusConnect ultimaModbusConnect(ConnectionController connectionController, ConnectProcessor connectProcessor, Devices devices, StatusBarWrapper statusBar,
                                             InetAddressWrapper inetAddressWrapper) {
        return new ModbusConnect(connectionController.getUltimaConnect(), connectProcessor, devices, statusBar, Device.ULTIMA, inetAddressWrapper);
    }

    @Bean
    public Devices devices() {
        return new Devices();
    }

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
