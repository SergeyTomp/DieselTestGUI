package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

public class MainSectionPumpsController implements ChangeListener<Pump> {

    @FXML private HBox startButtonHBox;

    @FXML private GridPane testSpeedGridPane;

    @FXML private HBox storeResetPrintHBox;

    @FXML private TextField pumpFieldTextField;

    @FXML private GridPane testModeGridPane;

    @FXML private HBox pumpTestList;

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

    @FXML private PumpTestListController pumpTestListController;

    @FXML private TestSpeedController testSpeedGridPaneController;

    @FXML private StartButtonController startButtonHBoxController;

    private PumpModel pumpModel;

    public PumpFieldController getPumpFieldController() {
        return pumpFieldTextFieldController;
    }

    public TestModeController getTestModeController() {
        return testModeGridPaneController;
    }

    public StoreResetPrintController getStoreResetPrintController() {
        return storeResetPrintHBoxController;
    }

    public PumpTestListController getPumpTestListController() {
        return pumpTestListController;
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

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    @PostConstruct
    private void init() {

        pumpModel.pumpProperty().addListener(this);

    }

    @Override
    public void changed(ObservableValue<? extends Pump> observableValue, Pump oldValue, Pump newValue) {

        pumpTestsVBox.setVisible(newValue != null);

        testSpeedVBox.setVisible(newValue != null);

        startButtonVBox.setVisible(newValue != null);

    }

}
