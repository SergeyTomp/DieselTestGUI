package fi.stardex.sisu.main;

import fi.stardex.sisu.logging.LogbackUncaughtExceptionHandler;
import fi.stardex.sisu.ui.ViewHolder;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

public class SisuApplication extends AbstractJavaFxApplicationSupport {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SISU");

        ViewHolder rootLayoutViewHolder = context.getBean("rootLayout", ViewHolder.class);
        Scene scene = new Scene(rootLayoutViewHolder.getView(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        String appId = "StardexGUI";

        try {
            JUnique.acquireLock(appId);
            Thread.setDefaultUncaughtExceptionHandler(new LogbackUncaughtExceptionHandler());
            launchApp(SisuApplication.class, args);
        } catch (AlreadyLockedException e) {
            JOptionPane.showMessageDialog(null, "Another instance of application is already running. Exiting.", "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(10);
        }
    }
}
