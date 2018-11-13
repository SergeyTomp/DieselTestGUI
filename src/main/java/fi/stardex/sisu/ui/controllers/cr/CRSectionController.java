package fi.stardex.sisu.ui.controllers.cr;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class CRSectionController {

    @FXML private StackPane highPressureSection;

    @FXML private HBox injectorSection;

    @FXML private HighPressureSectionController highPressureSectionController;

    @FXML private InjectorSectionController injectorSectionController;

    public HighPressureSectionController getHighPressureSectionController() {
        return highPressureSectionController;
    }

    public InjectorSectionController getInjectorSectionController() {
        return injectorSectionController;
    }

}
