package fi.stardex.sisu.ui.controllers.cr;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class CRSectionController {

    @FXML private StackPane injectorHighPressureSection;

    @FXML private HBox injectorSection;

    @FXML private InjectorHighPressureSectionController injectorHighPressureSectionController;

    @FXML private InjectorSectionController injectorSectionController;

    public InjectorSectionController getInjectorSectionController() {
        return injectorSectionController;
    }

    public InjectorHighPressureSectionController getInjectorHighPressureSectionController() {
        return injectorHighPressureSectionController;
    }
}
