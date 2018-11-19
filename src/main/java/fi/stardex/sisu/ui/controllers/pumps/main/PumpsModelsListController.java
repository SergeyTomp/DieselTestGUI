package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.ManufacturerPumpModel;
import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.states.CustomPumpState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

public class PumpsModelsListController implements ChangeListener<Pump> {

    @FXML private VBox injectorsVBox;

    @FXML private ListView<Pump> modelListView;

    @FXML private TextField searchModelTextField;

    @FXML private RadioButton customRadioButton;

    @FXML private RadioButton defaultRadioButton;

    @FXML private ToggleGroup baseTypeToggleGroup;

    private ManufacturerPumpModel manufacturerPumpModel;

    private PumpModel pumpModel;

    private CustomPumpState customPumpState;

    private FilteredList<Pump> filteredModelList;

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

    public void setManufacturerPumpModel(ManufacturerPumpModel manufacturerPumpModel) {
        this.manufacturerPumpModel = manufacturerPumpModel;
    }

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    public void setCustomPumpState(CustomPumpState customPumpState) {
        this.customPumpState = customPumpState;
    }

    @PostConstruct
    private void init() {

        baseTypeToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {

            BooleanProperty customPumpProperty = customPumpState.customPumpProperty();

            customPumpProperty.setValue(newValue == customRadioButton);

            pumpModel.initPumpList(manufacturerPumpModel.manufacturerPumpProperty().get(), customPumpProperty.get());

        }));

        setupSearchModelTextField();

        pumpModel.getPumpObservableListProperty().addListener((observable, oldValue, newValue) -> {

            filteredModelList = new FilteredList<>(newValue, pump -> true);

            modelListView.setItems(filteredModelList);

        });

        modelListView.getSelectionModel().selectedItemProperty().addListener(this);

        manufacturerPumpModel.manufacturerPumpProperty().addListener((observableValue, oldValue, newValue) -> injectorsVBox.setDisable(newValue == null));

    }

    private void setupSearchModelTextField() {

        searchModelTextField.textProperty().addListener(((observable, oldValue, newValue) -> filteredModelList.setPredicate(data -> {

            if (newValue == null || newValue.isEmpty())
                return true;
            else {
                return (data.toString().contains(newValue.toUpperCase())
                        || data.toString().replace("-", "").contains(newValue.toUpperCase())
                        || data.toString().replace("#", "").contains(newValue.toUpperCase())
                        || data.toString().replaceFirst("-", "").contains(newValue.toUpperCase()));
            }

        })));

    }

    @Override
    public void changed(ObservableValue<? extends Pump> observableValue, Pump oldValue, Pump newValue) {

        pumpModel.pumpProperty().set(newValue);

    }
}
