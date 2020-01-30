package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.cr.CrSettingsModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.model.pump.PumpTestModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.model.uis.UisSettingsModel;
import fi.stardex.sisu.model.updateModels.DifferentialFmUpdateModel;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import java.util.prefs.Preferences;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.*;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.MASTER_DF;

public class DifferentialFMController {

    @FXML public Button closeButton;
    @FXML private Label timeLabel;
    @FXML private Label periodLabel;
    @FXML private Spinner<Integer> shiftingTimeSpinner;
    @FXML private Spinner<Integer> shiftingPeriodSpinner;

    private Stage stage;
    private I18N i18N;
    private Parent calibrationDialogView;
    private Logger log = LoggerFactory.getLogger(DifferentialFMController.class);
    private Preferences preferences;

    private CrSettingsModel crSettingsModel;
    private UisSettingsModel uisSettingsModel;
    private ModbusRegisterProcessor flowModbusWriter;
    private DifferentialFmUpdateModel differentialFmUpdateModel;
    private MainSectionUisModel mainSectionUisModel;
    private MainSectionModel mainSectionModel;
    private PumpTestModel pumpTestModel;
    private InjectorSectionPwrState injectorSectionPwrState;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private ChangeListener<Boolean> launchCalibration;
    private GUI_TypeModel gui_typeModel;
    private ModbusConnect flowModbusConnect;
    private FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion;

    private final String PERIOD_PREF_KEY = "CalibrationPeriod";
    private final String TIME_PREF_KEY = "CalibrationTime";
    private BooleanProperty calibrationPermittedProperty;

    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setCalibrationDialogView(Parent calibrationDialogView) {
        this.calibrationDialogView = calibrationDialogView;
    }
    public void setCrSettingsModel(CrSettingsModel crSettingsModel) {
        this.crSettingsModel = crSettingsModel;
    }
    public void setUisSettingsModel(UisSettingsModel uisSettingsModel) {
        this.uisSettingsModel = uisSettingsModel;
    }
    public void setDifferentialFmUpdateModel(DifferentialFmUpdateModel differentialFmUpdateModel) {
        this.differentialFmUpdateModel = differentialFmUpdateModel;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
    public void setInjectorSectionPwrState(InjectorSectionPwrState injectorSectionPwrState) {
        this.injectorSectionPwrState = injectorSectionPwrState;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setFlowModbusConnect(ModbusConnect flowModbusConnect) {
        this.flowModbusConnect = flowModbusConnect;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }

    @PostConstruct
    public void init() {

        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> selectCalibrationPermittedProperty());

        /** Forced selection of ReadyForMeasurementsProperty to avoid null in calibrationPermittedProperty at the very beginning*/
        selectCalibrationPermittedProperty();

        /** This listener is registered on corresponding ReadyForMeasurementsProperty upon calibration condition reached
         * and unregistered immediately after first invocation*/
        launchCalibration = (observableValue, oldValue, newValue) -> {

            if (newValue) {

                differentialFmUpdateModel.calibrationPermittedProperty().setValue(false);
                differentialFmUpdateModel.lastCalibrationStartProperty().setValue(System.currentTimeMillis());
                flowModbusWriter.add(ShiftingManualStart, true);
                calibrationPermittedProperty.removeListener(launchCalibration);
            }
        };

        shiftingPeriodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1800, 300, 1));
        shiftingTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 10, 1));

        shiftingPeriodSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            flowModbusWriter.add(ShiftingPeriod, newValue);
            preferences.putInt(PERIOD_PREF_KEY, newValue);
        });

        shiftingTimeSpinner.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            flowModbusWriter.add(ShiftingDuration, getCalibrationTime(newValue));
            preferences.putInt(TIME_PREF_KEY, newValue);
        });

        differentialFmUpdateModel.calibrationPeriodProperty().bind(shiftingPeriodSpinner.valueProperty());
        differentialFmUpdateModel.calibrationTimeProperty().bind(shiftingTimeSpinner.valueProperty());

        shiftingPeriodSpinner.getValueFactory().setValue(preferences.getInt(PERIOD_PREF_KEY, 300));
        shiftingTimeSpinner.getValueFactory().setValue(preferences.getInt(TIME_PREF_KEY, 10));

        mainSectionUisModel.injectorTestProperty().addListener((observable, oldValue, newValue) ->
                startCalibration(newValue != null && differentialFmUpdateModel.calibrationPermittedProperty().get()));
        //TODO next 2 listeners should be deleted after implementation of unique MainSection concept based on MainSectionUisController
        mainSectionModel.injectorTestProperty().addListener((observable, oldValue, newValue) ->
                startCalibration(newValue != null && differentialFmUpdateModel.calibrationPermittedProperty().get()));
        // Temporarily calibration is deactivated for Cr_Pump.
        // Define and create triggerProperty for calibration start (it differs seriously from CR & UIS triggerProperties) and uncomment code below.
        // Configure this triggerProperty in selectCalibrationPermittedProperty() similarly to other GUIs.
        // Make necessary changes (commented) in init() method in PumpFlowController.
