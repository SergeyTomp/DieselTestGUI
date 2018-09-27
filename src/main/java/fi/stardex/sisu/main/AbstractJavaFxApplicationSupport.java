package fi.stardex.sisu.main;

import com.sun.javafx.application.LauncherImpl;
import fi.stardex.sisu.spring.SpringJavaConfig;
import javafx.application.Application;
import javafx.application.Preloader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ComponentScan("fi.stardex.sisu")
@Import(SpringJavaConfig.class)
public abstract class AbstractJavaFxApplicationSupport extends Application {

    private static String[] savedArgs;

    protected ConfigurableApplicationContext context;
    private ExecutorService es = Executors.newSingleThreadExecutor();

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
        super.stop();
        context.close();
    }

    protected static void launchApp(String[] args) {
        savedArgs = args;
        LauncherImpl.launchApplication(SisuApplication.class, LogoPreloader.class, args);
    }
}
