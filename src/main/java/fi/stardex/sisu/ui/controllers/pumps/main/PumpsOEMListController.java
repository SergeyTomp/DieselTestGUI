package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.ManufacturerPumpModel;
import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.model.PumpReportModel;
import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.states.CustomPumpState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javax.annotation.PostConstruct;

public class PumpsOEMListController implements ListChangeListener<ManufacturerPump>, ChangeListener<ManufacturerPump> {

    @FXML private ListView<ManufacturerPump> oemListView;

    private ManufacturerPumpModel manufacturerPumpModel;
    private PumpModel pumpModel;
    private PumpReportModel pumpReportModel;
    private PumpsStartButtonState pumpsStartButtonState;

    public void setManufacturerPumpModel(ManufacturerPumpModel manufacturerPumpModel) {
        this.manufacturerPumpModel = manufacturerPumpModel;
    }
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }
    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }

    @PostConstruct
    private void init() {

        manufacturerPumpModel.getManufacturerPumpObservableList().addListener(this);
        oemListView.getSelectionModel().selectedItemProperty().addListener(this);
        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> oemListView.setDisable(newValue));
    }

    @Override
    public void onChanged(Change<? extends ManufacturerPump> change) {

        oemListView.getItems().setAll(change.getList());
    }

    @Override
    public void changed(ObservableValue<? extends ManufacturerPump> observableValue, ManufacturerPump oldValue, ManufacturerPump newValue) {

        manufacturerPumpModel.manufacturerPumpProperty().set(newValue);
        pumpModel.initPumpList();
        pumpReportModel.clearResults();
    }
}
