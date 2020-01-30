package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisFlowModel;
import fi.stardex.sisu.model.uis.UisSettingsModel;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.ui.updaters.UisFlowUpdater;
import fi.stardex.sisu.util.InputController;
import fi.stardex.sisu.util.enums.Dimension;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.FlowUnitObtainer.getUisDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToFloat;
import static fi.stardex.sisu.util.converters.DataConverter.round;

public class UisBeakerController {

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
    @FXML private TextField flowTextField;
    @FXML private Label temperature1Flow;
    @FXML private Label temperature2Flow;

    private ToggleButton ledToggleButton;
    private StringBuilder convertedValue = new StringBuilder();
    private static final Logger logger = LoggerFactory.getLogger(UisBeakerController.class);
    private final int ELLIPSE_TOP_FUEL_DEVIATION = 8;
    private final int ARC_DEVIATION = 11;
    private final double PERCENT_075 = 0.75;
    private final double PERCENT_025 = 0.25;
    private final int TEXT_DEVIATION = 3;
    private final String REGEX = "[0-9.]*[^.]";
    private final int TEXT_FIELD_MAX_LENGTH = 7;
    private final String DEGREES_CELSIUS = " \u2103";
    private double[] currentFlowLevels;

    private UisFlowModel uisFlowModel;
    @Autowired
    private MainSectionUisModel mainSectionUisModel;
    @Autowired
    private UisSettingsModel uisSettingsModel;
    private UisFlowUpdater uisFlowUpdater;

    public void setUisFlowModel(UisFlowModel uisFlowModel) {
        this.uisFlowModel = uisFlowModel;
    }
    public void setLedToggleButton(ToggleButton ledToggleButton) {
        this.ledToggleButton = ledToggleButton;
    }
    public void setUisFlowUpdater(UisFlowUpdater uisFlowUpdater) {
        this.uisFlowUpdater = uisFlowUpdater;
    }

    @PostConstruct
    public void init() {
        setupResizeable();
        setupListeners();

        makeEmpty();
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

    /** # 1 listens for flowField value changes and set fuel level in corresponding beaker and into UisFlowModel for reports generation
     * (this provide values in the report in case manual input, not only in automatic crTestManager mode)
     * ## 2, 3, 4 listens for injectorTestProperty, flowRangeViewProperty, flowUnitsProperty and set flowRangeLabel text upon
     * - injectorTestProperty -> test choice
     * - flowRangeViewProperty -> LIMIT or PLUS_OR_MINUS choice
     * - flowUnitsProperty -> UoM choice
     * #5 listens for flow values changes in updater and show values in beakers
     * #6, 7 listen for temperature changes in updater and show values in labels
     * #8 listens for channels switch on/off and manage all above listeners activation/deactivation*/
    private void setupListeners() {

        // #1
        FlowFieldListener flowFieldListener = new FlowFieldListener();
        // #2
        ChangeListener<Test> testChangeListener = (observable, oldValue, newValue) -> {
            setFlowLabels(newValue, uisSettingsModel.getFlowRangeViewProperty().get());
            flowTextField.setText("");
        };
        // #3
        ChangeListener<Dimension> flowDimensionListener = (observableValue, oldValue, newValue) ->
                setFlowLabels(mainSectionUisModel.injectorTestProperty().get(), newValue);
        // #4
        ChangeListener<String> flowUnitsListener = (observableValue, oldValue, newValue) ->
                setFlowLabels(mainSectionUisModel.injectorTestProperty().get(), uisSettingsModel.getFlowRangeViewProperty().get());
        // #5
        ChangeListener<String> flowUpdaterListener = (observableValue, oldValue, newValue) -> {
            if (uisSettingsModel.instantFlowProperty().get()) { showOnChosenFlowUnit(newValue); }
        };
        // #6
        ChangeListener<String> temperatureOneUpdaterListener = (observableValue, oldValue, newValue) -> showTemperature(newValue, temperature1Flow);
        // #7
        ChangeListener<String> temperatureTwoUpdaterListener = (observableValue, oldValue, newValue) -> showTemperature(newValue, temperature2Flow);
        // #8
        ledToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue) {
                flowTextField.textProperty().addListener(flowFieldListener);
                mainSectionUisModel.injectorTestProperty().addListener(testChangeListener);
                uisSettingsModel.getFlowRangeViewProperty().addListener(flowDimensionListener);
                uisFlowModel.getFlowUnitsProperty().addListener(flowUnitsListener);
                uisFlowUpdater.flowProperty().addListener(flowUpdaterListener);
                uisFlowUpdater.temperature_1Property().addListener(temperatureOneUpdaterListener);
                uisFlowUpdater.temperature_2Property().addListener(temperatureTwoUpdaterListener);
                setFlowLabels(mainSectionUisModel.injectorTestProperty().get(), uisSettingsModel.getFlowRangeViewProperty().get());

            }else {
                flowTextField.textProperty().removeListener(flowFieldListener);
                mainSectionUisModel.injectorTestProperty().removeListener(testChangeListener);
                uisSettingsModel.getFlowRangeViewProperty().removeListener(flowDimensionListener);
                uisFlowModel.getFlowUnitsProperty().removeListener(flowUnitsListener);
                uisFlowUpdater.flowProperty().removeListener(flowUpdaterListener);
                uisFlowUpdater.temperature_1Property().removeListener(temperatureOneUpdaterListener);
                uisFlowUpdater.temperature_2Property().removeListener(temperatureTwoUpdaterListener);
                flowTextField.setText("");
                makeEmpty();
                makeLevelEmpty();
                temperature1Flow.setText("");
                temperature2Flow.setText("");
            }
        });
        // #3
