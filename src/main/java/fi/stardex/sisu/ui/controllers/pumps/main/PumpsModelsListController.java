package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.states.CustomPumpState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    private PumpTestModel pumpTestModel;
    private CustomPumpState customPumpState;
    private FilteredList<Pump> filteredModelList;
    private PumpTestListModel pumpTestListModel;
    private PumpReportModel pumpReportModel;
    private I18N i18N;
    private PumpsStartButtonState pumpsStartButtonState;

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
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }

    @PostConstruct
    private void init() {

        setupSearchModelTextField();
        setupListeners();
        bindingI18N();
    }

    @Override
    public void changed(ObservableValue<? extends Pump> observableValue, Pump oldValue, Pump newValue) {

        pumpModel.pumpProperty().set(newValue);
        pumpTestListModel.initPumpTestList();
        pumpReportModel.clearResults();
    }

    private void setupListeners() {

        baseTypeToggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {

            customPumpState.customPumpProperty().setValue(newValue == customRadioButton);
            pumpModel.initPumpList();

        }));
        pumpModel.getPumpObservableListProperty().addListener((observable, oldValue, newValue) -> {

            filteredModelList = new FilteredList<>(newValue, pump -> true);
            modelListView.setItems(filteredModelList);

        });

        modelListView.getSelectionModel().selectedItemProperty().addListener(this);
        manufacturerPumpModel.manufacturerPumpProperty().addListener((observableValue, oldValue, newValue) -> injectorsVBox.setDisable(newValue == null));
        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newNalue) -> {
            modelListView.setDisable(newNalue);
            searchModelTextField.setDisable(newNalue);
            baseTypeToggleGroup.getToggles().stream().filter(toggle -> !toggle.isSelected()).forEach(radioButton -> ((Node)radioButton).setDisable(newNalue));
        });
    }

    private void setupSearchModelTextField() {

        searchModelTextField.textProperty().addListener(((observable, oldValue, newValue) -> filteredModelList.setPredicate(data -> {

            if (newValue == null || newValue.isEmpty())
                return true;
            else {
                return (data.toString().contains(newValue.toUpperCase())
                        || data.toString().replace("-", "").contains(newValue.toUpperCase())
                        || data.toString().replace("#", "").contains(newValue.toUpperCase())
                        || data.toString().replace(" ", "").contains(newValue.toUpperCase())
                        || data.toString().replaceFirst("-", "").contains(newValue.toUpperCase()));
            }

        })));
    }

    private void bindingI18N(){

        defaultRadioButton.textProperty().bind(i18N.createStringBinding("main.defaultRB.radiobutton"));
        customRadioButton.textProperty().bind(i18N.createStringBinding("main.customRB.radiobutton"));
    }
}
