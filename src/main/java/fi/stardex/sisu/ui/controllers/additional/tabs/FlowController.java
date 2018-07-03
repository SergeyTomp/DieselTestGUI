package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.combobox_values.FlowUnits;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import javax.annotation.PostConstruct;

public class FlowController {

    @FXML
    private TextField delivery1TextField;

    @FXML
    private TextField delivery2TextField;

    @FXML
    private TextField delivery3TextField;

    @FXML
    private TextField delivery4TextField;

    @FXML
    private TextField backFlow1TextField;

    @FXML
    private TextField backFlow2TextField;

    @FXML
    private TextField backFlow3TextField;

    @FXML
    private TextField backFlow4TextField;

    @FXML
    private ComboBox<String> deliveryFlowComboBox;

    @FXML
    private ComboBox<String> backFlowComboBox;
    
    @FXML
    private Label temperature1Delivery1;

    @FXML
    private Label temperature1Delivery2;

    @FXML
    private Label temperature1Delivery3;

    @FXML
    private Label temperature1Delivery4;

    @FXML
    private Label temperature2Delivery1;

    @FXML
    private Label temperature2Delivery2;

    @FXML
    private Label temperature2Delivery3;

    @FXML
    private Label temperature2Delivery4;

    @FXML
    private Label temperature1BackFlow1;

    @FXML
    private Label temperature1BackFlow2;

    @FXML
    private Label temperature1BackFlow3;

    @FXML
    private Label temperature1BackFlow4;

    @FXML
    private Label temperature2BackFlow1;

    @FXML
    private Label temperature2BackFlow2;

    @FXML
    private Label temperature2BackFlow3;

    @FXML
    private Label temperature2BackFlow4;

    @FXML
    private AnchorPane beakerDelivery1;

    @FXML
    private AnchorPane beakerDelivery2;

    @FXML
    private AnchorPane beakerDelivery3;

    @FXML
    private AnchorPane beakerDelivery4;

    @FXML
    private AnchorPane beakerBackFlow1;

    @FXML
    private AnchorPane beakerBackFlow2;

    @FXML
    private AnchorPane beakerBackFlow3;

    @FXML
    private AnchorPane beakerBackFlow4;

    @FXML
    private BeakerController beakerDelivery1Controller;

    @FXML
    private BeakerController beakerDelivery2Controller;

    @FXML
    private BeakerController beakerDelivery3Controller;

    @FXML
    private BeakerController beakerDelivery4Controller;

    @FXML
    private BeakerController beakerBackFlow1Controller;

    @FXML
    private BeakerController beakerBackFlow2Controller;

    @FXML
    private BeakerController beakerBackFlow3Controller;

    @FXML
    private BeakerController beakerBackFlow4Controller;

    public Label getTemperature1Delivery1() {
        return temperature1Delivery1;
    }

    public Label getTemperature1Delivery2() {
        return temperature1Delivery2;
    }

    public Label getTemperature1Delivery3() {
        return temperature1Delivery3;
    }

    public Label getTemperature1Delivery4() {
        return temperature1Delivery4;
    }

    public Label getTemperature2Delivery1() {
        return temperature2Delivery1;
    }

    public Label getTemperature2Delivery2() {
        return temperature2Delivery2;
    }

    public Label getTemperature2Delivery3() {
        return temperature2Delivery3;
    }

    public Label getTemperature2Delivery4() {
        return temperature2Delivery4;
    }

    public Label getTemperature1BackFlow1() {
        return temperature1BackFlow1;
    }

    public Label getTemperature1BackFlow2() {
        return temperature1BackFlow2;
    }

    public Label getTemperature1BackFlow3() {
        return temperature1BackFlow3;
    }

    public Label getTemperature1BackFlow4() {
        return temperature1BackFlow4;
    }

    public Label getTemperature2BackFlow1() {
        return temperature2BackFlow1;
    }

