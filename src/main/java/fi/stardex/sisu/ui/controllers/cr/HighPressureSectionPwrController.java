package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.states.HighPressureSectionPwrState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

public class HighPressureSectionPwrController {

    @FXML private StackPane pwrButtonStackPane;
    @FXML private ToggleButton pwrButtonToggleButton;
    private HighPressureSectionPwrState highPressureSectionPwrState;

    public StackPane getPwrButtonStackPane() {
        return pwrButtonStackPane;
    }

    public ToggleButton getPwrButtonToggleButton() {
        return pwrButtonToggleButton;
    }

    public void setHighPressureSectionPwrState(HighPressureSectionPwrState highPressureSectionPwrState) {
        this.highPressureSectionPwrState = highPressureSectionPwrState;
    }

    @PostConstruct
    public void init(){
        highPressureSectionPwrState.powerButtonProperty().addListener((observableValue, oldValue, neValue) -> pwrButtonToggleButton.setSelected(neValue));
        highPressureSectionPwrState.powerButtonProperty().bindBidirectional(pwrButtonToggleButton.selectedProperty());
    }
}
