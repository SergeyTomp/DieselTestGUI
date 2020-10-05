package fi.stardex.sisu.ui.controllers.pumps.flow;

import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.common.BeakerController;
import fi.stardex.sisu.util.InputController;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.enums.Dimension;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.rescalers.Rescaler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.PreciseCellChoice;
import static fi.stardex.sisu.util.FlowUnitObtainer.*;
import static fi.stardex.sisu.util.converters.DataConverter.*;
import static fi.stardex.sisu.util.enums.BeakerType.DELIVERY;

public class PumpBeakerController {

    @FXML private Label precisionLabel;
    @FXML private ComboBox<String> precisionCB;
    @FXML private ComboBox<String> flowComboBox;
    @FXML private Label flowLabel;
    @FXML private Label flowRangeLabel;
    @FXML private TextField flowTextField;
    @FXML private Label temperature1Flow;
    @FXML private Label temperature2Flow;

    @FXML private StackPane beakerStackPane;
    @FXML private AnchorPane beakerPane;
    @FXML private Rectangle rectangleBeaker;
    @FXML private Ellipse ellipseTopBeaker;
    @FXML private Ellipse ellipseBottomBeaker;
    @FXML private Rectangle rectangleFuel;
    @FXML private Ellipse ellipseBottomFuel;
    @FXML private Ellipse ellipseTopFuel;
    @FXML private Arc arcTickTop;
    @FXML private Arc arcTickBottom;
    @FXML private Text textTop;
    @FXML private Text textBottom;
    @FXML private ImageView imageViewBottom;
    @FXML private ImageView imageViewCenter;
    @FXML private ImageView imageViewTop;
    @FXML private Line lineLeft;
    @FXML private Line lineRight;

    private BeakerType beakerType;
    private Rescaler rescaler;
    private String name;
    private boolean noTopLimit;
    private boolean noLowLimit;
    private float currentMaxLevel;
    private double[] currentFlowLevels;
    private StringBuilder convertedValue = new StringBuilder();

    private FlowRangeModel flowRangeModel;
    private FlowUnitsModel flowUnitsModel;

    private PumpFlowValuesModel pumpFlowValuesModel;
    private PumpFlowTemperaturesModel pumpFlowTemperaturesModel;
    private PumpTestModel pumpTestModel;
    private FlowViewModel flowViewModel;
    private PumpReportModel pumpReportModel;
    private ModbusRegisterProcessor flowModbusWriter;
    private Preferences rootPrefs;

    private I18N i18N;
    private static final Logger logger = LoggerFactory.getLogger(BeakerController.class);
    private static final int ELLIPSE_TOP_FUEL_DEVIATION = 8;
    private final static int ARC_DEVIATION = 11;
    private final static double PERCENT_075 = 0.75;
    private final static double PERCENT_025 = 0.25;
    private final static int TEXT_DEVIATION = 3;
    private static final String REGEX = "[0-9.]*[^.]";
    private static final String MILLILITRE_PER_MINUTE = "ml/min";
    private static final String LITRE_PER_HOUR = "l/h";
    private static final int TEXT_FIELD_MAX_LENGTH = 7;
    private static final String DEGREES_CELSIUS = " \u2103";

