package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainSectionPumpsController {

    @FXML private HBox startButtonHBox;
    @FXML private GridPane testSpeedGridPane;
    @FXML private HBox storeResetPrintHBox;
    @FXML private TextField pumpFieldTextField;
    @FXML private GridPane testModeGridPane;
    @FXML private HBox testListHBox;
    @FXML private GridPane pumpSelectGridPane;
    @FXML private ListView pumpsOEMListListView;
    @FXML private VBox pumpsModelsListVBox;
    @FXML private VBox pumpTestsVBox;

    @FXML private PumpsModelsListController pumpsModelsListVBoxController;
    @FXML private PumpsOEMListController pumpsOEMListListViewController;
    @FXML private PumpFieldController pumpFieldTextFieldController;
    @FXML private TestModeController testModeGridPaneController;
    @FXML private StoreResetPrintController storeResetPrintHBoxController;
    @FXML private TestListController testListHBoxController;
    @FXML private TestSpeedController testSpeedGridPaneController;
    @FXML private StartButtonController startButtonHBoxController;


    public GridPane getRootGridPane() {
        return pumpSelectGridPane;
    }

    public ListView getOemListViev() {
        return pumpsOEMListListView;
    }

    public VBox getModelsVBox() {
        return pumpsModelsListVBox;
    }

    public VBox getPumpTestsVBox() {
        return pumpTestsVBox;
    }

    public TextField getPumpFieldTextField() {
        return pumpFieldTextField;
    }

    public GridPane getTestModeGridPane() {
        return testModeGridPane;
    }

    public HBox getTestListHBox() {
        return testListHBox;
    }

    public HBox getStoreResetPrintHBox() {
        return storeResetPrintHBox;
    }

    public GridPane getTestSpeedGridPane() {
        return testSpeedGridPane;
    }

    public HBox getStartButtonHBox() {
        return startButtonHBox;
    }

    public PumpFieldController getPumpFieldController() {
        return pumpFieldTextFieldController;
    }

    public TestModeController getTestModeController() {
        return testModeGridPaneController;
    }

    public StoreResetPrintController getStoreResetPrintController() {
        return storeResetPrintHBoxController;
    }

    public TestListController getTestListController() {
        return testListHBoxController;
    }

    public PumpsModelsListController getPumpsModelsListController() {
        return pumpsModelsListVBoxController;
    }

    public PumpsOEMListController getPumpsOEMListController() {
        return pumpsOEMListListViewController;
    }

    public TestSpeedController getTestSpeedController() {
        return testSpeedGridPaneController;
    }

    public StartButtonController getStartButtonController() {
        return startButtonHBoxController;
    }
}
