package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import fi.stardex.sisu.util.storage.CurrentVAPStorage;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class VoltAmpereProfileController {

    @Autowired
    private VoltageController voltageController;

    @Autowired
    private CurrentVAPStorage currentVAPStorage;

    @FXML
    private Spinner<Integer> firstWSpinner;

    @FXML
    private Spinner<Integer> batteruUSpinner;

    @FXML
    private Spinner<Double> boostISpinner;

    @FXML
    private Spinner<Integer> boostUSpinner;

    @FXML
    private Spinner<Double> firstISpinner;

    @FXML
    private Spinner<Integer> negativeU1Spinner;

    @FXML
    private Spinner<Double> secondISpinner;

    @FXML
    private Spinner<Integer> negativeU2Spinner;

    @FXML
    private ToggleButton enableBoostToggleButton;

    @FXML
    private Button applyBtn;

    @FXML
    private Button cancelBtn;

    @PostConstruct
    private void init() {
        firstWSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(91, 15510, 500, 10));
        boostISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(3, 25, 21.5, 0.1));
        firstISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(2, 25, 15, 0.1));
        secondISpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 25, 5.5, 0.1));
        batteruUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(11, 32, 20, 1));
        negativeU1Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(17, 100, 48, 1));
        negativeU2Spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(12, 70, 36, 1));

        boostUSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(40, 75, 60, 1));

        firstWSpinner.setEditable(true);
        boostISpinner.setEditable(true);
        firstISpinner.setEditable(true);
        secondISpinner.setEditable(true);
        batteruUSpinner.setEditable(true);
        negativeU1Spinner.setEditable(true);
        negativeU2Spinner.setEditable(true);
        boostUSpinner.setEditable(true);

        applyBtn.setOnMouseClicked(event -> currentVAPStorage.calculateAndSet(firstWSpinner.getValue(), batteruUSpinner.getValue(), boostUSpinner.getValue(),
                firstISpinner.getValue(), boostISpinner.getValue(), secondISpinner.getValue(),
                negativeU1Spinner.getValue(), negativeU2Spinner.getValue()));
    }
}