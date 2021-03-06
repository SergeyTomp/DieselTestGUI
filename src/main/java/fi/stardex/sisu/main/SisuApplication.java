package fi.stardex.sisu.main;

import fi.stardex.sisu.logging.LogbackUncaughtExceptionHandler;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.common.RootLayoutController;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;

public class SisuApplication extends AbstractJavaFxApplicationSupport {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle(windowTitle);

        ViewHolder rootLayoutViewHolder = context.getBean("rootLayout", ViewHolder.class);
        RootLayoutController rootLayoutController = (RootLayoutController) rootLayoutViewHolder.getController();
        StatusBarWrapper statusBar = context.getBean(StatusBarWrapper.class);
        rootLayoutController.getAdditionalSectionGridPane().add(statusBar.getStatusBar(), 0, 3);

        Scene scene = new Scene(rootLayoutViewHolder.getView());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {

        String appId = "StardexGUI";

        try {
            JUnique.acquireLock(appId);
            Thread.setDefaultUncaughtExceptionHandler(new LogbackUncaughtExceptionHandler());
            launchApp(args);
        } catch (AlreadyLockedException e) {
            JOptionPane.showMessageDialog(null, "Another instance of application is already running. Exiting.", "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(10);
        }

    }

}