    public void setRescaler(Rescaler rescaler) {
        this.rescaler = rescaler;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBeakerType(BeakerType beakerType) {
        this.beakerType = beakerType;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpFlowValuesModel(PumpFlowValuesModel pumpFlowValuesModel) {
        this.pumpFlowValuesModel = pumpFlowValuesModel;
    }
    public void setPumpFlowTemperaturesModel(PumpFlowTemperaturesModel pumpFlowTemperaturesModel) {
        this.pumpFlowTemperaturesModel = pumpFlowTemperaturesModel;
    }
    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }
    public void setFlowViewModel(FlowViewModel flowViewModel) {
        this.flowViewModel = flowViewModel;
    }
    public void setFlowRangeModel(FlowRangeModel flowRangeModel) {
        this.flowRangeModel = flowRangeModel;
    }
    public void setFlowUnitsModel(FlowUnitsModel flowUnitsModel) {
        this.flowUnitsModel = flowUnitsModel;
    }
    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }
    public void setFlowModbusWriter(ModbusRegisterProcessor flowModbusWriter) {
        this.flowModbusWriter = flowModbusWriter;
    }
    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init() {

        setupRescaler();
        setupResizeable();
        bindingI18N();
        setupBindings();
        setupListeners();
        setupFlowComboBox();

        switch (beakerType){
            case DELIVERY:
                precisionCB.getItems().addAll("HIGH", "NORM");
                precisionCB.getSelectionModel().select(rootPrefs.get("sensorType", "HIGH"));
                precisionCB.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue)
                        -> rootPrefs.put("sensorType", newValue));
                precisionCB.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue)
                        -> flowModbusWriter.add(PreciseCellChoice, newValue.equals("HIGH")));
