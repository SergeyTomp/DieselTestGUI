package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.uis.CustomModelDialogModel;
import fi.stardex.sisu.model.uis.CustomVapUisDialogModel;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.persistence.repos.uis.UisVapService;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.Operation;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.util.Arrays;

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class CustomVapUisDialogController {

    @FXML private Label deletionError;
    @FXML private Label injSubTypeLabel;
    @FXML private Label firstW1Label;
    @FXML private Label batteryULabel;
    @FXML private Label boostI1Label;
    @FXML private Label boostULabel;
    @FXML private Label firstI1Label;
    @FXML private Label negativeULabel;
    @FXML private Label secondI1Label;
    @FXML private Label enterNameLabel;
    @FXML private Label injTypeLabel;
    @FXML private Label coil1Label;
    @FXML private Label coil2Label;
    @FXML private Label boostI2Label;
    @FXML private Label firstI2Label;
    @FXML private Label secondI2Label;
    @FXML private Label firstW2Label;
    @FXML private Label bipWindowLabel;
    @FXML private Label camTypeLabel;
    @FXML private Label inletPressureLabel;
    @FXML private CheckBox bipCheckBox;
    @FXML private Spinner<Double> firstI2Spinner;
    @FXML private Spinner<Double> secondI2Spinner;
    @FXML private Spinner<Double> boostI2Spinner;
    @FXML private Spinner<Integer> firstW2Spinner;
    @FXML private TextField enterNameTF;
    @FXML private Label notUniqueLabel;
    @FXML private Label currInjTypeLabel;
    @FXML private Label currInjSubTypeLabel;
    @FXML private Spinner<Integer> firstWSpinner;
    @FXML private Spinner<Integer> batteryUSpinner;
    @FXML private Spinner<Double> boostISpinner;
    @FXML private Spinner<Integer> boostUSpinner;
    @FXML private Spinner<Double> firstISpinner;
    @FXML private Spinner<Integer> negativeUSpinner;
    @FXML private Spinner<Double> secondISpinner;
    @FXML private ToggleButton enableBoostToggleButton;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private ComboBox<Integer> camTypeCB;
    @FXML private Spinner<Integer> inletPressureSpinner;
    @FXML private Spinner<Integer> bipPwmSpinner;
    @FXML private Spinner<Integer> bipWindowSpinner;

    private Parent newEditVOAPDialog;
    private Stage newVOAPStage;
    private String title = " VoltAmpereProfile";
    private SpinnerValueFactory.IntegerSpinnerValueFactory bipPwmSpinnerEnabled;
    private SpinnerValueFactory.IntegerSpinnerValueFactory bipPwmSpinnerDisabled;
    private SpinnerValueFactory.IntegerSpinnerValueFactory bipWindowSpinnerEnabled;
    private SpinnerValueFactory.IntegerSpinnerValueFactory bipWindowSpinnerDisabled;
    private StringProperty boostU_enabled = new SimpleStringProperty();
    private StringProperty boostU_disabled = new SimpleStringProperty();
    private StringBuilder codeBuilder = new StringBuilder();

    private CustomVapUisDialogModel customVapUisDialogModel;
    private CustomModelDialogModel customModelDialogModel;
    private UisVapService uisVapService;
    private I18N i18N;

    public void setCustomVapUisDialogModel(CustomVapUisDialogModel customVapUisDialogModel) {
        this.customVapUisDialogModel = customVapUisDialogModel;
    }
    public void setCustomModelDialogModel(CustomModelDialogModel customModelDialogModel) {
        this.customModelDialogModel = customModelDialogModel;
    }
    public void setNewEditVOAPDialog(Parent newEditVOAPDialog) {
        this.newEditVOAPDialog = newEditVOAPDialog;
    }
    public void setUisVapService(UisVapService uisVapService) {
        this.uisVapService = uisVapService;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {

        bipPwmSpinnerEnabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 20, 15, 1);
        bipPwmSpinnerDisabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0, 0);
        bipWindowSpinnerEnabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(400, 800, 400, 100);
        bipWindowSpinnerDisabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0, 0);

        camTypeCB.getItems().setAll((Arrays.asList(1, 2)));
        camTypeCB.getSelectionModel().select(0);
        deletionError.setVisible(false);

        customModelDialogModel.customVapOperationProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;

            if (newVOAPStage == null) {
                newVOAPStage = new Stage();
                newVOAPStage.setScene(new Scene(newEditVOAPDialog));
                newVOAPStage.setResizable(false);
                newVOAPStage.initModality(Modality.APPLICATION_MODAL);
            }

            newVOAPStage.setTitle(customModelDialogModel.customVapOperationProperty().get().getTitle() + title);
            deletionError.setVisible(false);
            notUniqueLabel.setVisible(false);

            switch (customModelDialogModel.customVapOperationProperty().get()) {
                case EDIT:
                    setEdit();
                    break;
                case COPY:
                    setCopy();
                    break;
                case DELETE:
                    setDelete();
                    break;
                case NEW:
                    setNew();
                    break;
            }
            newVOAPStage.show();
        });

        saveBtn.setOnAction(actionEvent -> {

            switch (customModelDialogModel.customVapOperationProperty().get()) {
                case NEW:
                    create();
                    break;
                case EDIT:
                    edit();
                    break;
                case COPY:
                    copy();
                    break;
                case DELETE:
                    delete();
                    break;
            }
        });

        cancelBtn.setOnAction(actionEvent -> {

            customVapUisDialogModel.cancelProperty().setValue(new Object());
            newVOAPStage.close();
        });

        enableBoostToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                enableBoostToggleButton.setText(boostU_enabled.get());
            } else {
                enableBoostToggleButton.setText(boostU_disabled.get());
            }
        });

        customModelDialogModel.getInjectorType().addListener((observableValue,  oldValue, newValue) -> currInjTypeLabel.setText(newValue.name()));
        customModelDialogModel.getInjectorSubType().addListener((observableValue,  oldValue, newValue) -> {
            currInjSubTypeLabel.setText(newValue.name());
            activateCoil2Spinners(newValue == InjectorSubType.DOUBLE_COIL || newValue == InjectorSubType.F2E_COMMON);
        });

        bipCheckBox.selectedProperty().addListener((observableValue,  oldValue, newValue) -> activateBipSpinners(newValue));
        activateBipSpinners(false);
        bipCheckBox.setSelected(false);

        firstWSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(FIRST_W_SPINNER_MIN,
                FIRST_W_SPINNER_MAX,
                FIRST_W_SPINNER_INIT,
                FIRST_W_SPINNER_STEP));
        boostISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(BOOST_I_SPINNER_MIN,
                BOOST_I_SPINNER_MAX,
                BOOST_I_SPINNER_INIT,
                BOOST_I_SPINNER_STEP));
        firstISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(FIRST_I_SPINNER_MIN,
                FIRST_I_SPINNER_MAX,
                FIRST_I_SPINNER_INIT,
                FIRST_I_SPINNER_STEP));
        secondISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(SECOND_I_SPINNER_MIN,
                SECOND_I_SPINNER_MAX,
                SECOND_I_SPINNER_INIT,
                SECOND_I_SPINNER_STEP));
        batteryUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BATTERY_U_SPINNER_MIN,
                BATTERY_U_SPINNER_MAX,
                BATTERY_U_SPINNER_INIT,
                BATTERY_U_SPINNER_STEP));
        negativeUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(NEGATIVE_U_SPINNER_MIN,
                NEGATIVE_U_SPINNER_MAX,
                NEGATIVE_U_SPINNER_INIT,
                NEGATIVE_U_SPINNER_STEP));
        boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                BOOST_U_SPINNER_MAX,
                BOOST_U_SPINNER_INIT,
                BOOST_U_SPINNER_STEP));

        firstW2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(FIRST_W_SPINNER_MIN,
                FIRST_W_SPINNER_MAX,
                FIRST_W_SPINNER_INIT,
                FIRST_W_SPINNER_STEP));
        boostI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(BOOST_I_SPINNER_MIN,
                BOOST_I_SPINNER_MAX,
                BOOST_I_SPINNER_INIT,
                BOOST_I_SPINNER_STEP));
        firstI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(FIRST_I_SPINNER_MIN,
                FIRST_I_SPINNER_MAX,
                FIRST_I_SPINNER_INIT,
                FIRST_I_SPINNER_STEP));
        secondI2Spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(SECOND_I_SPINNER_MIN,
                SECOND_I_SPINNER_MAX,
                SECOND_I_SPINNER_INIT,
                SECOND_I_SPINNER_STEP));

        inletPressureSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 6, 3, 1));

        SpinnerManager.setupIntegerSpinner(firstWSpinner);
        SpinnerManager.setupDoubleSpinner(boostISpinner);
        SpinnerManager.setupDoubleSpinner(firstISpinner);
        SpinnerManager.setupDoubleSpinner(secondISpinner);
        SpinnerManager.setupIntegerSpinner(batteryUSpinner);
        SpinnerManager.setupIntegerSpinner(negativeUSpinner);
        SpinnerManager.setupIntegerSpinner(boostUSpinner);
        SpinnerManager.setupIntegerSpinner(firstW2Spinner);
        SpinnerManager.setupDoubleSpinner(boostI2Spinner);
        SpinnerManager.setupDoubleSpinner(firstI2Spinner);
        SpinnerManager.setupDoubleSpinner(secondI2Spinner);
        SpinnerManager.setupIntegerSpinner(inletPressureSpinner);
        SpinnerManager.setupIntegerSpinner(bipPwmSpinner);
        SpinnerManager.setupIntegerSpinner(bipWindowSpinner);

        activateCoil2Spinners(false);

        bindingI18N();

        enableBoostToggleButton.setSelected(false);
        enableBoostToggleButton.setText(boostU_disabled.get());
    }

    private void create() {

        if (enterNameTF.getText().isEmpty() || uisVapService.existsById(enterNameTF.getText())) {
            notUniqueLabel.setVisible(true);
        } else {
            notUniqueLabel.setVisible(false);
            InjectorUisVAP newProfile = new InjectorUisVAP(
                    enterNameTF.getText(),
                    customModelDialogModel.getInjectorType().get(),
                    customModelDialogModel.getInjectorSubType().get(),
                    camTypeCB.getSelectionModel().getSelectedItem(),
                    true,
                    inletPressureSpinner.getValue(),
                    boostUSpinner.getValue(),
                    batteryUSpinner.getValue(),
                    boostISpinner.getValue(),
                    firstISpinner.getValue(),
                    firstWSpinner.getValue(),
                    secondISpinner.getValue(),
                    boostI2Spinner.getValue(),
                    firstI2Spinner.getValue(),
                    firstW2Spinner.getValue(),
                    secondI2Spinner.getValue(),
                    negativeUSpinner.getValue(),
                    !enableBoostToggleButton.isSelected(),
                    bipPwmSpinner.getValue() == 0 ? null : bipPwmSpinner.getValue(),
                    bipWindowSpinner.getValue() == 0 ? null : bipWindowSpinner.getValue());

            uisVapService.save(newProfile);
            complete(newProfile);
        }
    }

    private void copy() {

        InjectorUisVAP newProfile = new InjectorUisVAP(
                makeCode(enterNameTF.getText()),
                customModelDialogModel.getInjectorType().get(),
                customModelDialogModel.getInjectorSubType().get(),
                camTypeCB.getSelectionModel().getSelectedItem(),
                true,
                inletPressureSpinner.getValue(),
                boostUSpinner.getValue(),
                batteryUSpinner.getValue(),
                boostISpinner.getValue(),
                firstISpinner.getValue(),
                firstWSpinner.getValue(),
                secondISpinner.getValue(),
                boostI2Spinner.getValue(),
                firstI2Spinner.getValue(),
                firstW2Spinner.getValue(),
                secondI2Spinner.getValue(),
                negativeUSpinner.getValue(),
                !enableBoostToggleButton.isSelected(),
                bipPwmSpinner.getValue() == 0 ? null : bipPwmSpinner.getValue(),
                bipWindowSpinner.getValue() == 0 ? null : bipWindowSpinner.getValue());

        uisVapService.save(newProfile);
        complete(newProfile);
    }

    private void edit() {

        InjectorUisVAP vapToUpdate = (InjectorUisVAP)customModelDialogModel.customVapProperty().get();
        InjectorSubType injectorSubType = vapToUpdate.getInjectorSubType();

        vapToUpdate.setInletPressure(inletPressureSpinner.getValue());
        vapToUpdate.setCamType(camTypeCB.getSelectionModel().getSelectedItem());
        vapToUpdate.setBatteryU(batteryUSpinner.getValue());
        vapToUpdate.setBoostU(boostUSpinner.getValue());
        vapToUpdate.setBoostI(boostISpinner.getValue());
        vapToUpdate.setBoostDisable(!enableBoostToggleButton.isSelected());
        vapToUpdate.setFirstI(firstISpinner.getValue());
        vapToUpdate.setFirstW(firstWSpinner.getValue());
        vapToUpdate.setSecondI(secondISpinner.getValue());
        vapToUpdate.setNegativeU(negativeUSpinner.getValue());

        if (bipCheckBox.isSelected()) {
            vapToUpdate.setBipPWM(bipPwmSpinner.getValue());
            vapToUpdate.setBipWindow(bipWindowSpinner.getValue());
        }else{
            vapToUpdate.setBipPWM(null);
            vapToUpdate.setBipWindow(null);
        }

        if (injectorSubType == InjectorSubType.DOUBLE_COIL || injectorSubType == InjectorSubType.F2E_COMMON) {
            vapToUpdate.setBoostI2(boostI2Spinner.getValue());
            vapToUpdate.setFirstI2(firstI2Spinner.getValue());
            vapToUpdate.setFirstW2(firstW2Spinner.getValue());
            vapToUpdate.setSecondI2(secondI2Spinner.getValue());
        }

        uisVapService.save(vapToUpdate);
        complete(vapToUpdate);
    }

    private void delete() {

        if (!uisVapService.findByVapName(enterNameTF.getText()).getInjectors().isEmpty()) {
            deletionError.setVisible(true);
            return;
        }
        deletionError.setVisible(false);
        uisVapService.delete(customModelDialogModel.customVapProperty().get());
        complete(null);
    }

    private void setNew() {

        enterNameTF.setDisable(false);
        enterNameTF.clear();
        disableNodes(false);
        currInjTypeLabel.setText(customModelDialogModel.getInjectorType().get().name());
        currInjSubTypeLabel.setText(customModelDialogModel.getInjectorSubType().get().name());
        bipPwmSpinner.setDisable(!bipCheckBox.isSelected());
        bipWindowSpinner.setDisable(!bipCheckBox.isSelected());
    }

    private void setCopy() {

        enterNameTF.setDisable(false);
        enterNameTF.setText(customModelDialogModel.customVapProperty().get().getProfileName());
        disableNodes(true);
        setValues();
    }

    private void setEdit() {

        enterNameTF.setDisable(false);
        enterNameTF.setText(customModelDialogModel.customVapProperty().get().getProfileName());
        disableNodes(false);
        setValues();
    }

    private void setDelete() {

        enterNameTF.setDisable(true);
        enterNameTF.setText(customModelDialogModel.customVapProperty().get().getProfileName());
        disableNodes(true);
        setValues();
    }

    private void setValues() {

        InjectorUisVAP vap = (InjectorUisVAP)customModelDialogModel.customVapProperty().get();
        InjectorSubType injectorSubType = vap.getInjectorSubType();
        bipCheckBox.setSelected(true);
        bipCheckBox.setSelected(vap.getBipPWM() != null);
        currInjTypeLabel.setText(vap.getInjectorType().name());
        currInjSubTypeLabel.setText(vap.getInjectorSubType().name());
        batteryUSpinner.getValueFactory().setValue(vap.getBatteryU());
        boostUSpinner.getValueFactory().setValue(vap.getBoostU());
        boostISpinner.getValueFactory().setValue(vap.getBoostI());
        firstWSpinner.getValueFactory().setValue(vap.getFirstW());
        firstISpinner.getValueFactory().setValue(vap.getFirstI());
        secondISpinner.getValueFactory().setValue(vap.getSecondI());
        negativeUSpinner.getValueFactory().setValue(vap.getNegativeU());
        enableBoostToggleButton.setSelected(!vap.getBoostDisable());
        inletPressureSpinner.getValueFactory().setValue(vap.getInletPressure());
        camTypeCB.getSelectionModel().select(vap.getCamType());

        if (bipCheckBox.isSelected()) {
            bipPwmSpinner.getValueFactory().setValue(vap.getBipPWM());
            bipWindowSpinner.getValueFactory().setValue(vap.getBipWindow());
        }

        if (injectorSubType == InjectorSubType.DOUBLE_COIL || injectorSubType == InjectorSubType.F2E_COMMON) {
            boostI2Spinner.getValueFactory().setValue(vap.getBoostI2());
            firstW2Spinner.getValueFactory().setValue(vap.getFirstW2());
            firstI2Spinner.getValueFactory().setValue(vap.getFirstI2());
            secondI2Spinner.getValueFactory().setValue(vap.getSecondI2());
        }
    }

    private void disableNodes(boolean disable) {

        bipCheckBox.setDisable(disable);
        firstWSpinner.setDisable(disable);
        batteryUSpinner.setDisable(disable);
        boostISpinner.setDisable(disable);
        boostUSpinner.setDisable(disable);
        firstISpinner.setDisable(disable);
        negativeUSpinner.setDisable(disable);
        secondISpinner.setDisable(disable);
        enableBoostToggleButton.setDisable(disable);
        camTypeCB.setDisable(disable);
        inletPressureSpinner.setDisable(disable);
        bipPwmSpinner.setDisable(disable);
        bipWindowSpinner.setDisable(disable);
        firstI2Spinner.setDisable(disable);
        secondI2Spinner.setDisable(disable);
        boostI2Spinner.setDisable(disable);
        firstW2Spinner.setDisable(disable);

        if (customModelDialogModel.customVapOperationProperty().get() != Operation.DELETE) {
            InjectorSubType injectorSubType = customModelDialogModel.getInjectorSubType().get();
            activateCoil2Spinners(injectorSubType == InjectorSubType.DOUBLE_COIL || injectorSubType == InjectorSubType.F2E_COMMON);
        }
    }

    private String makeCode(String code) {
        codeBuilder.setLength(0);
        if (uisVapService.existsById(code)) {
            if (code.contains("(")) {
                int indexBKT1 = code.indexOf('(');
                int indexBKT2 = code.indexOf(')');
                int count = Integer.valueOf(code.substring(indexBKT1 + 1, indexBKT2));
                count++;
                codeBuilder.append(code, 0, indexBKT1).append("(").append(count).append(")");
            } else {
                codeBuilder.append(code).append("(1)");
            }
            return makeCode(codeBuilder.toString());
        }else {
            return code;
        }
    }

    private void complete(InjectorUisVAP vap) {
        customVapUisDialogModel.customVapProperty().setValue(vap);
        customVapUisDialogModel.doneProperty().setValue(new Object());
        newVOAPStage.close();
    }

    private void activateCoil2Spinners(boolean activate) {

        firstI2Spinner.setDisable(!activate);
        secondI2Spinner.setDisable(!activate);
        firstW2Spinner.setDisable(!activate);
        boostI2Spinner.setDisable(!activate);
    }

    private void activateBipSpinners(boolean activate) {

        bipPwmSpinner.setDisable(!activate);
        bipWindowSpinner.setDisable(!activate);
        bipPwmSpinner.setValueFactory(activate ? bipPwmSpinnerEnabled : bipPwmSpinnerDisabled);
        bipWindowSpinner.setValueFactory(activate ? bipWindowSpinnerEnabled : bipWindowSpinnerDisabled);
    }

    private void bindingI18N(){
        enterNameLabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.vapName"));
        injTypeLabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.injType"));
        injSubTypeLabel.textProperty().bind(i18N.createStringBinding("uis.customTest.injectorSubType"));
        coil1Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil1"));
        coil2Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil2"));
        boostU_enabled.bind(i18N.createStringBinding("voapProfile.button.boostU_enabled"));
        boostU_disabled.bind(i18N.createStringBinding("voapProfile.button.boostUdisabled"));
        boostULabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.boostU"));
        batteryULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltageHold"));
        negativeULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.firstNegativeVoltage"));
        boostI1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        boostI2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        firstW1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstW2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstI1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        firstI2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        secondI1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        secondI2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        saveBtn.textProperty().bind(i18N.createStringBinding("h4.delay.button.save"));
        cancelBtn.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
        inletPressureLabel.textProperty().bind(i18N.createStringBinding("pump.customPump.feedPressure"));
        bipWindowLabel.textProperty().bind(i18N.createStringBinding("uis.customTest.bipWindow"));
        camTypeLabel.textProperty().bind(i18N.createStringBinding("uis.customTest.camType"));
        deletionError.textProperty().bind(i18N.createStringBinding("voapProfile.label.deletionError"));
    }
}
