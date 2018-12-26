package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpTestModeModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.Tests.TestType.AUTO;
import static fi.stardex.sisu.util.enums.Tests.TestType.TESTPLAN;
import static fi.stardex.sisu.util.enums.Tests.getTestType;
import static fi.stardex.sisu.util.enums.Tests.setTestType;

public class TestModeController {

    @FXML private GridPane testModeGridPane;
    @FXML private RadioButton autoTestRadioButton;
    @FXML private RadioButton testPlanTestRadioButton;

    private PumpTestModeModel pumpTestModeModel;

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

    @PostConstruct
    public void init(){
        autoTestRadioButton.setSelected(true);

        autoTestRadioButton.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue){
                pumpTestModeModel.testModeProperty().setValue(AUTO);
            } else pumpTestModeModel.testModeProperty().setValue(TESTPLAN);
        });
    }
}
