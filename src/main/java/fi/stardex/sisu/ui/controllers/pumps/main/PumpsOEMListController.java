package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.pump.CustomPumpProducerDialogModel;
import fi.stardex.sisu.model.pump.ManufacturerPumpModel;
import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.model.pump.PumpReportModel;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.pump.ManufacturerPump;
import fi.stardex.sisu.persistence.repos.pump.PumpProducerService;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.GUI_type;
import fi.stardex.sisu.util.enums.Operation;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

public class PumpsOEMListController {

    @FXML private ListView<ManufacturerPump> oemListView;

    private ManufacturerPumpModel manufacturerPumpModel;
    private PumpModel pumpModel;
    private PumpReportModel pumpReportModel;
    private PumpsStartButtonState pumpsStartButtonState;
    private GUI_TypeModel gui_typeModel;
    //TODO: replace CustomPumpProducerDialogModel by unique CustomProducerDialogModel after implementation of MainSectionUisController as a unique one for all GUI types.
    private CustomPumpProducerDialogModel customPumpProducerDialogModel;
    private PumpProducerService producerService;

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
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setCustomPumpProducerDialogModel(CustomPumpProducerDialogModel customPumpProducerDialogModel) {
        this.customPumpProducerDialogModel = customPumpProducerDialogModel;
    }
    public void setProducerService(PumpProducerService producerService) {
        this.producerService = producerService;
    }

    @PostConstruct
    private void init() {

        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> oemListView.setDisable(newValue));
        setupOemListViewListener();
        setupCustomProducerDialogModelListener();
        initManufacturerContextMenu();
        setupGuiTypeListener();
    }

    private void setupOemListViewListener() {

        oemListView.getItems().addListener((ListChangeListener<Producer>) change ->
                manufacturerPumpModel.getManufacturerPumpObservableList().setAll(oemListView.getItems()));

        oemListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            manufacturerPumpModel.manufacturerPumpProperty().set(newValue);
            pumpModel.initPumpList();
            pumpReportModel.clearResults();
        });
    }

    private void setupGuiTypeListener() {

        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == GUI_type.CR_Pump) {
                oemListView.getItems().setAll(new ArrayList<>(producerService.findAll()));
            }
            oemListView.getSelectionModel().clearSelection();
            oemListView.getFocusModel().focus(0);
        });
    }

    private void setupCustomProducerDialogModelListener() {

        customPumpProducerDialogModel.doneProperty().addListener((observableValue, oldValue, newValue) -> {

            oemListView.getItems().setAll(new ArrayList<>(producerService.findAll()));
            oemListView.refresh();
            ManufacturerPump producer = oemListView.getItems().stream().filter(m -> m.equals(customPumpProducerDialogModel.customProducerProperty().get())).findFirst().orElse(null);
            oemListView.getSelectionModel().select(producer);
            manufacturerPumpModel.customProducerOperationProperty().setValue(null);
        });

        customPumpProducerDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                manufacturerPumpModel.customProducerOperationProperty().setValue(null));
    }

    private void initManufacturerContextMenu() {

        ContextMenu manufacturerMenu = new ContextMenu();
        MenuItem newManufacturer = new MenuItem("New");
        newManufacturer.setOnAction(actionEvent -> manufacturerPumpModel.customProducerOperationProperty().setValue(Operation.NEW));
        MenuItem deleteManufacturer = new MenuItem("Delete");
        deleteManufacturer.setOnAction(actionEvent -> manufacturerPumpModel.customProducerOperationProperty().setValue(Operation.DELETE));

        oemListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                manufacturerMenu.getItems().clear();
                manufacturerMenu.getItems().add(newManufacturer);
                if (manufacturerPumpModel.manufacturerPumpProperty().get() != null &&
                        manufacturerPumpModel.manufacturerPumpProperty().get().isCustom())
                    manufacturerMenu.getItems().addAll(deleteManufacturer);
                manufacturerMenu.show(oemListView, event.getScreenX(), event.getScreenY());
            } else
                manufacturerMenu.hide();
        });

        customPumpProducerDialogModel.cancelProperty().addListener((observableValue, oldValue, newValue) ->
                manufacturerPumpModel.customProducerOperationProperty().setValue(null));
    }
}
