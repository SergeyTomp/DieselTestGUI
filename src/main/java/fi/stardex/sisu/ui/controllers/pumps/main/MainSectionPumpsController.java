package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.states.PumpSelectionState;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

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

    @FXML private VBox testSpeedVBox;

    @FXML private VBox startButtonVBox;

    @FXML private PumpsModelsListController pumpsModelsListVBoxController;

    @FXML private PumpsOEMListController pumpsOEMListListViewController;

    @FXML private PumpFieldController pumpFieldTextFieldController;

    @FXML private TestModeController testModeGridPaneController;

    @FXML private StoreResetPrintController storeResetPrintHBoxController;

    @FXML private TestListController testListHBoxController;

    @FXML private TestSpeedController testSpeedGridPaneController;

    @FXML private StartButtonController startButtonHBoxController;

    private PumpSelectionState pumpSelectionState;

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

    public void setPumpSelectionState(PumpSelectionState pumpSelectionState) {
        this.pumpSelectionState = pumpSelectionState;
    }

    @PostConstruct
    private void init() {

        BooleanProperty pumpSelectionProperty = pumpSelectionState.pumpSelectionProperty();

        pumpTestsVBox.visibleProperty().bind(pumpSelectionProperty);

        testSpeedVBox.visibleProperty().bind(pumpSelectionProperty);

        startButtonVBox.visibleProperty().bind(pumpSelectionProperty);

    }

}
