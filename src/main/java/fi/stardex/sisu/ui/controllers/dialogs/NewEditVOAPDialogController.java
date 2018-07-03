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
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

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

    @Autowired
    private VoltAmpereProfileRepository voltAmpereProfileRepository;

    private InjectorType injectorType;
    private State currentState;
    private Stage stage;
    private ListView<VoltAmpereProfile> voapList;

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
        firstWSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(90, 15500, 500, 10));
        boostISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(3, 25.5, 21.5, 0.1));
        firstISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(2, 25.5, 15, 0.1));
        secondISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 25.5, 5.5, 0.1));
        batteryUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(11, 32, 20, 1));
        negativeUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(17, 121, 48, 1));
        boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 75, 60, 1));

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
