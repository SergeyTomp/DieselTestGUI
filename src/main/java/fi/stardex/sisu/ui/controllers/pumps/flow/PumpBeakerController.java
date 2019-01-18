package fi.stardex.sisu.ui.controllers.pumps.flow;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import fi.stardex.sisu.util.InputController;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.rescalers.Rescaler;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.util.function.Consumer;

import static fi.stardex.sisu.util.FlowUnitObtainer.*;
import static fi.stardex.sisu.util.FlowUnitObtainer.createBackFlowUnitBinding;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToFloat;
import static fi.stardex.sisu.util.converters.DataConverter.round;
import static fi.stardex.sisu.util.enums.BeakerType.DELIVERY;

public class PumpBeakerController {

    @FXML private ComboBox<String> flowComboBox;
    @FXML private Label flowLabel;
    @FXML private Label flowRangeLabel;
    @FXML private Label ml_Min_FlowLabel;
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
    private float currentMaxLevel;
    private double[] currentFlowLevels;
    private ObjectProperty<String> rangeLabelProperty = new SimpleObjectProperty<>();

    private FlowRangeModel flowRangeModel;
    private FlowUnitsModel flowUnitsModel;

    private PumpFlowValuesModel pumpFlowValuesModel;
    private PumpFlowTemperaturesModel pumpFlowTemperaturesModel;
    private PumpTestModel pumpTestModel;
    private FlowViewModel flowViewModel;

    private I18N i18N;
    private static final Logger logger = LoggerFactory.getLogger(BeakerController.class);
    private static final int ELLIPSE_TOP_FUEL_DEVIATION = 8;
    private final static int ARC_DEVIATION = 11;
    private final static double PERCENT_075 = 0.75;
    private final static double PERCENT_025 = 0.25;
    private final static int TEXT_DEVIATION = 3;
    private static final String REGEX = "[0-9.]*[^.]";
    public static final String MILLILITRE_PER_MINUTE = "ml/min";
    public static final String LITRE_PER_HOUR = "l/h";
    private static final int TEXT_FIELD_MAX_LENGTH = 7;

    public void setRescaler(Rescaler rescaler) {
        this.rescaler = rescaler;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBeakerType(BeakerType beakerType) {
        this.beakerType = beakerType;
    }
    public void setFlowRangeLabel(Label flowRangeLabel) {
        this.flowRangeLabel = flowRangeLabel;
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

    @PostConstruct
    public void init() {

        setupRescaler();
        setupResizeable();
        bindingI18N();
        setupBindings();
        setupListeners();
        setupFlowComboBox();
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

        ((StackPane) beakerPane.getParent()).heightProperty().addListener((observable, oldValue, newValue) -> {
            setHalfFuelLevel((rectangleBeaker.getHeight() / 2) * (rescaler.getMapOfLevels().get(name) / currentMaxLevel));
            lineLeft.setEndY(newValue.doubleValue());
            lineRight.setEndY(newValue.doubleValue());
        });

        ((StackPane) beakerPane.getParent()).widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setLeftAnchor(textTop, arcTickTop.getCenterX() + arcTickTop.getRadiusX() - textTop.getWrappingWidth() / 2);
            AnchorPane.setLeftAnchor(textBottom, arcTickBottom.getCenterX() + arcTickBottom.getRadiusX() - textBottom.getWrappingWidth() / 2);
        });

        InputController.blockTextInputToNumberFields(flowTextField, TEXT_FIELD_MAX_LENGTH);
    }