    public Label getTemperature2BackFlow2() {
        return temperature2BackFlow2;
    }

    public Label getTemperature2BackFlow3() {
        return temperature2BackFlow3;
    }

    public Label getTemperature2BackFlow4() {
        return temperature2BackFlow4;
    }

    public BeakerController getBeakerDelivery1Controller() {
        return beakerDelivery1Controller;
    }

    public BeakerController getBeakerDelivery2Controller() {
        return beakerDelivery2Controller;
    }

    public BeakerController getBeakerDelivery3Controller() {
        return beakerDelivery3Controller;
    }

    public BeakerController getBeakerDelivery4Controller() {
        return beakerDelivery4Controller;
    }

    public BeakerController getBeakerBackFlow1Controller() {
        return beakerBackFlow1Controller;
    }

    public BeakerController getBeakerBackFlow2Controller() {
        return beakerBackFlow2Controller;
    }

    public BeakerController getBeakerBackFlow3Controller() {
        return beakerBackFlow3Controller;
    }

    public BeakerController getBeakerBackFlow4Controller() {
        return beakerBackFlow4Controller;
    }

    public TextField getDelivery1TextField() {
        return delivery1TextField;
    }

    public TextField getDelivery2TextField() {
        return delivery2TextField;
    }

    public TextField getDelivery3TextField() {
        return delivery3TextField;
    }

    public TextField getDelivery4TextField() {
        return delivery4TextField;
    }

    public TextField getBackFlow1TextField() {
        return backFlow1TextField;
    }

    public TextField getBackFlow2TextField() {
        return backFlow2TextField;
    }

    public TextField getBackFlow3TextField() {
        return backFlow3TextField;
    }

    public TextField getBackFlow4TextField() {
        return backFlow4TextField;
    }

    public ComboBox<String> getBackFlowComboBox() {
        return backFlowComboBox;
    }

    public ComboBox<String> getDeliveryFlowComboBox() {
        return deliveryFlowComboBox;
    }

    private static final int TEXT_FIELD_MAX_LENGTH = 7;

    @PostConstruct
    private void init() {

        setupDeliveryAndBackFlowComboBox();

        blockTextInputToDeliveryBackFlowTextFields(delivery1TextField);
        blockTextInputToDeliveryBackFlowTextFields(delivery2TextField);
        blockTextInputToDeliveryBackFlowTextFields(delivery3TextField);
        blockTextInputToDeliveryBackFlowTextFields(delivery4TextField);
        blockTextInputToDeliveryBackFlowTextFields(backFlow1TextField);
        blockTextInputToDeliveryBackFlowTextFields(backFlow2TextField);
        blockTextInputToDeliveryBackFlowTextFields(backFlow3TextField);
        blockTextInputToDeliveryBackFlowTextFields(backFlow4TextField);

    }

    private void setupDeliveryAndBackFlowComboBox() {

        deliveryFlowComboBox.getItems().setAll(FlowUnits.getArrayOfFlowUnits());
        deliveryFlowComboBox.getSelectionModel().selectFirst();

        backFlowComboBox.getItems().setAll(FlowUnits.getArrayOfFlowUnits());
        backFlowComboBox.getSelectionModel().selectFirst();

    }

    private void blockTextInputToDeliveryBackFlowTextFields(TextField field) {

        field.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            TextField txt_TextField = (TextField) e.getSource();
            if (txt_TextField != null && txt_TextField.getText() != null) {
                if (txt_TextField.getText().length() >= TEXT_FIELD_MAX_LENGTH) {
                    e.consume();
                }
                if (e.getCharacter().matches("[0-9.]")) {
                    if (txt_TextField.getText().contains(".") && e.getCharacter().matches("[.]")) {
                        e.consume();
                    } else if (txt_TextField.getText().length() == 0 && e.getCharacter().matches("[.]")) {
                        e.consume();
                    }
                } else {
                    e.consume();
                }
            }

        });

    }

}
