package fi.stardex.sisu.ui.controllers.cr.dialogs;

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

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class NewEditVOAPDialogController {

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
                    enableBoostToggleButton.isSelected(),
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

    public void setNew() {
        currentState = State.NEW;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInjectorType(InjectorType injectorType) {
        this.injectorType = injectorType;
        currInjTypeLabel.setText(injectorType.getInjectorType());
    }

    public void setVoapList(ListView<VoltAmpereProfile> voapList) {
        this.voapList = voapList;
    }

    private enum State {
        NEW
    }

    private void bindingI18N(){
        enterNameLabel.textProperty().bind(i18N.createStringBinding("h4.report.table.label.injectorName"));
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
    }
}
