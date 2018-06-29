package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorType;
import fi.stardex.sisu.persistence.orm.cr.inj.VOAP;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class CRUDInjectorDialogController {
    private Logger logger = LoggerFactory.getLogger(CRUDInjectorDialogController.class);

    @FXML
    private ComboBox<InjectorType> typeComboBox;
    @FXML
    private Label boostULabel;
    @FXML
    private Label boostILabel;
    @FXML
    private Label batteryULabel;
    @FXML
    private Label negativeU1Label;
    @FXML
    private Label negativeU2Label;
    @FXML
    private TextField boostUTextField;
    @FXML
    private TextField firstWTextField;
    @FXML
    private TextField firstITextField;
    @FXML
    private TextField secondITextField;
    @FXML
    private TextField boostITextField;
    @FXML
    private TextField batteryUTextField;
    @FXML
    private TextField negativeU1TextField;
    @FXML
    private TextField negativeU2TextField;
    @FXML
    private ComboBox<VOAP> profileComboBox;
    @FXML
    private Button acceptButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField injectorNameTextField;
    @FXML
    private Button addProfileButton;
    @FXML
    private Button removeProfileButton;
    @FXML
    private Label noBoostLabel;
    @FXML
    private CheckBox noBoostCheckBox;
    @FXML
    public ToggleGroup dialogType;

    private Stage stage;



    @PostConstruct
    private void init() {
    }

}