    private void setupRescaler() {

        rescaler.getMapOfLevels().put(name, 0f);
        rescaler.getObservableMapOfLevels().addListener((MapChangeListener<String, Float>) change -> {

            currentMaxLevel = rescaler.getMapOfLevels().values().stream().max(Float::compare).get();
            Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * (rescaler.getMapOfLevels().get(name) / currentMaxLevel)));
        });
    }

    private void setupListeners() {

        rangeLabelProperty.addListener((observable, oldValue, newValue) -> showBeakerLevels(newValue));
        flowTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            if ((flowRangeLabel.getText().isEmpty())) {

                if (newValue == null || newValue.equals("") || newValue.equals("0.0") || newValue.equals("0")) {
                    rescaler.getMapOfLevels().put(name, 0f);
                    makeLevelEmpty();
                    return;
                }

                if (!newValue.matches(REGEX)) {
                    rescaler.getMapOfLevels().put(name, 0f);
                    return;
                }
                float currentVal = round(convertDataToFloat(newValue));
                rescaler.getMapOfLevels().put(name, currentVal);
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
        });

        pumpTestModel.pumpTestProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(newValue, flowViewModel.flowViewProperty().get()));

        flowViewModel.flowViewProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(pumpTestModel.pumpTestProperty().get(), newValue));

        flowComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                setFlowLabels(pumpTestModel.pumpTestProperty().get(), flowViewModel.flowViewProperty().get()));
    }

    private void setupBindings(){

        flowUnitsModel.flowUnitsProperty().bind(flowComboBox.getSelectionModel().selectedItemProperty());
        flowRangeModel.flowRangeProperty().bind(flowRangeLabel.textProperty());
        temperature1Flow.textProperty().bind(pumpFlowTemperaturesModel.temperature1FlowProperty());
        temperature2Flow.textProperty().bind(pumpFlowTemperaturesModel.temperature2FlowProperty());
        rangeLabelProperty.bind(flowRangeLabel.textProperty());
        pumpFlowValuesModel.flowProperty().bind(flowTextField.textProperty());
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

    private void setFlowLabels(PumpTest pumpTest, Dimension dimension) {

        if (pumpTest == null) {
            setLevelsToNull();
            return;
        }

        double maxFlow = 0;
        double minFlow = 0;

        Optional<Double> maxDirectFlow = Optional.ofNullable(pumpTest.getMaxDirectFlow());
        Optional<Double> minDirectFlow = Optional.ofNullable(pumpTest.getMinDirectFlow());
        Optional<Double> maxBackFlow = Optional.ofNullable(pumpTest.getMaxBackFlow());
        Optional<Double> minBackFlow = Optional.ofNullable(pumpTest.getMinBackFlow());

        switch(beakerType){

            case DELIVERY:

//                if(maxDirectFlow.isPresent()){
//                    maxFlow = maxDirectFlow.get();
//                }
//                if(minDirectFlow.isPresent()){
//                    minFlow = minDirectFlow.get();
//                }
                maxFlow = maxDirectFlow.orElse(0d);
                minFlow = minDirectFlow.orElse(0d);
                break;
            case BACKFLOW:

//                if(maxBackFlow.isPresent()){
//                    maxFlow = maxBackFlow.get();
//                }
//                if(minBackFlow.isPresent()){
//                    minFlow = minBackFlow.get();
//                }
                maxFlow = maxBackFlow.orElse(0d);
                minFlow = minBackFlow.orElse(0d);

                break;
        }

        if(minFlow != 0 && maxFlow == 0){
            maxFlow = minFlow * 5;
        }

        switch (dimension) {

            case LIMIT:
                calculateLIMIT(minFlow, maxFlow, beakerType, flowRangeLabel);
                break;
            case PLUS_OR_MINUS:
                calculatePLUS_OR_MINUS(minFlow, maxFlow, beakerType, flowRangeLabel);
                break;
        }
    }

    public void changeFlow(String value) {

        if (value == null)
            flowTextField.setText(null);
        else if (value.equals(flowTextField.getText()))
            setNewLevel(value);
        else
            flowTextField.setText(value);
    }

    private void setLevelsToNull() {

        flowRangeLabel.setText("");
        currentFlowLevels = null;
    }

    private void calculateLIMIT(double minFlow, double maxFlow,  BeakerType beakerType, Label flowLabel) {

        double[] result;
        double coefficient = (beakerType == DELIVERY) ? getPumpDeliveryCoefficient() : getPumpBackFlowCoefficient();
        result = getRange(minFlow, maxFlow, coefficient);
        currentFlowLevels = result;
        flowLabel.setText(String.format("%.1f - %.1f", result[0], result[1]));
    }

    private void calculatePLUS_OR_MINUS(double minFlow, double maxFlow, BeakerType beakerType, Label flowLabel) {

        double[] result = new double[2];
        double coefficient = (beakerType == DELIVERY) ? getPumpDeliveryCoefficient() : getPumpBackFlowCoefficient();
        result[0] = round(((maxFlow + minFlow) / 2) * coefficient);
        result[1] = round(((maxFlow - minFlow) / 2) * coefficient);
        currentFlowLevels = getRange(minFlow, maxFlow, coefficient);
        flowLabel.setText(String.format("%.1f \u00B1 %.1f", result[0], result[1]));
    }

    private void setNewLevel(String value) {

        double currentVal;
        if (flowRangeLabel.getText().isEmpty()){
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
                textBottom.setText(String.valueOf(currentDeliveryFlowLevels[0]));
                setArc(arcTickBottom, PERCENT_025);
            }
            else{
                makeEmpty();
            }
            if(currentDeliveryFlowLevels[1] != 0){
                textTop.setText(String.valueOf(currentDeliveryFlowLevels[1]));
                setArc(arcTickTop, PERCENT_075);
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
}
