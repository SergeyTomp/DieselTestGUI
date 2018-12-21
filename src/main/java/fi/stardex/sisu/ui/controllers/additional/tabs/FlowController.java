package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import fi.stardex.sisu.util.InputController;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.FlowUnitObtainer.*;

public class FlowController {

    @FXML private Label backFlowLabel;

    @FXML private Label deliveryLabel;

    @FXML private Label ml_Min_DeliveryLabel;

    @FXML private Label ml_Min_BackFlowLabel;

    @FXML private Label deliveryRangeLabel;

    @FXML private Label backFlowRangeLabel;

    @FXML private TextField delivery1TextField;

    @FXML private TextField delivery2TextField;

    @FXML private TextField delivery3TextField;

    @FXML private TextField delivery4TextField;

    @FXML private TextField backFlow1TextField;

    @FXML private TextField backFlow2TextField;

    @FXML private TextField backFlow3TextField;

    @FXML private TextField backFlow4TextField;

    @FXML private ComboBox<String> deliveryFlowComboBox;

    @FXML private ComboBox<String> backFlowComboBox;

    @FXML private Label temperature1Delivery1;

    @FXML private Label temperature1Delivery2;

    @FXML private Label temperature1Delivery3;

    @FXML private Label temperature1Delivery4;

    @FXML private Label temperature2Delivery1;

    @FXML private Label temperature2Delivery2;

    @FXML private Label temperature2Delivery3;

    @FXML private Label temperature2Delivery4;

    @FXML private Label temperature1BackFlow1;

    @FXML private Label temperature1BackFlow2;

    @FXML private Label temperature1BackFlow3;

    @FXML private Label temperature1BackFlow4;

    @FXML private Label temperature2BackFlow1;

    @FXML private Label temperature2BackFlow2;

    @FXML private Label temperature2BackFlow3;

    @FXML private Label temperature2BackFlow4;

    @FXML private AnchorPane beakerDelivery1;

    @FXML private AnchorPane beakerDelivery2;

    @FXML private AnchorPane beakerDelivery3;

    @FXML private AnchorPane beakerDelivery4;

    @FXML private AnchorPane beakerBackFlow1;

    @FXML private AnchorPane beakerBackFlow2;

    @FXML private AnchorPane beakerBackFlow3;

    @FXML private AnchorPane beakerBackFlow4;

    @FXML private BeakerController beakerDelivery1Controller;

    @FXML private BeakerController beakerDelivery2Controller;

    @FXML private BeakerController beakerDelivery3Controller;

    @FXML private BeakerController beakerDelivery4Controller;

    @FXML private BeakerController beakerBackFlow1Controller;

    @FXML private BeakerController beakerBackFlow2Controller;

    @FXML private BeakerController beakerBackFlow3Controller;

    @FXML private BeakerController beakerBackFlow4Controller;

    private I18N i18N;

    private FlowValuesModel flowValuesModel;
    private BackFlowRangeModel backFlowRangeModel;
    private BackFlowUnitsModel backFlowUnitsModel;
    private DeliveryFlowRangeModel deliveryFlowRangeModel;
    private DeliveryFlowUnitsModel deliveryFlowUnitsModel;

    private static final int TEXT_FIELD_MAX_LENGTH = 7;

    private double[] currentDeliveryFlowLevels;

    private double[] currentBackFlowLevels;

    private ObjectProperty<String> deliveryRangeLabelProperty = new SimpleObjectProperty<>();

    private ObjectProperty<String> backFlowRangeLabelProperty = new SimpleObjectProperty<>();

    public Label getDeliveryRangeLabel() {
        return deliveryRangeLabel;
    }

    public Label getBackFlowRangeLabel() {
        return backFlowRangeLabel;
    }

    public Label getMl_Min_DeliveryLabel() {
        return ml_Min_DeliveryLabel;
    }

    public Label getMl_Min_BackFlowLabel() {
        return ml_Min_BackFlowLabel;
    }

    public ObjectProperty<String> deliveryRangeLabelPropertyProperty() {
        return deliveryRangeLabelProperty;
    }

    public ObjectProperty<String> backFlowRangeLabelPropertyProperty() {
        return backFlowRangeLabelProperty;
    }

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

    public double[] getCurrentDeliveryFlowLevels() {
        return currentDeliveryFlowLevels;
    }

    public double[] getCurrentBackFlowLevels() {
        return currentBackFlowLevels;
    }

    public void setCurrentDeliveryFlowLevels(double[] currentDeliveryFlowLevels) {
        this.currentDeliveryFlowLevels = currentDeliveryFlowLevels;
    }

