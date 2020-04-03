package fi.stardex.sisu.ui.controllers.pumps.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.pump.CustomPumpTestDialogModel;
import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.model.pump.PumpTestListModel;
import fi.stardex.sisu.model.pump.PumpTestModel;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.persistence.orm.pump.PumpTestName;
import fi.stardex.sisu.persistence.repos.pump.PumpTestNameService;
import fi.stardex.sisu.persistence.repos.pump.PumpTestService;
import fi.stardex.sisu.util.InputController;
import fi.stardex.sisu.util.enums.Operation;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.util.enums.GUI_type.CR_Pump;
import static fi.stardex.sisu.util.enums.Operation.DELETE;
import static fi.stardex.sisu.util.enums.Operation.NEW;
import static fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig.NO_CONFIG;

public class CustomPumpTestDialogController {

    @FXML private TextField rpmTextField;
    @FXML private TextField barTextField;
    @FXML private TextField scvEavTextField;
    @FXML private TextField maxScvTextField;
    @FXML private TextField minScvTextField;
    @FXML private TextField shiftTextField;
    @FXML private TextField maxDirectTextField;
    @FXML private TextField minDirectTextField;
    @FXML private TextField maxBackTextField;
    @FXML private TextField minBackTextField;
    @FXML private TextField adjustmentTextField;
    @FXML private TextField measurementTextField;
    @FXML private CheckBox vacuumCheckBox;
    @FXML private CheckBox directCheckBox;
    @FXML private CheckBox backCheckBox;
    @FXML private CheckBox measureCheckBox;
    @FXML private Label rpmLabel;
    @FXML private Label scvMaxLabel;
    @FXML private Label minDirectLabel;
    @FXML private Label scvEavLabel;
    @FXML private Label adjustingLabel;
    @FXML private Label pressureLabel;
    @FXML private Label scvMinLabel;
    @FXML private Label maxDirectLabel;
    @FXML private Label measuringLabel;
    @FXML private Label testLabel;
    @FXML private Label minBackLabel;
    @FXML private Label maxBackLabel;
    @FXML private Label shiftLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private ComboBox<PumpTestName> testComboBox;

    private Stage dialogStage;
    private Parent dialogView;
    private GUI_TypeModel guiTypeModel;
    private PumpTestService pumpTestService;
    private CustomPumpTestDialogModel customPumpTestDialogModel;
    private PumpTestModel pumpTestModel;
    private PumpModel pumpModel;
    private PumpTestNameService pumpTestNameService;
    private I18N i18N;
    private PumpTestListModel pumpTestListModel;
    private List<PumpTestName> testNames = new LinkedList<>();
    private List<Control> allControlsList = new ArrayList<>();
    private boolean isValid;

    private StringProperty alertString = new SimpleStringProperty("Please specify all values!");
    private StringProperty yesButton = new SimpleStringProperty();
    private Alert alert;
    private final int TEXT_FIELD_MAX_LENGTH = 6;

    public void setDialogViev(Parent dialogViev) {
        this.dialogView = dialogViev;
    }
    public void setGuiTypeModel(GUI_TypeModel guiTypeModel) {
        this.guiTypeModel = guiTypeModel;
    }
    public void setPumpTestService(PumpTestService pumpTestService) {
        this.pumpTestService = pumpTestService;
    }
    public void setCustomPumpTestDialogModel(CustomPumpTestDialogModel customPumpTestDialogModel) {
        this.customPumpTestDialogModel = customPumpTestDialogModel;
    }
    public void setPumpTestNameService(PumpTestNameService pumpTestNameService) {
        this.pumpTestNameService = pumpTestNameService;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpTestListModel(PumpTestListModel pumpTestListModel) {
        this.pumpTestListModel = pumpTestListModel;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    @PostConstruct
    public void init() {

        testNames.addAll(pumpTestNameService.findAll());
        setupCustomOperationListener();
        setupButtonsListeners();
        setupTestComboBoxListener();
        setupControlsActivationListeners();
        fillControlsList();
        bindingI18N();
    }

    private void setupCustomOperationListener() {

        pumpTestListModel.customTestOperationProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;
            /** Additional check of GUI type is done to prevent listener invocation and dialog window irrelevant to GUI type activation.
             * This will be important after implementation of MainSectionUisController as a unique one for all GUI types.
             * Such a check could be done through {@code newValue instanceOf InjectorUisTest} but it is slower */
            if (guiTypeModel.guiTypeProperty().get() != CR_Pump) {return;}

            if (dialogStage == null) {
                dialogStage = new Stage();
                dialogStage.setScene(new Scene(dialogView));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setOnCloseRequest(event -> customPumpTestDialogModel.cancelProperty().setValue(new Object()));
            }

            dialogStage.setTitle(pumpTestListModel.customTestOperationProperty().get().getTitle() + " test");
            prepareWindow();
            dialogStage.show();
        });
    }

