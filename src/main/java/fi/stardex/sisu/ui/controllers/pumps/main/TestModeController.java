package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestModeModel;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;

public class TestModeController {

    @FXML private ToggleGroup testsToggleGroup;
    @FXML private GridPane testModeGridPane;
    @FXML private RadioButton autoTestRadioButton;
    @FXML private RadioButton testPlanTestRadioButton;

    private I18N i18N;
    private PumpTestModeModel pumpTestModeModel;
    private PumpsStartButtonState pumpsStartButtonState;

    public RadioButton getAutoTestRadioButton() {
        return autoTestRadioButton;
    }
    public RadioButton getTestPlanTestRadioButton() {
        return testPlanTestRadioButton;
    }
    public GridPane getTestModeGridPane() {
        return testModeGridPane;
    }

    public void setPumpTestModeModel(PumpTestModeModel pumpTestModeModel) {
        this.pumpTestModeModel = pumpTestModeModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }

    @PostConstruct
    public void init(){
        autoTestRadioButton.setSelected(true);

        autoTestRadioButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                pumpTestModeModel.testModeProperty().setValue(AUTO);
            } else pumpTestModeModel.testModeProperty().setValue(TESTPLAN);
        });

        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) ->
                testsToggleGroup
                        .getToggles()
                        .stream()
                        .filter(toggle -> !toggle.isSelected())
                        .forEach(radioButton -> ((Node)radioButton).setDisable(newValue)));

        bindingI18N();
    }

    private void bindingI18N(){

        autoTestRadioButton.textProperty().bind(i18N.createStringBinding("main.auto.radiobutton"));
        testPlanTestRadioButton.textProperty().bind(i18N.createStringBinding("main.testPlan.radiobutton"));
    }
}
