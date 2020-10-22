package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.TabSectionModel;
import fi.stardex.sisu.model.cr.PressureRegulatorOneModel;
import fi.stardex.sisu.states.HighPressureSectionPwrState;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.GUI_type.CR_Inj;
import static fi.stardex.sisu.util.enums.GUI_type.HEUI;

public class HighPressureSectionPwrController {

    @FXML private StackPane pwrButtonStackPane;
    @FXML private ToggleButton pwrButtonToggleButton;

    private HighPressureSectionPwrState highPressureSectionPwrState;
    private TabSectionModel tabSectionModel;
    private GUI_TypeModel gui_typeModel;
    private PressureRegulatorOneModel pressureRegulatorOneModel;

    public StackPane getPwrButtonStackPane() {
        return pwrButtonStackPane;
    }
    public ToggleButton getPwrButtonToggleButton() {
        return pwrButtonToggleButton;
    }

    public void setHighPressureSectionPwrState(HighPressureSectionPwrState highPressureSectionPwrState) {
        this.highPressureSectionPwrState = highPressureSectionPwrState;
    }
    public void setTabSectionModel(TabSectionModel tabSectionModel) {
        this.tabSectionModel = tabSectionModel;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setPressureRegulatorOneModel(PressureRegulatorOneModel pressureRegulatorOneModel) {
        this.pressureRegulatorOneModel = pressureRegulatorOneModel;
    }

    @PostConstruct
    public void init(){
        highPressureSectionPwrState.powerButtonProperty().bindBidirectional(pwrButtonToggleButton.selectedProperty());
        tabSectionModel.piezoTabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> {
            pwrButtonToggleButton.setDisable(newValue);
            if (newValue) {
                pwrButtonToggleButton.setSelected(false);
            }
        });
        tabSectionModel.step3TabIsShowingProperty().addListener((observableValue, oldValue, newValue) -> {
            pwrButtonToggleButton.setDisable(newValue);
            if (newValue) {
                pwrButtonToggleButton.setSelected(false);
            }
        });

        gui_typeModel.guiTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == CR_Inj || oldValue == HEUI) {
                pwrButtonToggleButton.setSelected(false);
            }
        });

        pressureRegulatorOneModel.overPressureProperty().addListener((observable, oldValue, newValue) -> {

            if (pwrButtonToggleButton.isSelected() && newValue) {
                pwrButtonToggleButton.setSelected(false);
            }
        });
    }
}
