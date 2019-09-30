package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.TabSectionModel;
import fi.stardex.sisu.states.HighPressureSectionPwrState;
import fi.stardex.sisu.ui.controllers.common.GUI_TypeController;
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
    private TabSectionModel tabSectionModel;
    private GUI_TypeModel gui_typeModel;

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
            if (oldValue == GUI_TypeController.GUIType.CR_Inj || oldValue == GUI_TypeController.GUIType.HEUI) {
                pwrButtonToggleButton.setSelected(false);
            }
        });
    }
}
