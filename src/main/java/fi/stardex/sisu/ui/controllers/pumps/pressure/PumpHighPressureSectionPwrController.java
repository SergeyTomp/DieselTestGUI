package fi.stardex.sisu.ui.controllers.pumps.pressure;

import fi.stardex.sisu.states.PumpHighPressureSectionPwrState;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

public class PumpHighPressureSectionPwrController {
    @FXML private StackPane pwrButtonStackPane;
    @FXML private ToggleButton pwrButtonToggleButton;

    private PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState;

    public StackPane getPwrButtonStackPane() {
        return pwrButtonStackPane;
    }
    public ToggleButton getPwrButtonToggleButton() {
        return pwrButtonToggleButton;
    }

    public void setPumpHighPressureSectionPwrState(PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState) {
        this.pumpHighPressureSectionPwrState = pumpHighPressureSectionPwrState;
    }

    @PostConstruct
    public void init() {

        pumpHighPressureSectionPwrState.powerButtonProperty().bindBidirectional(pwrButtonToggleButton.selectedProperty());
    }
}
