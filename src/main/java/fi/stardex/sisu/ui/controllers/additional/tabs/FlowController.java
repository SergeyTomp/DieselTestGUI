package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.formulas.Formula;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.core.OrderComparator;

import javax.annotation.PostConstruct;
import java.util.List;

public class FlowController {
    
    @FXML
    private ComboBox<Formula> deliveryFlowComboBox;

    @FXML
    private ComboBox<Formula> backFlowComboBox;
    
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
    private AnchorPane beakerFlowDelivery1;

    @FXML
    private AnchorPane beakerFlowDelivery2;

    @FXML
    private AnchorPane beakerFlowDelivery3;

    @FXML
    private AnchorPane beakerFlowDelivery4;

    @FXML
    private AnchorPane beakerBackFlow1;

    @FXML
    private AnchorPane beakerBackFlow2;

    @FXML
    private AnchorPane beakerBackFlow3;

    @FXML
    private AnchorPane beakerBackFlow4;

    @FXML
    private BeakerController beakerFlowDelivery1Controller;

    @FXML
    private BeakerController beakerFlowDelivery2Controller;

    @FXML
    private BeakerController beakerFlowDelivery3Controller;

    @FXML
    private BeakerController beakerFlowDelivery4Controller;

    @FXML
    private BeakerController beakerBackFlow1Controller;

    @FXML
    private BeakerController beakerBackFlow2Controller;

    @FXML
    private BeakerController beakerBackFlow3Controller;

    @FXML
    private BeakerController beakerBackFlow4Controller;

    private List<Formula> formulas;

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

    public BeakerController getBeakerFlowDelivery1Controller() {
        return beakerFlowDelivery1Controller;
    }

    public BeakerController getBeakerFlowDelivery2Controller() {
        return beakerFlowDelivery2Controller;
    }

    public BeakerController getBeakerFlowDelivery3Controller() {
        return beakerFlowDelivery3Controller;
    }

    public BeakerController getBeakerFlowDelivery4Controller() {
        return beakerFlowDelivery4Controller;
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

    public ComboBox getBackFlowComboBox() {
        return backFlowComboBox;
    }

    public ComboBox getDeliveryFlowComboBox() {
        return deliveryFlowComboBox;
    }

    public void setFormulasList(List<Formula> formulas) {
        this.formulas = formulas;
    }
    
    @PostConstruct
    private void init() {

        setupDeliveryAndBackFlowComboBox();

    }

    private void setupDeliveryAndBackFlowComboBox() {

        formulas.sort(new OrderComparator());
        deliveryFlowComboBox.getItems().setAll(formulas);
        deliveryFlowComboBox.getSelectionModel().select(0);

        backFlowComboBox.getItems().setAll(formulas);
        backFlowComboBox.getSelectionModel().select(0);

    }

}