//                flowModbusWriter.add(PreciseCellChoice, precisionCB.getSelectionModel().getSelectedItem().equals("HIGH"));
                break;
            case BACKFLOW:
                precisionCB.setVisible(false);
                precisionLabel.setVisible(false);
                break;
        }
        precisionCB.setVisible(false);
        precisionLabel.setVisible(false);
    }

    private void bindingI18N(){

        switch (beakerType){
            case DELIVERY:
                flowLabel.textProperty().bind(i18N.createStringBinding("h4.flow.label.delivery"));
                break;
            case BACKFLOW:
                flowLabel.textProperty().bind(i18N.createStringBinding("h4.flow.label.backflow"));
                break;
        }
    }

    private void setupResizeable() {

        rectangleBeaker.heightProperty().bind(((StackPane) beakerPane.getParent()).heightProperty());
        rectangleBeaker.widthProperty().bind(((StackPane) beakerPane.getParent()).widthProperty());
        ellipseTopBeaker.radiusXProperty().bind(((StackPane) beakerPane.getParent()).widthProperty().divide(2));
        ellipseBottomBeaker.radiusXProperty().bind(((StackPane) beakerPane.getParent()).widthProperty().divide(2));
        ellipseTopFuel.radiusXProperty().bind(((StackPane) beakerPane.getParent()).widthProperty().divide(2.1));
        ellipseBottomFuel.radiusXProperty().bind(((StackPane) beakerPane.getParent()).widthProperty().divide(2.1));
        arcTickTop.radiusXProperty().bind(((StackPane) beakerPane.getParent()).widthProperty().divide(2));
        arcTickBottom.radiusXProperty().bind(((StackPane) beakerPane.getParent()).widthProperty().divide(2));
        rectangleFuel.widthProperty().bind(((StackPane) beakerPane.getParent()).widthProperty().divide(1.055));
        imageViewCenter.fitHeightProperty().bind(rectangleBeaker.heightProperty().subtract(79));
        imageViewTop.fitWidthProperty().bind(((StackPane) beakerPane.getParent()).widthProperty());
        imageViewBottom.fitWidthProperty().bind(((StackPane) beakerPane.getParent()).widthProperty());
        imageViewCenter.fitWidthProperty().bind(((StackPane) beakerPane.getParent()).widthProperty());

        beakerStackPane.heightProperty().addListener(new BeakerHeightListener());

        ((StackPane) beakerPane.getParent()).widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setLeftAnchor(textTop, arcTickTop.getCenterX() + arcTickTop.getRadiusX() - textTop.getWrappingWidth() / 2);
            AnchorPane.setLeftAnchor(textBottom, arcTickBottom.getCenterX() + arcTickBottom.getRadiusX() - textBottom.getWrappingWidth() / 2);
        });

        InputController.blockTextInputToNumberFields(flowTextField, TEXT_FIELD_MAX_LENGTH);
    }

    private void setupRescaler() {

        rescaler.getMapOfLevels().put(name, 0f);
        rescaler.getObservableMapOfLevels().addListener(new MapOfLevelsListener());
    }

    /** # 1 listens for flowField value changes, set fuel level in corresponding beaker and put new value into Rescaler's MapOfLevels
     * # 2 listens for rangeLabelProperty and set top and bottom limits arcs with text labels upon UoM changes
     * ## 3, 4, 5 listens for pumpTestProperty, flowViewModel, flowComboBox and set flowRangeLabel text upon
     * - pumpTestProperty -> pumpTest choice
     * - flowViewModel -> LIMIT or PLUS_OR_MINUS choice
     * - flowComboBox -> UoM changes
     * # 6 listens for textField changes and set values into PumpReportModel for reports generation
     * (this is necessary to have values in the report in case manual input, not only in automatic crTestManager mode)*/
    private void setupListeners() {
        // #1
        flowTextField.textProperty().addListener(new FlowFieldListener());
        // #2
        flowRangeModel.flowRangeLabelProperty().addListener((observable, oldValue, newValue) -> showBeakerLevels(newValue));
        // #3
        pumpTestModel.pumpTestProperty().addListener((observable, oldValue, newValue) ->{
            setFlowLabels(newValue, flowViewModel.flowViewProperty().get());
            flowTextField.setText("");
        });
        // #4
        flowViewModel.flowViewProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(pumpTestModel.pumpTestProperty().get(), newValue));
        // #5
        flowComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(pumpTestModel.pumpTestProperty().get(), flowViewModel.flowViewProperty().get()));

        pumpFlowValuesModel.flowProperty().addListener((observableValue, oldValue, newValue) -> showOnChosenFlowUnit(newValue));
        pumpFlowTemperaturesModel.temperature1FlowProperty().addListener((observableValue, oldValue, newValue) -> showTemperature(newValue, temperature1Flow));
        pumpFlowTemperaturesModel.temperature2FlowProperty().addListener((observableValue, oldValue, newValue) -> showTemperature(newValue, temperature2Flow));
        // #6
        flowTextField.textProperty().addListener((observable, oldValue, newValue) -> pumpReportModel.setFlowValues(newValue, beakerType));
    }

    private void setupBindings(){

        flowUnitsModel.flowUnitsProperty().bind(flowComboBox.getSelectionModel().selectedItemProperty());
        flowRangeModel.flowRangeLabelProperty().bind(flowRangeLabel.textProperty());
    }

    private void setupFlowComboBox() {

        flowComboBox.getItems().setAll(MILLILITRE_PER_MINUTE, LITRE_PER_HOUR);

        switch (beakerType){
            case DELIVERY:
                createPumpDeliveryFlowUnitBinding(flowComboBox);
                break;
            case BACKFLOW:
                createPumpBackFlowUnitBinding(flowComboBox);
                break;
        }
        flowComboBox.getSelectionModel().selectFirst();
    }

    private double opacityByLevel(double level) {

        return level == 0 ? 0 : 1;
    }

    private void setLevel(double level) {

        rectangleFuel.setOpacity(0.8);
        ellipseBottomFuel.setOpacity(opacityByLevel(level));
        ellipseTopFuel.setOpacity(opacityByLevel(level));
        setHalfFuelLevel(level);
    }

    private void setHalfFuelLevel(double level) {
        rectangleFuel.setHeight(level);
        AnchorPane.setBottomAnchor(ellipseTopFuel, level - ELLIPSE_TOP_FUEL_DEVIATION);
    }

    private void clearArcAndText(Arc arc, Text arcText){

        arc.setOpacity(0d);
        arcText.setText("");
    }

    private void setFlowLabels(PumpTest pumpTest, Dimension dimension) {

        if (pumpTest == null) {
            setLevelsToNull();
            return;
        }

        double maxFlow = 0;
        double minFlow = 0;
        noTopLimit = false;
        noLowLimit = false;
        flowRangeModel.setNoLowLimit(false);
        flowRangeModel.setNoTopLimit(false);

        Optional<Double> maxDirectFlow = Optional.ofNullable(pumpTest.getMaxDirectFlow());
        Optional<Double> minDirectFlow = Optional.ofNullable(pumpTest.getMinDirectFlow());
        Optional<Double> maxBackFlow = Optional.ofNullable(pumpTest.getMaxBackFlow());
        Optional<Double> minBackFlow = Optional.ofNullable(pumpTest.getMinBackFlow());

        switch(beakerType){

            case DELIVERY:

                maxFlow = maxDirectFlow.orElse(0d);
                minFlow = minDirectFlow.orElse(0d);
                break;
            case BACKFLOW:

                maxFlow = maxBackFlow.orElse(0d);
                minFlow = minBackFlow.orElse(0d);
                break;
        }

        if(minFlow == 0d && maxFlow == 0d){
            setLevelsToNull();
            return;
        }

        if (minFlow == 0d && maxFlow != 0d) {
            noLowLimit = true;
            flowRangeModel.setNoLowLimit(true);
        }

        if(minFlow != 0 && maxFlow == 0){
            maxFlow = minFlow * 5; // if flow top limit is absent this value is used for beaker scaling only
            noTopLimit = true;
            flowRangeModel.setNoTopLimit(true);
        }

        flowRangeModel.setScaledLowLimit(minFlow * getCoefficient());
        flowRangeModel.setScaledTopLimit(maxFlow * getCoefficient());

        switch (dimension) {

            case LIMIT:
                calculateLIMIT(minFlow, maxFlow, flowRangeLabel);
                break;
            case PLUS_OR_MINUS:
                calculatePLUS_OR_MINUS(minFlow, maxFlow, flowRangeLabel);
                break;
        }
    }

    private void showOnChosenFlowUnit(String value) {

        if (value != null) {
            double coefficient = getCoefficient();
            double convertedValue = round(convertDataToDouble(value) * coefficient * 0.06);
            changeFlow(String.valueOf(convertedValue));
        }else changeFlow(null);
    }

    private void changeFlow(String value) {

        if (value == null)
            flowTextField.setText(null);
        else if (value.equals(flowTextField.getText()))
            setNewLevel(value);
        else
            flowTextField.setText(value);
    }

    private double getCoefficient(){
        return (beakerType == DELIVERY) ? getPumpDeliveryCoefficient() : getPumpBackFlowCoefficient();
    }

    private void setLevelsToNull() {

        flowRangeLabel.setText("");
        currentFlowLevels = null;
    }

    private void showTemperature(String temperature, Label textLabel) {

        String s = convertedValue.append(round(convertDataToFloat(temperature))).append(DEGREES_CELSIUS).toString();
        textLabel.setText(s);
        convertedValue.setLength(0);
    }

    private void calculateLIMIT(double minFlow, double maxFlow,  Label flowLabel) {

        double[] result;
        double coefficient = getCoefficient();
        result = getRange(minFlow, maxFlow, coefficient);
        currentFlowLevels = result;


        if (noTopLimit) {
            flowLabel.setText(String.format("> %.1f", result[0]));
        }else{
            flowLabel.setText(String.format("%.1f - %.1f", result[0], result[1]));
        }
    }

    private void calculatePLUS_OR_MINUS(double minFlow, double maxFlow, Label flowLabel) {

        double[] result = new double[2];
        double coefficient = getCoefficient();
        result[0] = round(((maxFlow + minFlow) / 2) * coefficient);
        result[1] = round(((maxFlow - minFlow) / 2) * coefficient);
        currentFlowLevels = getRange(minFlow, maxFlow, coefficient);

        if (noTopLimit) {
            flowLabel.setText(String.format("> %.1f", minFlow * coefficient));
        }else{
            flowLabel.setText(String.format("%.1f \u00B1 %.1f", result[0], result[1]));
        }
    }

    private void setNewLevel(String value) {

        double currentVal;
        if (flowRangeModel.flowRangeLabelProperty().get().isEmpty()){
            return;
        }
        currentVal = round(convertDataToDouble(value));
        setBeakerLevel(currentVal, currentFlowLevels[0], currentFlowLevels[1]);
    }

    private double[] getRange(double minFlow, double maxFlow, double coefficient) {

        double[] result = new double[2];
        result[0] = round(minFlow * coefficient);
        result[1] = round(maxFlow * coefficient);
        return result;
    }

    private void setBeakerLevel(double currentVal, double lowFlowLevelValue, double highFlowLevelValue) {

        if (currentVal <= lowFlowLevelValue)
            Platform.runLater(() -> setLevel(calculateBelowRangeLevel(currentVal, lowFlowLevelValue)));
        else if ((currentVal > lowFlowLevelValue) && (currentVal <= highFlowLevelValue))
            Platform.runLater(() -> setLevel(calculateInRangeLevel(currentVal, lowFlowLevelValue, highFlowLevelValue)));
        else if (currentVal > highFlowLevelValue)
            Platform.runLater(() -> setLevel(calculateAboveRangeLevel(currentVal, lowFlowLevelValue, highFlowLevelValue)));
    }

    private double calculateBelowRangeLevel(double currentVal, double lowFlowLevelValue) {

        return (rectangleBeaker.getHeight() * PERCENT_025) * (currentVal / lowFlowLevelValue);
    }

    private double calculateInRangeLevel(double currentVal, double lowFlowLevelValue, double highFlowLevelValue) {

        double ratio = (currentVal - lowFlowLevelValue) / (highFlowLevelValue - lowFlowLevelValue);
        double intermediateValue = (3 - 1) * ratio + 1;
        return (rectangleBeaker.getHeight() * PERCENT_075) * (intermediateValue / 3);
    }

    private double calculateAboveRangeLevel(double currentVal, double lowFlowLevelValue, double highFlowLevelValue) {

        double maxLevel = lowFlowLevelValue + highFlowLevelValue;
        double ratio;
        if (currentVal >= maxLevel)
            ratio = 1;
        else
            ratio = (currentVal - highFlowLevelValue) / (maxLevel - highFlowLevelValue);
        double intermediateValue = (4 - 3) * ratio + 3;
        return rectangleBeaker.getHeight() * (intermediateValue / 4);
    }

    private void showBeakerLevels(String rangeLabel) {

        if (!rangeLabel.isEmpty()) {
            double[] currentDeliveryFlowLevels = currentFlowLevels;
            if(currentDeliveryFlowLevels[0] != 0){
                if (noLowLimit) {
                    clearArcAndText(arcTickBottom, textBottom);
                }else{
                    textBottom.setText(String.valueOf(currentDeliveryFlowLevels[0]));
                    setArc(arcTickBottom, PERCENT_025);
                }
            }
            else{
                makeEmpty();
            }
            if(currentDeliveryFlowLevels[1] != 0){
                if (noTopLimit) {
                    clearArcAndText(arcTickTop, textTop);
                }else{
                    textTop.setText(String.valueOf(currentDeliveryFlowLevels[1]));
                    setArc(arcTickTop, PERCENT_075);
                }
            }else{
                makeEmpty();
            }

//            setArcs();
            AnchorPane.setBottomAnchor(textTop, AnchorPane.getBottomAnchor(arcTickTop) + TEXT_DEVIATION);
            AnchorPane.setBottomAnchor(textBottom, AnchorPane.getBottomAnchor(arcTickBottom) + TEXT_DEVIATION);

        } else {
            makeEmpty();
        }
    }

    private void makeEmpty() {

        arcTickTop.setOpacity(0);
        arcTickBottom.setOpacity(0);
        ellipseBottomFuel.setOpacity(0);
        ellipseTopFuel.setOpacity(0);
        textTop.setText("");
        textBottom.setText("");
        rectangleFuel.setHeight(0);
        rectangleFuel.setOpacity(0);
    }

    private void makeLevelEmpty() {
        ellipseBottomFuel.setOpacity(0);
        ellipseTopFuel.setOpacity(0);
        rectangleFuel.setHeight(0);
        rectangleFuel.setOpacity(0);
    }

    private void setArcs() {


        setArc(arcTickTop, PERCENT_075);
        setArc(arcTickBottom, PERCENT_025);
        arcTickTop.setOpacity(1d);
        arcTickBottom.setOpacity(1d);
    }

    private void setArc(Arc arc, double value){
        AnchorPane.setBottomAnchor(arc, rectangleBeaker.getHeight() * value - ARC_DEVIATION);
        arc.setOpacity(1d);
    }

    /** Listens for corresponding beaker height changes and set level in corresponding beaker
     * either in relation with other beakers levels in the same beaker set in case corresponding range label is empty
     * or in accordance with not empty flow value in the flow text field*/
    private class BeakerHeightListener implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

            if (flowRangeModel.flowRangeLabelProperty().get().isEmpty()) {

                setHalfFuelLevel((rectangleBeaker.getHeight() / 2) * (rescaler.getMapOfLevels().get(name) / currentMaxLevel));
            }
            else {
                if(flowTextField.getText() != null && !flowTextField.getText().isEmpty()){

                    setHalfFuelLevel(rectangleFuel.getHeight() * (newValue.floatValue() / oldValue.floatValue()));
                }
                showBeakerLevels(flowRangeLabel.getText());
            }
            lineLeft.setEndY(newValue.doubleValue());
            lineRight.setEndY(newValue.doubleValue());
        }
    }
    /** Listens for FlowField value changes,
     * set fuel level in corresponding beaker and put new value into Rescaler's MapOfLevels*/
    private class FlowFieldListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {


            if ((flowRangeModel.flowRangeLabelProperty().get().isEmpty())) {

                if (newValue == null || newValue.equals("") || newValue.equals("0.0") || newValue.equals("0")) {
                    rescaler.getObservableMapOfLevels().put(name, 0f);
                    makeLevelEmpty();
                    return;
                }

                if (!newValue.matches(REGEX)) {
                    rescaler.getObservableMapOfLevels().put(name, 0f);
                    return;
                }
                float currentVal = round(convertDataToFloat(newValue));
                rescaler.getObservableMapOfLevels().put(name, currentVal);
                setLevel(rectangleBeaker.getHeight() / 2);

            } else if (newValue != null) {

                if (newValue.equals("") || newValue.equals("0.0") || newValue.equals("0")) {
                    makeLevelEmpty();
                    return;
                }
                if (!newValue.matches(REGEX)) {
                    return;
                }
                setNewLevel(newValue);
            } else {
                makeLevelEmpty();
            }
        }
    }
    /** Listens for Rescaler's MapOfLevels changes and set level in corresponding beaker
     * in relation with other beakers levels in the same beaker set
     * in case corresponding range label is empty
     * Double.isNaN check is used to avoid color artifacts on fuel cylinder elements in case value.isNaN
     * CURRENTLY RESCALER IS NOT REALLY USED HERE AS IN INJECTORS, IMPLEMENTED FOR POSSIBLE FUTURE DEVELOPMENT NEEDS ONLY !!!*/
    private class MapOfLevelsListener implements MapChangeListener<String, Float>{

        @Override
        public void onChanged(Change<? extends String, ? extends Float> change) {
            currentMaxLevel = rescaler.getMapOfLevels().values().stream().max(Float::compare).get();
            if (flowRangeModel.flowRangeLabelProperty().get().isEmpty()) {
                double value = (rectangleBeaker.getHeight() / 2) * (rescaler.getMapOfLevels().get(name) / currentMaxLevel);
                Platform.runLater(() -> setLevel(Double.isNaN(value) ? 0f : value));
            }
        }
    }
}
