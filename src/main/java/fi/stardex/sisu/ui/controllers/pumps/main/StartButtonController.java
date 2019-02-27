package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestListModel;
import fi.stardex.sisu.model.PumpTestModeModel;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.Tests;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

public class StartButtonController {
    @FXML private ToggleButton startToggleButton;
    @FXML private HBox startHBox;
    private boolean startLight;

    private PumpsStartButtonState pumpsStartButtonState;
    private PumpTestListModel pumpTestListModel;
    private PumpTestModeModel pumpTestModeModel;

    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpTestModeModel(PumpTestModeModel pumpTestModeModel) {
        this.pumpTestModeModel = pumpTestModeModel;
    }

    public ToggleButton getStartToggleButton() {
        return startToggleButton;
    }
    public HBox getStartHBox() {
        return startHBox;
    }

    @PostConstruct
    public void init() {

        pumpsStartButtonState.startButtonProperty().bindBidirectional(startToggleButton.selectedProperty());
        initStartToggleButtonBlinking();

        pumpTestListModel.selectedTestIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != 0 && pumpTestModeModel.testModeProperty().get() == Tests.TestType.AUTO && !pumpsStartButtonState.startButtonProperty().get()) {
                startToggleButton.setDisable(true);
            }
            else{
                startToggleButton.setDisable(false);
            }
        });
    }

    private void initStartToggleButtonBlinking() {

        Timeline startButtonTimeline = new Timeline(new KeyFrame(Duration.millis(400), event -> startBlinking()));
        startButtonTimeline.setCycleCount(Animation.INDEFINITE);

        startToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                startButtonTimeline.play();
            else {
                startButtonTimeline.stop();
                startToggleButton.getStyleClass().clear();
                startToggleButton.getStyleClass().add("startButton");
            }
        });
    }

    private void startBlinking() {

        startToggleButton.getStyleClass().clear();

        if (startLight) {
            startToggleButton.getStyleClass().add("stopButtonDark");
            startLight = false;
        } else {
            startToggleButton.getStyleClass().add("stopButtonLight");
            startLight = true;
        }
    }
}
