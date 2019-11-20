package fi.stardex.sisu.ui.controllers.pumps.pressure;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.states.PumpHighPressureSectionPwrState;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.GUI_type.CR_Pump;

public class PumpHighPressureSectionPwrController {
    @FXML private StackPane pwrButtonStackPane;
    @FXML private ToggleButton pwrButtonToggleButton;

    private PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState;
    private GUI_TypeModel gui_typeModel;

    public StackPane getPwrButtonStackPane() {
        return pwrButtonStackPane;
    }
    public ToggleButton getPwrButtonToggleButton() {
        return pwrButtonToggleButton;
    }

    public void setPumpHighPressureSectionPwrState(PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState) {
        this.pumpHighPressureSectionPwrState = pumpHighPressureSectionPwrState;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    @PostConstruct
    public void init() {

        pumpHighPressureSectionPwrState.powerButtonProperty().bindBidirectional(pwrButtonToggleButton.selectedProperty());

        gui_typeModel.guiTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == CR_Pump) {
                pwrButtonToggleButton.setSelected(false);
            }
        });
    }
}
