package fi.stardex.sisu.main;

import fi.stardex.sisu.spring.JavaFXSpringConfigure;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LogoPreloader extends Preloader {

    private String version;
    private ProgressBar bar;
    private Stage stage;
    private Scene scene;
    private final Logger logger = LoggerFactory.getLogger(JavaFXSpringConfigure.class);

    public void init(){

        String fxmlPath = null;
        try{
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("properties/app.properties");
            if(resourceAsStream == null){
                throw new NullPointerException();
            }
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            version = properties.getProperty("stardex.version");
            fxmlPath = properties.getProperty("logo.preloader");
        } catch (Exception i){
            logger.info("Exception while load {}", "properties/app.properties - not found");
            i.printStackTrace();
            System.exit(10);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        try {
            fxmlLoader.load();
            LogoController logoController = fxmlLoader.getController();
            bar = logoController.getProgressBar();
            bar.setProgress(0);
            this.scene = new Scene(logoController.getRootBorderPane(), 600, 500);
            scene.setFill(Color.TRANSPARENT);
            logoController.getVersionLabel().setText(version);
        } catch (IOException e) {
            logger.info("Exception while load {}", fxmlPath);
            e.printStackTrace();
            System.exit(10);
        }
    }

    public void start(Stage stage) {
        this.stage = stage;
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        if(evt.getType().equals(StateChangeNotification.Type.BEFORE_START)){
            stage.hide();
        }
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        if (pn instanceof ProgressNotification) {
            bar.setProgress(((ProgressNotification) pn).getProgress());
        }
    }
}