    private void setupButtonsListeners() {

        saveButton.setOnMouseClicked(event -> {

            switch (pumpTestListModel.customTestOperationProperty().get()) {

                case NEW:
                    create();
                    break;
                case EDIT:
                    update();
                    break;
                case DELETE:
                    delete();
                    break;
                case COPY:
                    break;
            }
        });

        cancelButton.setOnMouseClicked(event -> {

            customPumpTestDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });
    }

    private void setupTestComboBoxListener() {

        testComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == null){
                saveButton.setDisable(true);
                return;
            }

            saveButton.setDisable(false);
        });
    }

    private void setupControlsActivationListeners() {

        directCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            maxDirectTextField.setDisable(!newValue);
            minDirectTextField.setDisable(!newValue);
        });

        backCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            maxBackTextField.setDisable(!newValue);
            minBackTextField.setDisable(!newValue);
        });

        testComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;

            boolean scvCalibration = newValue.getName().equals("SCV Calibration");
            maxScvTextField.setDisable(!scvCalibration);
            minScvTextField.setDisable(!scvCalibration);
            shiftTextField.setDisable(!scvCalibration);
        });

        measureCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> measurementTextField.setDisable(!newValue));
    }

    private void fillControlsList() {

        allControlsList.add(rpmTextField);
        allControlsList.add(barTextField);
        allControlsList.add(scvEavTextField);
        allControlsList.add(maxScvTextField);
        allControlsList.add(minScvTextField);
        allControlsList.add(shiftTextField);
        allControlsList.add(maxDirectTextField);
        allControlsList.add(minDirectTextField);
        allControlsList.add(maxBackTextField);
        allControlsList.add(minBackTextField);
        allControlsList.add(adjustmentTextField);
        allControlsList.add(measurementTextField);
        allControlsList.forEach(c -> InputController.blockTextInputToNumberFields((TextField) c, TEXT_FIELD_MAX_LENGTH));
        allControlsList.add(directCheckBox);
        allControlsList.add(backCheckBox);
        allControlsList.add(measureCheckBox);
        allControlsList.add(vacuumCheckBox);
        allControlsList.add(testComboBox);
    }

    private void prepareWindow() {

        disableAllNodes(false);
        Operation operation = pumpTestListModel.customTestOperationProperty().get();
        Pump pump = pumpModel.pumpProperty().get();

        scvEavTextField.setDisable(pump.getPumpRegulatorConfig() == NO_CONFIG);

        directCheckBox.setSelected(false);
        backCheckBox.setSelected(false);
        measureCheckBox.setSelected(false);
        maxDirectTextField.setDisable(true);
        minDirectTextField.setDisable(true);
        maxBackTextField.setDisable(true);
        minBackTextField.setDisable(true);
        maxScvTextField.setDisable(true);
        minScvTextField.setDisable(true);
        shiftTextField.setDisable(true);
        measurementTextField.setDisable(true);

        if (operation == NEW) {

            testComboBox.getSelectionModel().clearSelection();
            vacuumCheckBox.setSelected(false);

            rpmTextField.clear();
            barTextField.clear();
            scvEavTextField.clear();
            maxDirectTextField.clear();
            minDirectTextField.clear();
            maxBackTextField.clear();
            minBackTextField.clear();
            maxScvTextField.clear();
            minScvTextField.clear();
            shiftTextField.clear();
            adjustmentTextField.clear();
            measurementTextField.clear();

            testComboBox.getItems().setAll(testNames);
            List<PumpTest> tests = pumpTestService.findAllByModel(pumpModel.pumpProperty().get());
            if (tests != null) {
                tests.forEach(pumpTest -> testComboBox.getItems().remove(pumpTest.getTestName())); }

        } else {

            PumpTest test = pumpTestModel.pumpTestProperty().get();
            testComboBox.getItems().setAll(testNames);
            testComboBox.getSelectionModel().select(test.getTestName());
            testComboBox.setDisable(true);

            rpmTextField.setText(getStringFromInteger(PumpTest::getMotorSpeed, test));
            barTextField.setText(getStringFromInteger(PumpTest::getTargetPressure, test));
            scvEavTextField.setText(getStringFromDouble(PumpTest::getRegulatorCurrent, test));
            vacuumCheckBox.setSelected(test.isVacuum());
            adjustmentTextField.setText(getStringFromInteger(PumpTest::getAdjustingTime, test));

            if (test.getMaxDirectFlow() != null || test.getMinDirectFlow() != null) {
                directCheckBox.setSelected(true);
                maxDirectTextField.setText(getStringFromDouble(PumpTest::getMaxDirectFlow, test));
                minDirectTextField.setText(getStringFromDouble(PumpTest::getMinDirectFlow, test));
            }
            if (test.getMaxBackFlow() != null || test.getMinBackFlow() != null) {
                backCheckBox.setSelected(true);
                maxBackTextField.setText(getStringFromDouble(PumpTest::getMaxBackFlow, test));
                minBackTextField.setText(getStringFromDouble(PumpTest::getMinBackFlow, test));
            }
            if (test.getCalibrationMaxI() != null || test.getCalibrationMinI() != null) {
                maxScvTextField.setText(getStringFromDouble(PumpTest::getCalibrationMaxI, test));
                minScvTextField.setText(getStringFromDouble(PumpTest::getCalibrationMinI, test));
                shiftTextField.setText(getStringFromDouble(PumpTest::getCalibrationIoffset, test));
            }
            if (test.getMeasuringTime() != null) {
                measureCheckBox.setSelected(true);
                measurementTextField.setText(getStringFromInteger(PumpTest::getMeasuringTime, test));
            }
        }

        if (operation == DELETE) { disableAllNodes(true); }
    }

    private void disableAllNodes(boolean disable) {

        allControlsList.forEach(c -> c.setDisable(disable));
    }

    private void create() {

        if(!isDataComplete())return;

        PumpTest test = new PumpTest(pumpModel.pumpProperty().get(),
                testComboBox.getValue(),
                getInteger(rpmTextField),
                getInteger(barTextField),
                getDouble(scvEavTextField),
                vacuumCheckBox.isSelected(),
                getDouble(maxDirectTextField),
                getDouble(minDirectTextField),
                getDouble(maxBackTextField),
                getDouble(minBackTextField),
                getDouble(maxScvTextField),
                getDouble(minScvTextField),
                getDouble(shiftTextField),
                getInteger(adjustmentTextField),
                getInteger(measurementTextField));

        pumpTestService.save(test);
        customPumpTestDialogModel.customTestProperty().setValue(test);
        customPumpTestDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void update() {

        if(!isDataComplete())return;

        PumpTest test = pumpTestModel.pumpTestProperty().get();

        test.setMotorSpeed(getInteger(rpmTextField));
        test.setTargetPressure(getInteger(barTextField));
        test.setRegulatorCurrent(getDouble(scvEavTextField));
        test.setVacuum(vacuumCheckBox.isSelected());
        test.setMaxDirectFlow(getDouble(maxDirectTextField));
        test.setMinDirectFlow(getDouble(minDirectTextField));
        test.setMaxBackFlow(getDouble(maxBackTextField));
        test.setMinBackFlow(getDouble(minBackTextField));
        test.setCalibrationMaxI(getDouble(maxScvTextField));
        test.setCalibrationMinI(getDouble(minScvTextField));
        test.setCalibrationIoffset(getDouble(shiftTextField));
        test.setAdjustingTime(getInteger(adjustmentTextField));
        test.setMeasuringTime(getInteger(measurementTextField));

        pumpTestService.update(test);
        customPumpTestDialogModel.customTestProperty().setValue(test);
        customPumpTestDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void delete() {

        Test test = pumpTestModel.pumpTestProperty().get();
        pumpTestService.delete(test);
        customPumpTestDialogModel.customTestProperty().setValue(null);
        customPumpTestDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }


    private boolean isEmpty(Control control) {

        if (control instanceof ComboBox) {
            return ((ComboBox) control).getSelectionModel().isEmpty();
        } else if (control instanceof TextField) {
            return ((TextField) control).getText().isEmpty();
        }else return true;
    }

    private Integer getInteger(Control control) {

        if (control instanceof TextField) {
            return ((TextField)control).getText().isEmpty() ? null : convertDataToInt(((TextField)control).getText());
        } return null;
    }

    private Double getDouble(Control control) {

        if (control instanceof TextField) {
            return ((TextField)control).getText().isEmpty() ? null : convertDataToDouble(((TextField)control).getText());
        } return null;
    }

    private String getStringFromInteger(Function<PumpTest, Integer> integerFunction, PumpTest test) {

        if(test == null) return null;
        if(integerFunction.apply(test) == null) return "";
        return integerFunction.apply(test).toString();
    }

    private String getStringFromDouble(Function<PumpTest, Double> doubleFunction, PumpTest test) {

        if(test == null) return null;
        if(doubleFunction.apply(test) == null) return "";
        return doubleFunction.apply(test).toString();
    }

    private boolean isDataComplete() {

        isValid = true;

        validateValues(maxDirectTextField, minDirectTextField, directCheckBox.isSelected());
        validateValues(maxBackTextField, minBackTextField, backCheckBox.isSelected());
        validateValues(maxScvTextField, minScvTextField, !maxScvTextField.isDisabled());

        if ((!scvEavTextField.isDisabled() && isEmpty(scvEavTextField))
                || isEmpty(rpmTextField)
                || isEmpty(barTextField)
                || isEmpty(adjustmentTextField)
                || (!measurementTextField.isDisabled() && isEmpty(measurementTextField)))

        { isValid  = false; }

        if(!isValid) showAlert();

        if(alert != null && alert.isShowing())return false;
        return true;
    }

    private void validateValues(TextField max, TextField min, boolean groupActive) {

        if (!groupActive) return;
        if (isEmpty(max) && isEmpty(min)) { isValid = false; }
        if (!isEmpty(max) && !isEmpty(min) && getDouble(max) <= getDouble(min)) { isValid = false;}
    }

    private void showAlert() {

        if (alert == null) {
            alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        }
        ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).textProperty().setValue(yesButton.get());
        alert.setContentText(alertString.get());
        alert.show();
    }

    private void bindingI18N() {
//        rpmLabel.textProperty().bind(i18N.createStringBinding(""));
//        scvMaxLabel.textProperty().bind(i18N.createStringBinding(""));
//        minDirectLabel.textProperty().bind(i18N.createStringBinding(""));
//        adjustingLabel.textProperty().bind(i18N.createStringBinding(""));
//        pressureLabel.textProperty().bind(i18N.createStringBinding(""));
//        scvMinLabel.textProperty().bind(i18N.createStringBinding(""));
//        maxDirectLabel.textProperty().bind(i18N.createStringBinding(""));
//        measuringLabel.textProperty().bind(i18N.createStringBinding(""));
//        testLabel.textProperty().bind(i18N.createStringBinding(""));
//        minBackLabel.textProperty().bind(i18N.createStringBinding(""));
//        maxBackLabel.textProperty().bind(i18N.createStringBinding(""));
//        shiftLabel.textProperty().bind(i18N.createStringBinding(""));
//        pcvLabel.textProperty().bind(i18N.createStringBinding(""));
//        saveButton.textProperty().bind(i18N.createStringBinding(""));
//        cancelButton.textProperty().bind(i18N.createStringBinding(""));
//        vacuumCheckBox.textProperty().bind(i18N.createStringBinding(""));
        alertString.bind((i18N.createStringBinding("alert.customDialog")));
        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
    }
}