//        pumpTestModel.pumpTestProperty().addListener((observable, oldValue, newValue) ->
//                startCalibration(newValue != null && differentialFmUpdateModel.calibrationPermittedProperty().get()));

        /**Additional check on lastCalibrationStartTime != 0 is necessary to avoid progress bar exposition after hardware first start.
         * Durable primary calibration is started upon hardware is powered on but GUI counter is not started simultaneously.
         * Illegal state of progressBar will be demonstrated as a consequence if it is not hidden during this interval.*/
        differentialFmUpdateModel.calibrationIsInProgressProperty().addListener((observable, oldValue, newValue) ->
                differentialFmUpdateModel.showProgressProperty().setValue(newValue && differentialFmUpdateModel.lastCalibrationStartProperty().get() != 0));

        closeButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                log.info(String.format("Calibration parameters set to: duration %d ms, period %d s", shiftingTimeSpinner.getValue(), shiftingPeriodSpinner.getValue()));
                stage.close();
            }
        });

        flowModbusConnect.connectedProperty().addListener((observableValue, oldValue, newValue) -> {

            /** If HW is reconnected we have to clean value of last calibration time for :
             * - providing correct internal counting
             * - avoidance of irrelevant progress bar exposition after hardware start*/
            differentialFmUpdateModel.lastCalibrationStartProperty().setValue(0);

            if (flowFirmwareVersion.getVersions() != MASTER_DF) { return; }

            if (newValue) {

                flowModbusWriter.add(ShiftingAutoStartIsOn, false);
                flowModbusWriter.add(ShiftingDuration, getCalibrationTime(shiftingTimeSpinner.getValue()));
                flowModbusWriter.add(ShiftingPeriod, shiftingPeriodSpinner.getValue());
            }
        });

        setupWindow();
        bindingI18N();
    }

    /** It is not allowed to start calibration simply upon test selection and calibration interval passed because of the pressure readjustment phase.
     * StopMeasurements command is sent to hardware upon new pressure setting choice.
     * We are waiting for pressure stabilisation after this moment and then send StartMeasurements command to hardware.
     * Calibration should be launched after StartMeasurements command only!*/
    private void startCalibration(boolean init) {

        if (init) {
            /** User can select tests several times. Each time new listener will be added and each will be invoked.
             * To avoid this mistake any new listener addition should replace previously added listener.*/
            calibrationPermittedProperty.removeListener(launchCalibration);
            calibrationPermittedProperty.addListener(launchCalibration);
        }
    }

    private void setupWindow() {

        crSettingsModel.getDifferentialFmSettingsButton().setOnAction(actionEvent -> showWindow());
        uisSettingsModel.getDifferentialFmSettingsButton().setOnAction(actionEvent -> showWindow());
    }

    private void showWindow() {

        if (stage == null) {
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(calibrationDialogView));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
        }
        stage.show();
    }

    private void selectCalibrationPermittedProperty() {

        switch (gui_typeModel.guiTypeProperty().get()) {

            case CR_Inj:
            case HEUI:
                calibrationPermittedProperty = injectorSectionPwrState.powerButtonProperty();
                break;
            case UIS:
                calibrationPermittedProperty = uisInjectorSectionModel.injectorButtonProperty();
        }
    }

    private int getCalibrationTime(int raw) {
        return raw * 1000;
    }

    private void bindingI18N() {

        timeLabel.textProperty().bind(i18N.createStringBinding("differentialFM.calibrationTime"));
        periodLabel.textProperty().bind(i18N.createStringBinding("differentialFM.calibrationPeriod"));
        closeButton.textProperty().bind(i18N.createStringBinding("dialog.customer.close"));
    }
}