//        mainSectionUisModel.injectorTestProperty().addListener((observable, oldValue, newValue) ->{
//            setFlowLabels(newValue, uisSettingsModel.getFlowRangeViewProperty().get());
//            flowTextField.setText("");
//        });
        // #4
//        uisSettingsModel.getFlowRangeViewProperty().addListener((observable, oldValue, newValue) ->
//                setFlowLabels(mainSectionUisModel.injectorTestProperty().get(), newValue));
        // #5
//        uisFlowModel.getFlowUnitsProperty().addListener((observable, oldValue, newValue) ->
//                setFlowLabels(mainSectionUisModel.injectorTestProperty().get(), uisSettingsModel.getFlowRangeViewProperty().get()));

        // здесь вставить три листенера: слушают в updater поток и две температуры, отображают их в текстовом поле и лэйблах соответственно
        //updater.flowProperty().addListener((observableValue, oldValue, newValue) -> showOnChosenFlowUnit(newValue));
        //updater.temperature1FlowProperty().addListener((observableValue, oldValue, newValue) -> showTemperature(newValue, temperature1Flow));
        //updater.temperature2FlowProperty().addListener((observableValue, oldValue, newValue) -> showTemperature(newValue, temperature2Flow));

//        flowTextField.textProperty().addListener((observable, oldValue, newValue) -> uisFlowModel.getFlowValueProperty().setValue(newValue));
    }

    private void setFlowLabels(Test test, Dimension dimension) {

        if (test == null) {
            currentFlowLevels = null;
            return;
        }

        double maxFlow;
        double minFlow;

        double nominalFlow = test.getNominalFlow();
        double flowRange = test.getFlowRange();
        double tolerance = nominalFlow * flowRange / 100;

        maxFlow = nominalFlow + tolerance;
        minFlow = nominalFlow - tolerance;

        uisFlowModel.setScaledLeftLimit(minFlow * getUisDeliveryCoefficient());
        uisFlowModel.setScaledRightLimit(maxFlow * getUisDeliveryCoefficient());

        switch (dimension) {

            case LIMIT:
                calculateLIMIT(minFlow, maxFlow);
                break;
            case PLUS_OR_MINUS:
                calculatePLUS_OR_MINUS(minFlow, maxFlow);
                break;
        }
        showBeakerLevels(uisFlowModel.getFlowRangeLabelProperty().get());
    }

    private void showOnChosenFlowUnit(String value) {

        if (value != null) {
            double coefficient = getUisDeliveryCoefficient();
            double convertedValue = round(convertDataToDouble(value) * coefficient);
            changeFlow(String.valueOf(convertedValue));
        }else changeFlow(null);
    }

    private void showTemperature(String temperature, Label textLabel) {

        String s = convertedValue.append(round(convertDataToFloat(temperature))).append(DEGREES_CELSIUS).toString();
        textLabel.setText(s);
        convertedValue.setLength(0);
    }

    private void calculateLIMIT(double minFlow, double maxFlow) {

        double[] result;
        double coefficient = getUisDeliveryCoefficient();
        result = getRange(minFlow, maxFlow, coefficient);
        currentFlowLevels = result;
        if (minFlow == 0 && maxFlow == 0) {
            uisFlowModel.getFlowRangeLabelProperty().setValue("");
            return;
        }
        uisFlowModel.getFlowRangeLabelProperty().setValue(String.format("%.1f - %.1f", result[0], result[1]));
    }

    private void calculatePLUS_OR_MINUS(double minFlow, double maxFlow) {

        double[] result = new double[2];
        double coefficient = getUisDeliveryCoefficient();
        result[0] = round(((maxFlow + minFlow) / 2) * coefficient);
        result[1] = round(((maxFlow - minFlow) / 2) * coefficient);
        currentFlowLevels = getRange(minFlow, maxFlow, coefficient);
        if (minFlow == 0 && maxFlow == 0) {
            uisFlowModel.getFlowRangeLabelProperty().setValue("");
            return;
        }
        uisFlowModel.getFlowRangeLabelProperty().setValue(String.format("%.1f \u00B1 %.1f", result[0], result[1]));
    }

    private double[] getRange(double minFlow, double maxFlow, double coefficient) {

        double[] result = new double[2];
        result[0] = round(minFlow * coefficient);
        result[1] = round(maxFlow * coefficient);
        return result;
    }

    private void changeFlow(String value) {

        if (value == null)
            flowTextField.setText(null);
        else if (value.equals(flowTextField.getText()))
            setNewLevel(value);
        else
            flowTextField.setText(value);
    }

    private void setNewLevel(String value) {

        if (uisFlowModel.getFlowRangeLabelProperty().get().isEmpty()){

            setLevel(rectangleBeaker.getHeight() / 2);
            return;
        }
        double currentVal = round(convertDataToDouble(value));
        setBeakerLevel(currentVal, currentFlowLevels[0], currentFlowLevels[1]);
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
            if(currentFlowLevels[0] != 0){
                    textBottom.setText(String.valueOf(currentFlowLevels[0]));
                    setArc(arcTickBottom, PERCENT_025);
            } else{ makeEmpty(); }

            if(currentFlowLevels[1] != 0){
                    textTop.setText(String.valueOf(currentFlowLevels[1]));
                    setArc(arcTickTop, PERCENT_075);
            }else{ makeEmpty(); }

            AnchorPane.setBottomAnchor(textTop, AnchorPane.getBottomAnchor(arcTickTop) + TEXT_DEVIATION);
            AnchorPane.setBottomAnchor(textBottom, AnchorPane.getBottomAnchor(arcTickBottom) + TEXT_DEVIATION);

        } else { makeEmpty(); }
    }

    private void setArc(Arc arc, double value){
        AnchorPane.setBottomAnchor(arc, rectangleBeaker.getHeight() * value - ARC_DEVIATION);
        arc.setOpacity(1d);
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


    private double opacityByLevel(double level) {

        return level == 0 ? 0 : 1;
    }

    private void setBiggerArc(Arc arc, double value) {

        AnchorPane.setBottomAnchor(arc, rectangleBeaker.getHeight() * value - ARC_DEVIATION);
    }

    private void setLowerArc(Arc arc, double value) {

        AnchorPane.setBottomAnchor(arc, rectangleBeaker.getHeight() * value - ARC_DEVIATION);
    }

    /** Listens for corresponding beaker StackPane height changes and reset level arcs and text in corresponding beaker
     * in accordance with flow value in the flow text field*/
    private class BeakerHeightListener implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

            if (ledToggleButton.selectedProperty().get()) {
                if(flowTextField.getText() != null && !flowTextField.getText().isEmpty()){

                    setHalfFuelLevel(rectangleFuel.getHeight() * (newValue.floatValue() / oldValue.floatValue()));
                }
                showBeakerLevels(uisFlowModel.getFlowRangeLabelProperty().get());
            }
            lineLeft.setEndY(newValue.doubleValue());
            lineRight.setEndY(newValue.doubleValue());
        }
    }

    /** Listens for FlowField value changes and set fuel level in corresponding beaker*/
    private class FlowFieldListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {

            if (newValue != null) {

                if (newValue.equals("") || newValue.equals("0.0") || newValue.equals("0")) {
                    makeLevelEmpty();
                    uisFlowModel.getFlowValueProperty().setValue("0.0");
                    return;
                }
                if (!newValue.matches(REGEX)) {
                    return;
                }
                setNewLevel(newValue);
                uisFlowModel.getFlowValueProperty().setValue(newValue);
            } else {
                makeLevelEmpty();
                uisFlowModel.getFlowValueProperty().setValue("0.0");
            }
        }
    }
}
