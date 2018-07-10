package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditInjectorDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditTestDialogController;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.view.GUIType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MainSectionController {

    @FXML
    private ToggleGroup testsToggleGroup;

    @FXML
    private TextField injNumberTextField;

    @FXML
    private RadioButton manualTestRadioButton;

    @FXML
    private RadioButton testPlanTestRadioButton;

    @FXML
    private RadioButton autoTestRadioButton;

    @FXML
    private RadioButton codingTestRadioButton;

    @FXML
    private ToggleGroup baseType;
    @FXML
    private RadioButton defaultRB;
    @FXML
    private RadioButton customRB;

    private List<String> versions = new LinkedList<>();

    {
        versions.add("CR Injectors");
        versions.add("CR Pumps");
        versions.add("UIS");
    }


    @FXML
    private ComboBox<String> versionComboBox;

    @FXML
    private ListView<Manufacturer> manufacturerListView;
    @FXML
    private TextField searchModelTF;
    @FXML
    private ListView<Model> modelListView;
    @FXML
    private ListView<InjectorTest> testListView;

    @Autowired
    private Enabler enabler;
    @Autowired
    private CurrentManufacturerObtainer currentManufacturerObtainer;
    @Autowired
    private ApplicationConfigHandler applicationConfigHandler;
    @Autowired
    private ApplicationAppearanceChanger applicationAppearanceChanger;
    @Autowired
    private ViewHolder manufacturerMenuDialog;
    @Autowired
    private ViewHolder newEditInjectorDialog;
    @Autowired
    private ViewHolder newEditTestDialog;
    @Autowired
    private VoltAmpereProfileController voltAmpereProfileController;
    @Autowired
    private DataConverter firmwareDataConverter;
    @Autowired
    private CurrentInjectorObtainer currentInjectorObtainer;

    private Stage manufacturerDialogStage;
    private Stage modelDialogStage;
    private Stage testDialogStage;

    public ListView<Manufacturer> getManufacturerListView() {
        return manufacturerListView;
    }

    public ListView<Model> getModelListView() {
        return modelListView;
    }

    public ToggleGroup getTestsToggleGroup() {
        return testsToggleGroup;
    }

    public RadioButton getManualTestRadioButton() {
        return manualTestRadioButton;
    }

    public RadioButton getCodingTestRadioButton() {
        return codingTestRadioButton;
    }

    public RadioButton getAutoTestRadioButton() {
        return autoTestRadioButton;
    }

    public RadioButton getTestPlanTestRadioButton() {
        return testPlanTestRadioButton;
    }

    public ListView<InjectorTest> getTestListView() {
        return testListView;
    }

    @PostConstruct
    private void init() {

        // TODO: do not delete!
//        setupStartStopToggleButton();
//        setupResetButton()

        versionComboBox.getItems().addAll(versions);

        versionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            switch (newValue) {
                case "UIS":
                    applicationAppearanceChanger.changeToUIS();
                    break;
                case "CR Injectors":
                    applicationAppearanceChanger.changeToCRInj();
                    break;
                case "CR Pumps":
                    applicationAppearanceChanger.changeToCRPump();
                    break;
            }
        });

        switch (GUIType.getByString(applicationConfigHandler.get("GUI_Type"))) {
            case UIS:
                versionComboBox.getSelectionModel().select("UIS");
                break;
            case CR_Inj:
                versionComboBox.getSelectionModel().select("CR Injectors");
                break;
            case CR_Pump:
                versionComboBox.getSelectionModel().select("CR Pumps");
        }

        initManufacturerContextMenu();
        initModelContextMenu();
        initTestContextMenu();

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            currentManufacturerObtainer.setCurrentManufacturer(newValue);

            if (newValue.isCustom()) {
                defaultRB.setDisable(true);
                customRB.setSelected(true);
            } else
                defaultRB.setDisable(false);

            switch (GUIType.getCurrentType()) {
                case CR_Inj:
                    modelListView.getItems().setAll(newValue.getInjectors(customRB.isSelected()));
                    break;
                case CR_Pump:
                    //TODO
                    System.err.println("IN DEVELOPMENT");
                    break;
                case UIS:
                    //TODO
                    System.err.println("IN DEVELOPMENT");
                    break;
            }
        }));

        baseType.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            Manufacturer selectedItem = manufacturerListView.getSelectionModel().getSelectedItem();

            if (selectedItem == null)
                return;

            modelListView.getItems().setAll(selectedItem.getInjectors(customRB.isSelected()));
        }));

        modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null) {
                enabler.selectInjector(false);
                return;
            }

            Injector inj = (Injector) newValue;
            currentInjectorObtainer.setInjector(inj);

            enabler.selectInjector(true);

            System.err.println(inj.getVoltAmpereProfile().getInjectorType().getInjectorType());
            VoltAmpereProfile voltAmpereProfile = inj.getVoltAmpereProfile();

            Double firstI = voltAmpereProfile.getFirstI();
            Double secondI = voltAmpereProfile.getSecondI();
            Double boostI = voltAmpereProfile.getBoostI();

            voltAmpereProfileController.getBoostUSpinner().getValueFactory().setValue(voltAmpereProfile.getBoostU());
            voltAmpereProfileController.getFirstWSpinner().getValueFactory().setValue(voltAmpereProfile.getFirstW());
            voltAmpereProfileController.getFirstISpinner().getValueFactory().setValue((firstI * 100 % 10 != 0) ? firmwareDataConverter.roundToOneDecimalPlace(firstI) : firstI);
            voltAmpereProfileController.getSecondISpinner().getValueFactory().setValue((secondI * 100 % 10 != 0) ? firmwareDataConverter.roundToOneDecimalPlace(secondI) : secondI);
            voltAmpereProfileController.getBoostISpinner().getValueFactory().setValue((boostI * 100 % 10 != 0) ? firmwareDataConverter.roundToOneDecimalPlace(boostI) : boostI);
            voltAmpereProfileController.getBatteryUSpinner().getValueFactory().setValue(voltAmpereProfile.getBatteryU());
            voltAmpereProfileController.getNegativeUSpinner().getValueFactory().setValue(voltAmpereProfile.getNegativeU());
            voltAmpereProfileController.getEnableBoostToggleButton().setSelected(voltAmpereProfile.getBoostDisable());

            InjectorSectionController injectorSectionController = voltAmpereProfileController.getInjectorSectionController();

            switch (voltAmpereProfile.getInjectorType().getInjectorType()) {
                case "coil":
                    injectorSectionController.getCoilRadioButton().setSelected(true);
                    break;
                case "piezo":
                    injectorSectionController.getPiezoRadioButton().setSelected(true);
                    break;
                case "piezoDelphi":
                    injectorSectionController.getPiezoDelphiRadioButton().setSelected(true);
                    break;
                default:
                    throw new IllegalArgumentException("Wrong injector type parameter!");
            }

            voltAmpereProfileController.getApplyButton().fire();
        });

        testsToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == manualTestRadioButton)
                enabler.selectTest(Tests.MANUAL);
            else if (newValue == testPlanTestRadioButton)
                enabler.selectTest(Tests.TESTPLAN);
            else if (newValue == autoTestRadioButton)
                enabler.selectTest(Tests.AUTO);
            else if (newValue == codingTestRadioButton)
                enabler.selectTest(Tests.CODING);

        });

    }

    private void initManufacturerContextMenu() {
        ContextMenu manufacturerMenu = new ContextMenu();
        MenuItem newManufacturer = new MenuItem("New");
        newManufacturer.setOnAction(new ManufacturerMenuEventHandler("New manufacturer", ManufacturerMenuDialogController::setNew));
        MenuItem deleteManufacturer = new MenuItem("Delete");
        deleteManufacturer.setOnAction(new ManufacturerMenuEventHandler("Delete manufacturer", ManufacturerMenuDialogController::setDelete));

        manufacturerListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                manufacturerMenu.getItems().clear();
                manufacturerMenu.getItems().add(newManufacturer);
                if (currentManufacturerObtainer.getCurrentManufacturer() != null &&
                        currentManufacturerObtainer.getCurrentManufacturer().isCustom())
                    manufacturerMenu.getItems().addAll(deleteManufacturer);
                manufacturerMenu.show(manufacturerListView, event.getScreenX(), event.getScreenY());
            } else
                manufacturerMenu.hide();
        });
    }

    private void initModelContextMenu() {
        ContextMenu modelMenu = new ContextMenu();
        MenuItem newModel = new MenuItem("New");
        newModel.setOnAction(new ModelMenuEventHandler("New injector", NewEditInjectorDialogController::setNew));
        MenuItem editModel = new MenuItem("Edit");
        editModel.setOnAction(new ModelMenuEventHandler("Edit injector", NewEditInjectorDialogController::setEdit));
        MenuItem copyModel = new MenuItem("Copy");

        MenuItem deleteModel = new MenuItem("Delete");
        deleteModel.setOnAction(new ModelMenuEventHandler("Delete injector", NewEditInjectorDialogController::setDelete));

        //TODO
        modelListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                modelMenu.getItems().clear();
                if (defaultRB.isSelected()) {
                    modelMenu.getItems().add(copyModel);
                } else {
                    modelMenu.getItems().add(newModel);
                    if (modelListView.getSelectionModel().getSelectedItem() != null)
                        modelMenu.getItems().addAll(copyModel, editModel, deleteModel);
                }
                modelMenu.show(modelListView, event.getScreenX(), event.getScreenY());
            } else
                modelMenu.hide();
        });
    }

    private void initTestContextMenu() {
        ContextMenu testMenu = new ContextMenu();
        MenuItem newTest = new MenuItem("New");
        newTest.setOnAction(new TestMenuEventHandler("New test", NewEditTestDialogController::setNew));
        MenuItem editTest = new MenuItem("Edit");
        editTest.setOnAction(new TestMenuEventHandler("Edit test", NewEditTestDialogController::setEdit));
        MenuItem deleteTest = new MenuItem("Delete");
        deleteTest.setOnAction(new TestMenuEventHandler("Delete test", NewEditTestDialogController::setDelete));

        testMenu.getItems().add(newTest);
        testListView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                testMenu.show(testListView, event.getScreenX(), event.getScreenY());
            } else
                testMenu.hide();
        });

    }
    private class ManufacturerMenuEventHandler implements EventHandler<ActionEvent> {

        private String title;
        private Consumer<ManufacturerMenuDialogController> dialogType;

        public ManufacturerMenuEventHandler(String title, Consumer<ManufacturerMenuDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            if (manufacturerDialogStage == null) {
                manufacturerDialogStage = new Stage();
                manufacturerDialogStage.setScene(new Scene(manufacturerMenuDialog.getView(), 200, 130));
                manufacturerDialogStage.setResizable(false);
                manufacturerDialogStage.initModality(Modality.APPLICATION_MODAL);
//                manufacturerDialogStage.initStyle(StageStyle.UNDECORATED);
                ((ManufacturerMenuDialogController) manufacturerMenuDialog.getController()).setStage(manufacturerDialogStage);
            }
            manufacturerDialogStage.setTitle(title);
            dialogType.accept((ManufacturerMenuDialogController) manufacturerMenuDialog.getController());
            manufacturerDialogStage.show();
        }
    }

    private class ModelMenuEventHandler implements EventHandler<ActionEvent> {
        private String title;
        private Consumer<NewEditInjectorDialogController> dialogType;

        public ModelMenuEventHandler(String title, Consumer<NewEditInjectorDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            NewEditInjectorDialogController controller = (NewEditInjectorDialogController) newEditInjectorDialog.getController();
            if (modelDialogStage == null) {
                modelDialogStage = new Stage();
                modelDialogStage.setScene(new Scene(newEditInjectorDialog.getView(), 600, 400));
                modelDialogStage.setResizable(false);
                modelDialogStage.initModality(Modality.APPLICATION_MODAL);
//                manufacturerDialogStage.initStyle(StageStyle.UNDECORATED);
                controller.setStage(modelDialogStage);
                controller.setModelListView(modelListView);

            }
            modelDialogStage.setTitle(title);
            dialogType.accept(controller);
            modelDialogStage.show();
        }
    }

    private class TestMenuEventHandler implements EventHandler<ActionEvent> {
        private String title;
        private Consumer<NewEditTestDialogController> dialogType;

        public TestMenuEventHandler(String title, Consumer<NewEditTestDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            NewEditTestDialogController controller = (NewEditTestDialogController) newEditTestDialog.getController();
            if (testDialogStage == null) {
                testDialogStage = new Stage();
                testDialogStage.setScene(new Scene(newEditTestDialog.getView()));
                testDialogStage.setResizable(false);
                testDialogStage.initModality(Modality.APPLICATION_MODAL);
//                manufacturerDialogStage.initStyle(StageStyle.UNDECORATED);
                controller.setStage(testDialogStage);
                controller.setTestListView(testListView);

            }
            testDialogStage.setTitle(title);
            dialogType.accept(controller);
            testDialogStage.show();
        }
    }

    // TODO: do not delete!
//    private void setupStartStopToggleButton() {
//
//        startStopToggleButton.selectedProperty().addListener((observable, stopped, started) -> {
//            if (started) {
//                flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true);
//                startStopToggleButton.setText("Stop");
//            }
//            else {
//                flowModbusWriter.add(ModbusMapFlow.StopMeasurementCycle, true);
//                startStopToggleButton.setText("Start");
//            }
//        });
//
//    }
//
//    private void setupResetButton() {
//
//        resetButton.setOnAction(event -> flowModbusWriter.add(ModbusMapFlow.StartMeasurementCycle, true));
//
//    }
}
