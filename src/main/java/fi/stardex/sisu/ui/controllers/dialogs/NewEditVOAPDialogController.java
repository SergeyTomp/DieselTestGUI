package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorType;
import fi.stardex.sisu.persistence.orm.cr.inj.VoltAmpereProfile;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.util.spinners.SpinnerManager;
import fi.stardex.sisu.util.spinners.SpinnerValueObtainer;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class NewEditVOAPDialogController {

    @FXML
    private TextField enterNameTF;
    @FXML
    private Label notUniqueLabel;
    @FXML
    private Label currInjTypeLabel;
    @FXML
    private Spinner<Integer> firstWSpinner;
    @FXML
    private Spinner<Integer> batteryUSpinner;
    @FXML
    private Spinner<Double> boostISpinner;
    @FXML
    private Spinner<Integer> boostUSpinner;
    @FXML
    private Spinner<Double> firstISpinner;
    @FXML
    private Spinner<Integer> negativeUSpinner;
    @FXML
    private Spinner<Double> secondISpinner;
    @FXML
    private ToggleButton enableBoostToggleButton;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private VoltAmpereProfileRepository voltAmpereProfileRepository;

    private InjectorType injectorType;

    private State currentState;

    private Stage stage;

    private ListView<VoltAmpereProfile> voapList;

    public void setVoltAmpereProfileRepository(VoltAmpereProfileRepository voltAmpereProfileRepository) {
        this.voltAmpereProfileRepository = voltAmpereProfileRepository;
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
                enableBoostToggleButton.setText("Boost_U enabled");
            } else {
                enableBoostToggleButton.setText("Boost_U disabled");
            }
        });

        //TODO: Boost U для Piezo/PiezoDelphi [30; 350]
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

        SpinnerManager.setupSpinner(firstWSpinner, FIRST_W_SPINNER_INIT, FIRST_W_SPINNER_MIN, FIRST_W_SPINNER_MAX, new CustomTooltip(), new SpinnerValueObtainer(FIRST_W_SPINNER_INIT));
        SpinnerManager.setupSpinner(boostISpinner, BOOST_I_SPINNER_INIT, BOOST_I_SPINNER_FAKE, new CustomTooltip(), new SpinnerValueObtainer(BOOST_I_SPINNER_INIT));
        SpinnerManager.setupSpinner(firstISpinner, FIRST_I_SPINNER_INIT, FIRST_I_SPINNER_FAKE, new CustomTooltip(), new SpinnerValueObtainer(FIRST_I_SPINNER_INIT));
        SpinnerManager.setupSpinner(secondISpinner, FIRST_I_SPINNER_INIT, SECOND_I_SPINNER_FAKE, new CustomTooltip(), new SpinnerValueObtainer(FIRST_I_SPINNER_INIT));
        SpinnerManager.setupSpinner(batteryUSpinner, BATTERY_U_SPINNER_INIT, BATTERY_U_SPINNER_MIN, BATTERY_U_SPINNER_MAX, new CustomTooltip(), new SpinnerValueObtainer(BATTERY_U_SPINNER_INIT));
        SpinnerManager.setupSpinner(negativeUSpinner, NEGATIVE_U_SPINNER_INIT, NEGATIVE_U_SPINNER_MIN, NEGATIVE_U_SPINNER_MAX, new CustomTooltip(), new SpinnerValueObtainer(NEGATIVE_U_SPINNER_INIT));
        SpinnerManager.setupSpinner(boostUSpinner, BOOST_U_SPINNER_INIT, BOOST_U_SPINNER_MIN, BOOST_U_SPINNER_MAX, new CustomTooltip(), new SpinnerValueObtainer(BOOST_U_SPINNER_INIT));
    }

    private void checkAndSaveVOAP() {
        if (voltAmpereProfileRepository.existsById(enterNameTF.getText())) {
            notUniqueLabel.setVisible(true);
        } else {
            notUniqueLabel.setVisible(false);
            VoltAmpereProfile newProfile = new VoltAmpereProfile(enterNameTF.getText(), injectorType, true, boostUSpinner.getValue(), batteryUSpinner.getValue(),
                    boostISpinner.getValue(), firstISpinner.getValue(), firstWSpinner.getValue(), secondISpinner.getValue(),
                    negativeUSpinner.getValue(), enableBoostToggleButton.isSelected());
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
}
