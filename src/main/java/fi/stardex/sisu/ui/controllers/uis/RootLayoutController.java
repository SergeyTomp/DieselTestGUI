package fi.stardex.sisu.ui.controllers.uis;

import fi.stardex.sisu.ui.controllers.GUI_TypeController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class RootLayoutController {

    @FXML
    private GridPane mainSectionGridPane;

    @FXML
    private GridPane additionalSectionGridPane;

    @FXML
    private HBox gui_type;

    @FXML
    private HBox testBenchSectionHBox;

    @FXML
    private TestBenchSectionController testBenchSectionHBoxController;

    @FXML
    private GUI_TypeController gui_typeController;

    public TestBenchSectionController getTestBenchSectionController() {
        return testBenchSectionHBoxController;
    }

    public GUI_TypeController getGui_typeController() {
        return gui_typeController;
    }

    public GridPane getMainSectionGridPane() {
        return mainSectionGridPane;
    }

    public GridPane getAdditionalSectionGridPane() {
        return additionalSectionGridPane;
    }

}
