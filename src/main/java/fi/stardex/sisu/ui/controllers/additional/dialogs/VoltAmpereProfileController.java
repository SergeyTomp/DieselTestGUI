package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.ui.controllers.additional.tabs.VoltageController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class VoltAmpereProfileController {

    @Autowired
    private VoltageController voltageController;

    @FXML
    private Spinner firstWSpinner;

    @FXML
    private Spinner batteruUSpinner;

    @FXML
    private Spinner boostISpinner;

    @FXML
    private Spinner boostUSpinner;

    @FXML
    private Spinner firstISpinner;

    @FXML
    private Spinner negativeU1Spinner;

    @FXML
    private Spinner secondISpinner;

    @FXML
    private Spinner negativeU2Spinner;

    @FXML
    private ToggleButton enableBoostToggleButton;

    @FXML
    private Button applyBtn;

    @FXML
    private Button cancelBtn;

    @PostConstruct
    private void init() {
        applyBtn.setOnMouseClicked(event -> {
            System.err.println(voltageController);
        });
    }
}
