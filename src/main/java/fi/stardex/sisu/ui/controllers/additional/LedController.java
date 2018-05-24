package fi.stardex.sisu.ui.controllers.additional;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class LedController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(LedController.class);

    private static final String LED_BLINK_ON = "ledBlink-on";
    private static final String LED_BLINK_OFF = "ledBlink-off";

    private boolean piezoDelphiMode;
    private int number;
    private int injectorNumber;
    private boolean blink;
    private static volatile int injectorMask;

    @FXML
    public ToggleButton ledBeaker;
    @FXML
    public AnchorPane anchorPaneLed;

    private Timeline timeline = new Timeline();

    private KeyFrame keyFrame = new KeyFrame(Duration.millis(500), event -> {
        if (blink) {
            ledBeaker.getStyleClass().set(2, LED_BLINK_OFF);
            blink = false;
        } else {
            ledBeaker.getStyleClass().set(2, LED_BLINK_ON);
            blink = true;
        }
    });

    public boolean isSelected() {
        return ledBeaker.isSelected();
    }

    public void setDisable(boolean disabled) {
        ledBeaker.setDisable(disabled);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        ledBeaker.getStyleClass().add(2, LED_BLINK_OFF);

        ledBeaker.selectedProperty().addListener((observable, oldValue, newValue) -> {
            getLoggingInjectorSelection(newValue);
            if (newValue) {
                ledBeaker.getStyleClass().set(2, LED_BLINK_ON);
            } else {
                ledBeaker.getStyleClass().set(2, LED_BLINK_OFF);
            }
        });
    }

    public void setVisible(boolean visible) {
        if (ledBeaker.isSelected() && !visible) {
            ledBeaker.setSelected(false);
        }
        anchorPaneLed.setVisible(visible);
    }

    public void setPiezoDelphiMode(boolean piezoDelphiMode) {
        this.piezoDelphiMode = piezoDelphiMode;
        ledBeaker.setDisable(piezoDelphiMode);
    }

    public ToggleButton getLedBeaker() {
        return ledBeaker;
    }

    public void ledBlinkStart() {
        if (ledBeaker.isSelected()) {
            blink = true;
            timeline.play();
        }
    }

    public void ledBlinkStop() {
        if (!piezoDelphiMode) {
            if (ledBeaker.isSelected()) {
                blink = false;
                ledBeaker.getStyleClass().set(2, LED_BLINK_ON);
                ledBeaker.setDisable(false);
                timeline.stop();
            } else {
                blink = false;
                ledBeaker.getStyleClass().set(2, LED_BLINK_OFF);
                ledBeaker.setDisable(false);
                timeline.stop();
            }
        }
    }

    public void setNumber(int number) {
        this.number = number;
        injectorNumber = number - 1;
        ledBeaker.setText(String.valueOf(number));
    }

    public int getNumber() {
        return number;
    }



    private void getLoggingInjectorSelection(Boolean value) {
        String s = String.format("LedBeaker %s selected: %s", ledBeaker.getText(), value);
        logger.info(s);
    }


}
