package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.uis.CustomModelDialogModel;
import fi.stardex.sisu.model.uis.CustomVapUisDialogModel;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisVAP;
import fi.stardex.sisu.persistence.repos.uis.UisVapService;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.spinners.SpinnerManager;
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

    private CustomVapUisDialogModel customVapUisDialogModel;
    private CustomModelDialogModel customModelDialogModel;
    private UisVapService uisVapService;

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

    @PostConstruct
    public void init() {

        bipPwmSpinnerEnabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(15, 20, 15, 1);
        bipPwmSpinnerDisabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0, 0);
        bipWindowSpinnerEnabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(400, 800, 400, 100);
        bipWindowSpinnerDisabled = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0, 0);

        camTypeCB.getItems().setAll((Arrays.asList(1, 2)));
        camTypeCB.getSelectionModel().select(0);

        customModelDialogModel.customVapOperationProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;

            if (newVOAPStage == null) {
                newVOAPStage = new Stage();
                newVOAPStage.setScene(new Scene(newEditVOAPDialog));
                newVOAPStage.setResizable(false);
                newVOAPStage.initModality(Modality.APPLICATION_MODAL);
            }

            newVOAPStage.setTitle(customModelDialogModel.customVapOperationProperty().get().getTitle() + title);

            switch (customModelDialogModel.customVapOperationProperty().get()) {
                case EDIT:
                case COPY:
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
                case COPY:
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
                enableBoostToggleButton.setText("Boost_U enabled");
            } else {
                enableBoostToggleButton.setText("Boost_U disabled");
            }
        });

        customModelDialogModel.getInjectorType().addListener((observableValue,  oldValue, newValue) -> currInjTypeLabel.setText(newValue.name()));
        customModelDialogModel.getInjectorSubType().addListener((observableValue,  oldValue, newValue) -> {
            currInjSubTypeLabel.setText(newValue.name());
            activateCoil2Spinners(newValue == InjectorSubType.DOUBLE_COIL);
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
    }

    private void create() {

        if (uisVapService.existsById(enterNameTF.getText())) {
            notUniqueLabel.setVisible(true);
        } else {
            notUniqueLabel.setVisible(false);
            VAP newProfile = new InjectorUisVAP(
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
                    enableBoostToggleButton.isSelected(),
                    bipPwmSpinner.getValue(),
                    bipWindowSpinner.getValue());

            uisVapService.save(newProfile);
            customVapUisDialogModel.customVapProperty().setValue(newProfile);
            customVapUisDialogModel.doneProperty().setValue(new Object());
            newVOAPStage.close();
        }
    }

    private void delete() {

        uisVapService.delete(customModelDialogModel.customVapProperty().get());
        customVapUisDialogModel.doneProperty().setValue(new Object());
        newVOAPStage.close();
    }

    private void setNew() {

        bipCheckBox.setDisable(false);
        enterNameTF.setDisable(false);
        notUniqueLabel.setDisable(false);
        currInjTypeLabel.setDisable(false);
        currInjSubTypeLabel.setDisable(false);
        firstWSpinner.setDisable(false);
        batteryUSpinner.setDisable(false);
        boostISpinner.setDisable(false);
        boostUSpinner.setDisable(false);
        firstISpinner.setDisable(false);
        negativeUSpinner.setDisable(false);
        secondISpinner.setDisable(false);
        enableBoostToggleButton.setDisable(false);
        camTypeCB.setDisable(false);
        inletPressureSpinner.setDisable(false);
        bipPwmSpinner.setDisable(false);
        bipWindowSpinner.setDisable(false);
        activateCoil2Spinners(customModelDialogModel.getInjectorSubType().get() == InjectorSubType.DOUBLE_COIL);
    }

    private void setDelete() {

        bipCheckBox.setDisable(true);
        enterNameTF.setDisable(true);
        notUniqueLabel.setDisable(true);
        currInjTypeLabel.setDisable(true);
        currInjSubTypeLabel.setDisable(true);
        firstWSpinner.setDisable(true);
        batteryUSpinner.setDisable(true);
        boostISpinner.setDisable(true);
        boostUSpinner.setDisable(true);
        firstISpinner.setDisable(true);
        negativeUSpinner.setDisable(true);
        secondISpinner.setDisable(true);
        enableBoostToggleButton.setDisable(true);
        camTypeCB.setDisable(true);
        inletPressureSpinner.setDisable(true);
        bipPwmSpinner.setDisable(true);
        bipWindowSpinner.setDisable(true);
        firstI2Spinner.setDisable(true);
        secondI2Spinner.setDisable(true);
        boostI2Spinner.setDisable(true);
        firstW2Spinner.setDisable(true);
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
}
