package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.states.ManufacturerPumpSelectionState;
import fi.stardex.sisu.states.PumpSelectionState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;
import java.util.Optional;

public class PumpsModelsListController implements ListChangeListener<Pump>, ChangeListener<Pump> {

    @FXML private VBox injectorsVBox;

    @FXML private ListView<Pump> modelListView;

    @FXML private TextField searchModelTextField;

    @FXML private RadioButton customRadioButton;

    @FXML private RadioButton defaultRadioButton;

    @FXML private ToggleGroup baseTypeToggleGroup;

    private PumpModel pumpModel;

    private ManufacturerPumpSelectionState manufacturerPumpSelectionState;

    private PumpSelectionState pumpSelectionState;

    public VBox getInjectorsVBox() {
        return injectorsVBox;
    }

    public ListView<Pump> getModelListView() {
        return modelListView;
    }

    public TextField getSearchModelTextField() {
        return searchModelTextField;
    }

    public RadioButton getCustomRadioButton() {
        return customRadioButton;
    }

    public RadioButton getDefaultRadioButton() {
        return defaultRadioButton;
    }

    public ToggleGroup getBaseTypeToggleGroup() {
        return baseTypeToggleGroup;
    }

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    public void setManufacturerPumpSelectionState(ManufacturerPumpSelectionState manufacturerPumpSelectionState) {
        this.manufacturerPumpSelectionState = manufacturerPumpSelectionState;
    }

    public void setPumpSelectionState(PumpSelectionState pumpSelectionState) {
        this.pumpSelectionState = pumpSelectionState;
    }

    @PostConstruct
    private void init() {

        pumpModel.getPumpObservableList().addListener(this);

        modelListView.getSelectionModel().selectedItemProperty().addListener(this);

        injectorsVBox.disableProperty().bind(manufacturerPumpSelectionState.manufacturerPumpSelectionProperty().not());

    }

    @Override
    public void onChanged(Change<? extends Pump> change) {

        modelListView.getItems().setAll(change.getList());

    }

    @Override
    public void changed(ObservableValue<? extends Pump> observableValue, Pump oldValue, Pump newValue) {

        pumpSelectionState.pumpSelectionProperty().setValue(newValue != null);

        pumpModel.pumpCodeProperty().setValue(newValue != null ? newValue.getPumpCode() : "");

    }
}
