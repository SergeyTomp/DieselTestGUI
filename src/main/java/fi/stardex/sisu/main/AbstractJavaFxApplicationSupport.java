package fi.stardex.sisu.main;

import com.sun.javafx.application.LauncherImpl;
import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.spring.SpringJavaConfig;
import javafx.application.Application;
import javafx.application.Preloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static fi.stardex.sisu.registers.stand.ModbusMapStand.Rotation;
import static fi.stardex.sisu.registers.stand.ModbusMapStand.RotationStandFM;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;

@SpringBootApplication
@ComponentScan("fi.stardex.sisu")
@Import(SpringJavaConfig.class)
public abstract class AbstractJavaFxApplicationSupport extends Application {

    private static String[] savedArgs;

    protected ConfigurableApplicationContext context;
    private ExecutorService es = Executors.newSingleThreadExecutor();

    @Autowired
    private ModbusRegisterProcessor ultimaModbusWriter;
    @Autowired
    private ModbusRegisterProcessor flowModbusWriter;
    @Autowired
    private ModbusRegisterProcessor standModbusWriter;
    @Autowired
    private TimerTasksManager timerTasksManager;

    @Override
    public void init() {

        Thread progress = new Thread(new Runnable() {
            @Override
            public void run() {
                double progress = 0;
                while (true) {
                    try {
                        notifyPreloader(new Preloader.ProgressNotification(progress));
                        progress += 0.01;
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        notifyPreloader(new Preloader.ProgressNotification(1));
                        try {Thread.sleep(500); } catch (InterruptedException e1) {}
                        return;
                    }
                }
            }
        });
        es.submit(progress);
        context = SpringApplication.run(AbstractJavaFxApplicationSupport.class, savedArgs);
        context.getAutowireCapableBeanFactory().autowireBean(this);
        if(!es.isTerminated()){
            es.shutdownNow();
        }
        try {
            es.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {

        ultimaModbusWriter.add(PressureReg1_ON, false);
        ultimaModbusWriter.add(PressureReg2_ON, false);
        ultimaModbusWriter.add(PressureReg3_ON, false);
        ultimaModbusWriter.add(Injectors_Running_En, false);
        getSlotNumbersList().forEach((s) -> ultimaModbusWriter.add(s, 255));
        getSlotPulsesList().forEach((s) -> ultimaModbusWriter.add(s, 65535));
        timerTasksManager.stop();
        flowModbusWriter.add(RotationStandFM, false);
        standModbusWriter.add(Rotation, false);
        Thread.sleep(2000);
        super.stop();
        context.close();
    }

    protected static void launchApp(String[] args) {
        savedArgs = args;
        LauncherImpl.launchApplication(SisuApplication.class, LogoPreloader.class, args);
    }
}
