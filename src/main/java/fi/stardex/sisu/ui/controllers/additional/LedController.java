package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class LedController {

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

    private InjectorSectionController injectorSectionController;

    public boolean isSelected() {
        return ledBeaker.isSelected();
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

    @PostConstruct
    private void init() {
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        ledBeaker.getStyleClass().add(2, LED_BLINK_OFF);

        ledBeaker.selectedProperty().addListener((observable, oldValue, newValue) -> {
            getLoggingInjectorSelection(newValue);
            if (newValue) {
                injectorMask += 1 << injectorNumber;
                ledBeaker.getStyleClass().set(2, LED_BLINK_ON);
//                if (injectorSectionController.getPowerSwitch().isSelected()) {
//                    ledBlinkStart();
//                }

            } else {
                injectorMask -= 1 << injectorNumber;
                ledBeaker.getStyleClass().set(2, LED_BLINK_OFF);
//                if (injectorSectionController.getPowerSwitch().isSelected()) {
//                    ledBlinkStop();
//                }
            }
        });
    }

    private void getLoggingInjectorSelection(Boolean value) {
        String s = String.format("LedBeaker %s selected: %s", ledBeaker.getText(), value);
        logger.info(s);
    }

    public void setInjectorSectionController(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
    }

}
