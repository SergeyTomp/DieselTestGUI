package fi.stardex.sisu.ui.controllers.cr;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class CRSectionController {

    @FXML
    private HBox testBenchSection;

    @FXML
    private StackPane highPressureSection;

    @FXML
    private HBox injectorSection;

    @FXML
    private TestBenchSectionController testBenchSectionController;

    @FXML
    private HighPressureSectionController highPressureSectionController;

    @FXML
    private InjectorSectionController injectorSectionController;


    public TestBenchSectionController getTestBenchSectionController() {
        return testBenchSectionController;
    }

    public HighPressureSectionController getHighPressureSectionController() {
        return highPressureSectionController;
    }

    public InjectorSectionController getInjectorSectionController() {
        return injectorSectionController;
    }
}
