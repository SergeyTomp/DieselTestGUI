package fi.stardex.sisu.ui.controllers.cr.dialogs;

import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorType;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class NewEditVOAPDialogController {

    @FXML private Label deletionError;
    @FXML private Label firstW_1Label;
    @FXML private Label batteryULabel;
    @FXML private Label boostI_1Label;
    @FXML private Label boostULabel;
    @FXML private Label firstI_1Label;
    @FXML private Label negativeULabel;
    @FXML private Label secondI_1Label;
    @FXML private Label enterNameLabel;
    @FXML private Label injTypeLabel;
    @FXML private Label coil1Label;
    @FXML private Label boostI_2Label;
    @FXML private Label firstI_2Label;
    @FXML private Label secondI_2Label;
    @FXML private Label firstW_2Label;
    @FXML private CheckBox coil2CheckBox;
    @FXML private Spinner<Double> firstI2Spinner;
    @FXML private Spinner<Double> secondI2Spinner;
    @FXML private Spinner<Double> boostI2Spinner;
    @FXML private Spinner<Integer> firstW2Spinner;
    @FXML private TextField enterNameTF;
    @FXML private Label notUniqueLabel;
    @FXML private Label currInjTypeLabel;
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

    private VoltAmpereProfileRepository voltAmpereProfileRepository;
    private InjectorType injectorType;
    private State currentState;
    private Stage stage;
    private ListView<VoltAmpereProfile> voapList;
    private I18N i18N;
    private StringProperty boostU_enabled = new SimpleStringProperty();
    private StringProperty boostU_disabled = new SimpleStringProperty();
    private StringBuilder codeBuilder = new StringBuilder();
    private VoltAmpereProfile currentVOAP;

    @Autowired
    private MainSectionModel mainSectionModel;

    public void setVoltAmpereProfileRepository(VoltAmpereProfileRepository voltAmpereProfileRepository) {
        this.voltAmpereProfileRepository = voltAmpereProfileRepository;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }


    @PostConstruct
    private void init() {
        saveBtn.setOnMouseClicked(event -> {
            switch (currentState) {
                case NEW:
                    checkAndSaveVOAP();
                    break;
                case COPY:
                    copyVOAP();
                    break;
                case DELETE:
                    deleteVOAP();
                    break;
                case EDIT:
                    editVOAP();
                    break;
            }
        });

        cancelBtn.setOnMouseClicked(event -> {
            notUniqueLabel.setVisible(false);
            stage.close();
        });

        enableBoostToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                enableBoostToggleButton.setText(boostU_enabled.get());
            } else {
                enableBoostToggleButton.setText(boostU_disabled.get());
            }
        });

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

        coil2CheckBox.selectedProperty().setValue(false);
        activateCoil2Spinners(false);
        coil2CheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> activateCoil2Spinners(newValue));

        bindingI18N();
        enableBoostToggleButton.setSelected(false);
        enableBoostToggleButton.setText(boostU_disabled.get());
    }

    private void activateCoil2Spinners(boolean activate) {

        firstI2Spinner.setDisable(!activate);
        secondI2Spinner.setDisable(!activate);
        firstW2Spinner.setDisable(!activate);
        boostI2Spinner.setDisable(!activate);
    }

    private void checkAndSaveVOAP() {
        if (voltAmpereProfileRepository.existsById(enterNameTF.getText())) {
            notUniqueLabel.setVisible(true);
        } else {
            notUniqueLabel.setVisible(false);
            VoltAmpereProfile newProfile = new VoltAmpereProfile(enterNameTF.getText(),
                    injectorType,
                    true,
                    boostUSpinner.getValue(),
                    batteryUSpinner.getValue(),
                    boostISpinner.getValue(),
                    firstISpinner.getValue(),
                    firstWSpinner.getValue(),
                    secondISpinner.getValue(),
                    negativeUSpinner.getValue(),
                    !enableBoostToggleButton.isSelected(),
                    boostI2Spinner.getValue(),
                    firstI2Spinner.getValue(),
                    firstW2Spinner.getValue(),
                    secondI2Spinner.getValue(),
                    coil2CheckBox.isSelected());
            voltAmpereProfileRepository.save(newProfile);
            voapList.getItems().add(newProfile);
            voapList.getSelectionModel().select(newProfile);
            voapList.scrollTo(newProfile);
            stage.close();
        }
    }

    private void copyVOAP() {

        notUniqueLabel.setVisible(false);
        VoltAmpereProfile newProfile = new VoltAmpereProfile(makeCode(enterNameTF.getText()),
                injectorType,
                true,
                boostUSpinner.getValue(),
                batteryUSpinner.getValue(),
                boostISpinner.getValue(),
                firstISpinner.getValue(),
                firstWSpinner.getValue(),
                secondISpinner.getValue(),
                negativeUSpinner.getValue(),
                !enableBoostToggleButton.isSelected(),
                boostI2Spinner.getValue(),
                firstI2Spinner.getValue(),
                firstW2Spinner.getValue(),
                secondI2Spinner.getValue(),
                coil2CheckBox.isSelected());
        voltAmpereProfileRepository.save(newProfile);
        voapList.getItems().add(newProfile);
        voapList.getSelectionModel().select(newProfile);
        voapList.scrollTo(newProfile);
        stage.close();
    }

    private void deleteVOAP() {

        if (!voltAmpereProfileRepository.findByProfileName(voapList.getSelectionModel().getSelectedItem().getProfileName()).getInjectors().isEmpty()) {
            deletionError.setVisible(true);
            return;
        }
        deletionError.setVisible(false);
        voltAmpereProfileRepository.deleteById(voapList.getSelectionModel().getSelectedItem().getProfileName());
        voapList.getItems().remove(voapList.getSelectionModel().getSelectedItem());
        stage.close();
    }

    private void editVOAP() {

        VoltAmpereProfile vapToUpdate = currentVOAP;

        vapToUpdate.setBatteryU(batteryUSpinner.getValue());
        vapToUpdate.setBoostU(boostUSpinner.getValue());
        vapToUpdate.setBoostI(boostISpinner.getValue());
        vapToUpdate.setBoostDisable(!enableBoostToggleButton.isSelected());
        vapToUpdate.setFirstI(firstISpinner.getValue());
        vapToUpdate.setFirstW(firstWSpinner.getValue());
        vapToUpdate.setSecondI(secondISpinner.getValue());
        vapToUpdate.setNegativeU(negativeUSpinner.getValue());
        vapToUpdate.setDoubleCoil(coil2CheckBox.isSelected());

        if (coil2CheckBox.isSelected()) {
            vapToUpdate.setBoostI2(boostI2Spinner.getValue());
            vapToUpdate.setFirstI2(firstI2Spinner.getValue());
            vapToUpdate.setFirstW2(firstW2Spinner.getValue());
            vapToUpdate.setSecondI2(secondI2Spinner.getValue());
        }
        voltAmpereProfileRepository.save(vapToUpdate);
        voapList.getItems().setAll(voltAmpereProfileRepository.findByIsCustomAndInjectorType(true, currentVOAP.getInjectorType()));
        voapList.getSelectionModel().select(currentVOAP);
        voapList.scrollTo(currentVOAP);
        stage.close();
    }

    public void setNew() {
        currentState = State.NEW;
        enterNameTF.clear();
        deletionError.setVisible(false);
        disableNodes(false);
        enterNameTF.setDisable(false);
        if (mainSectionModel.manufacturerObjectProperty().get().getManufacturerName().equals("Denso")) {
            boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                    BOOST_U_SPINNER_MAX + 15,
                    BOOST_U_SPINNER_INIT,
                    BOOST_U_SPINNER_STEP));
        }
    }

    public void setCopy() {
        currentState = State.COPY;
        deletionError.setVisible(false);
        setValues();
        disableNodes(true);
        enterNameTF.setDisable(false);
    }

    public void setDelete() {
        currentState = State.DELETE;
        enterNameTF.setText(voapList.getSelectionModel().getSelectedItem().getProfileName());
        deletionError.setVisible(false);
        setValues();
        disableNodes(true);
        enterNameTF.setDisable(true);
    }

    public void setEdit() {
        currentState = State.EDIT;
        enterNameTF.setText(voapList.getSelectionModel().getSelectedItem().getProfileName());
        deletionError.setVisible(false);
        disableNodes(false);
        setValues();
        enterNameTF.setDisable(true);
    }

    private void disableNodes(boolean disable) {
        boostUSpinner.setDisable(disable);
        batteryUSpinner.setDisable(disable);
        boostISpinner.setDisable(disable);
        firstISpinner.setDisable(disable);
        firstWSpinner.setDisable(disable);
        secondISpinner.setDisable(disable);
        negativeUSpinner.setDisable(disable);
        enableBoostToggleButton.setDisable(disable);
        coil2CheckBox.setDisable(disable);

        if (currentState != State.NEW) {
            boostI2Spinner.setDisable(disable || !currentVOAP.isDoubleCoil());
            firstI2Spinner.setDisable(disable || !currentVOAP.isDoubleCoil());
            firstW2Spinner.setDisable(disable || !currentVOAP.isDoubleCoil());
            secondI2Spinner.setDisable(disable || !currentVOAP.isDoubleCoil());
        } else {
            boostI2Spinner.setDisable(disable || !coil2CheckBox.isSelected());
            firstI2Spinner.setDisable(disable || !coil2CheckBox.isSelected());
            firstW2Spinner.setDisable(disable || !coil2CheckBox.isSelected());
            secondI2Spinner.setDisable(disable || !coil2CheckBox.isSelected());
        }
    }

    private void setValues() {

        String typeName = currentVOAP.getInjectorType().getTypeName();
        if (typeName.equals("PIEZO") || typeName.equals("NEG_PIEZO")) {

            boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                    BOOST_U_SPINNER_MAX_PIEZO,
                    BOOST_U_SPINNER_INIT,
                    BOOST_U_SPINNER_STEP));
        } else if (mainSectionModel.manufacturerObjectProperty().get().getManufacturerName().equals("Denso")) {
            boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                    BOOST_U_SPINNER_MAX + 15,
                    BOOST_U_SPINNER_INIT,
                    BOOST_U_SPINNER_STEP));
        } else {
            boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(BOOST_U_SPINNER_MIN,
                    BOOST_U_SPINNER_MAX,
                    BOOST_U_SPINNER_INIT,
                    BOOST_U_SPINNER_STEP));
        }
        enterNameTF.setText(currentVOAP.getProfileName());
        batteryUSpinner.getValueFactory().setValue(currentVOAP.getBatteryU());
        boostUSpinner.getValueFactory().setValue(currentVOAP.getBoostU());
        boostISpinner.getValueFactory().setValue(currentVOAP.getBoostI());
        firstISpinner.getValueFactory().setValue(currentVOAP.getFirstI());
        firstWSpinner.getValueFactory().setValue(currentVOAP.getFirstW());
        secondISpinner.getValueFactory().setValue(currentVOAP.getSecondI());
        negativeUSpinner.getValueFactory().setValue(currentVOAP.getNegativeU());
        enableBoostToggleButton.setSelected(!currentVOAP.getBoostDisable());
        coil2CheckBox.setSelected(currentVOAP.isDoubleCoil());

        if (currentVOAP.isDoubleCoil()) {
            boostI2Spinner.getValueFactory().setValue(currentVOAP.getBoostI2());
            firstI2Spinner.getValueFactory().setValue(currentVOAP.getFirstI2());
            firstW2Spinner.getValueFactory().setValue(currentVOAP.getFirstW2());
            secondI2Spinner.getValueFactory().setValue(currentVOAP.getSecondI2());
        }
    }

    private String makeCode(String code) {
        codeBuilder.setLength(0);
        if (voltAmpereProfileRepository.existsById(code)) {
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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInjectorType(InjectorType injectorType) {
        this.injectorType = injectorType;
        currInjTypeLabel.setText(injectorType.getInjectorType());
    }

    void setCurrentVOAP(VoltAmpereProfile currentVOAP) {
        this.currentVOAP = currentVOAP;
    }
    void setVoapList(ListView<VoltAmpereProfile> voapList) {
        this.voapList = voapList;
    }

    private enum State {
        NEW, COPY, EDIT, DELETE
    }

    private void bindingI18N(){
        enterNameLabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.vapName"));
        injTypeLabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.injType"));
        coil1Label.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil1"));
        coil2CheckBox.textProperty().bind(i18N.createStringBinding("voapProfile.label.coil2"));
        boostU_enabled.bind(i18N.createStringBinding("voapProfile.button.boostU_enabled"));
        boostU_disabled.bind(i18N.createStringBinding("voapProfile.button.boostUdisabled"));
        boostULabel.textProperty().bind(i18N.createStringBinding("voapProfile.label.boostU"));
        batteryULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.voltageHold"));
        negativeULabel.textProperty().bind(i18N.createStringBinding("h4.voltage.label.firstNegativeVoltage"));
        boostI_1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        boostI_2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.currentBoost"));
        firstW_1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstW_2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.first"));
        firstI_1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        firstI_2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I1"));
        secondI_1Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        secondI_2Label.textProperty().bind(i18N.createStringBinding("h4.voltage.label.I2"));
        saveBtn.textProperty().bind(i18N.createStringBinding("h4.delay.button.save"));
        cancelBtn.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
        deletionError.textProperty().bind(i18N.createStringBinding("voapProfile.label.deletionError"));
    }
}