    public void setCurrentBackFlowLevels(double[] currentBackFlowLevels) {
        this.currentBackFlowLevels = currentBackFlowLevels;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setFlowValuesModel(FlowValuesModel flowValuesModel) {
        this.flowValuesModel = flowValuesModel;
    }

    public void setBackFlowRangeModel(BackFlowRangeModel backFlowRangeModel) {
        this.backFlowRangeModel = backFlowRangeModel;
    }

    public void setBackFlowUnitsModel(BackFlowUnitsModel backFlowUnitsModel) {
        this.backFlowUnitsModel = backFlowUnitsModel;
    }

    public void setDeliveryFlowRangeModel(DeliveryFlowRangeModel deliveryFlowRangeModel) {
        this.deliveryFlowRangeModel = deliveryFlowRangeModel;
    }

    public void setDeliveryFlowUnitsModel(DeliveryFlowUnitsModel deliveryFlowUnitsModel) {
        this.deliveryFlowUnitsModel = deliveryFlowUnitsModel;
    }

    @PostConstruct
    private void init() {

        bindingI18N();

        bindProperties();

        setupDeliveryFlowComboBox();
        setupBackFlowComboBox();

        InputController.blockTextInputToNumberFields(delivery1TextField, TEXT_FIELD_MAX_LENGTH);
        InputController.blockTextInputToNumberFields(delivery2TextField, TEXT_FIELD_MAX_LENGTH);
        InputController.blockTextInputToNumberFields(delivery3TextField, TEXT_FIELD_MAX_LENGTH);
        InputController.blockTextInputToNumberFields(delivery4TextField, TEXT_FIELD_MAX_LENGTH);
        InputController.blockTextInputToNumberFields(backFlow1TextField, TEXT_FIELD_MAX_LENGTH);
        InputController.blockTextInputToNumberFields(backFlow2TextField, TEXT_FIELD_MAX_LENGTH);
        InputController.blockTextInputToNumberFields(backFlow3TextField, TEXT_FIELD_MAX_LENGTH);
        InputController.blockTextInputToNumberFields(backFlow4TextField, TEXT_FIELD_MAX_LENGTH);

        flowValuesModel.backFlow1Property().bind(backFlow1TextField.textProperty());
        flowValuesModel.backFlow2Property().bind(backFlow2TextField.textProperty());
        flowValuesModel.backFlow3Property().bind(backFlow3TextField.textProperty());
        flowValuesModel.backFlow4Property().bind(backFlow4TextField.textProperty());
        flowValuesModel.delivery1Property().bind(delivery1TextField.textProperty());
        flowValuesModel.delivery2Property().bind(delivery2TextField.textProperty());
        flowValuesModel.delivery3Property().bind(delivery3TextField.textProperty());
        flowValuesModel.delivery4Property().bind(delivery4TextField.textProperty());

        deliveryFlowUnitsModel.deliveryFlowUnitsProperty().bind(deliveryFlowComboBox.valueProperty());
        deliveryFlowRangeModel.deliveryFlowRangeProperty().bind(deliveryRangeLabel.textProperty());
        backFlowUnitsModel.backFlowUnitsProperty().bind(backFlowComboBox.valueProperty());
        backFlowRangeModel.backFlowRangeProperty().bind(backFlowRangeLabel.textProperty());
    }

    private void setupDeliveryFlowComboBox() {

        deliveryFlowComboBox.getItems().setAll(MILLILITRE_PER_MINUTE, LITRE_PER_HOUR);
        createDeliveryFlowUnitBinding(deliveryFlowComboBox);
        deliveryFlowComboBox.getSelectionModel().selectFirst();

    }

    private void setupBackFlowComboBox() {

        backFlowComboBox.getItems().setAll(MILLILITRE_PER_MINUTE, LITRE_PER_HOUR);
        createBackFlowUnitBinding(backFlowComboBox);
        backFlowComboBox.getSelectionModel().selectFirst();

    }

    private void bindingI18N() {
        deliveryLabel.textProperty().bind(i18N.createStringBinding("h4.flow.label.delivery"));
        backFlowLabel.textProperty().bind(i18N.createStringBinding("h4.flow.label.backflow"));
    }

    private void bindProperties() {

        deliveryRangeLabelProperty.bind(deliveryRangeLabel.textProperty());

        backFlowRangeLabelProperty.bind(backFlowRangeLabel.textProperty());

    }

    public void changeFlow(TextField field, String value) {

        if (field == delivery1TextField)
            changeFlow(deliveryRangeLabel, beakerDelivery1Controller, value);
        else if (field == delivery2TextField)
            changeFlow(deliveryRangeLabel, beakerDelivery2Controller, value);
        else if (field == delivery3TextField)
            changeFlow(deliveryRangeLabel, beakerDelivery3Controller, value);
        else if (field == delivery4TextField)
            changeFlow(deliveryRangeLabel, beakerDelivery4Controller, value);
        else if (field == backFlow1TextField)
            changeFlow(backFlowRangeLabel, beakerBackFlow1Controller, value);
        else if (field == backFlow2TextField)
            changeFlow(backFlowRangeLabel, beakerBackFlow2Controller, value);
        else if (field == backFlow3TextField)
            changeFlow(backFlowRangeLabel, beakerBackFlow3Controller, value);
        else if (field == backFlow4TextField)
            changeFlow(backFlowRangeLabel, beakerBackFlow4Controller, value);

    }

    private void changeFlow(Label rangeLabel, BeakerController beakerController, String value) {

        if (!rangeLabel.getText().isEmpty())
            beakerController.changeFlow(value);
        else
            beakerController.getTextField().setText(value);

    }

}
