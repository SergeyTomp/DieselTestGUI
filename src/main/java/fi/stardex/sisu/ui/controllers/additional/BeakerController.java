package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.rescalers.Rescaler;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
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

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.FlowUnitObtainer.getBackFlowCoefficient;
import static fi.stardex.sisu.util.FlowUnitObtainer.getDeliveryCoefficient;
import static fi.stardex.sisu.util.converters.DataConverter.*;

public class BeakerController {

    @FXML private AnchorPane beakerPane;

    @FXML private Ellipse ellipseTopFuel;

    @FXML private Ellipse ellipseBottomFuel;

    @FXML private Arc arcTickTop;

    @FXML private Arc arcTickBottom;

    @FXML private Text textTop;

    @FXML private Text textBottom;

    @FXML private Rectangle rectangleBeaker;

    @FXML private Rectangle rectangleFuel;

    @FXML private Ellipse ellipseBottomBeaker;

    @FXML private Ellipse ellipseTopBeaker;

    @FXML private ImageView imageViewCenter;

    @FXML private ImageView imageViewTop;

    @FXML private ImageView imageViewBottom;

    @FXML private Line lineLeft;

    @FXML private Line lineRight;

    private static final Logger logger = LoggerFactory.getLogger(BeakerController.class);

    private static final int ELLIPSE_TOP_FUEL_DEVIATION = 8;

    private final static int ARC_DEVIATION = 11;

    private final static double PERCENT_075 = 0.75;

    private final static double PERCENT_025 = 0.25;

    private final static int TEXT_DEVIATION = 3;

    private static final String REGEX = "[0-9.]*[^.]";

    private ToggleButton ledBeakerController;

    private TextField textField;

    private Rescaler rescaler;

    private String name;

    private float currentMaxLevel;

    private BeakerType beakerType;

    private FlowController flowController;

    private Label deliveryRangeLabel;

    private Label backFlowRangeLabel;

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public void setLedController(ToggleButton ledBeakerController) {
        this.ledBeakerController = ledBeakerController;
    }

    public void setRescaler(Rescaler rescaler) {
        this.rescaler = rescaler;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBeakerType(BeakerType beakerType) {
        this.beakerType = beakerType;
    }

    public void setFlowController(FlowController flowController) {
        this.flowController = flowController;
    }

    public void setDeliveryRangeLabel(Label deliveryRangeLabel) {
        this.deliveryRangeLabel = deliveryRangeLabel;
    }

    public void setBackFlowRangeLabel(Label backFlowRangeLabel) {
        this.backFlowRangeLabel = backFlowRangeLabel;
    }

    @PostConstruct
    public void init() {

        setupRescaler();

        setupListeners();

        setupResizeable();

        makeEmpty();

    }

    private void setupRescaler() {

        rescaler.getMapOfLevels().put(name, 0f);

        rescaler.getObservableMapOfLevels().addListener((MapChangeListener<String, Float>) change -> {

            currentMaxLevel = rescaler.getMapOfLevels().values().stream().max(Float::compare).get();
            Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * (rescaler.getMapOfLevels().get(name) / currentMaxLevel)));

        });

    }

    private void setupListeners() {

        flowController.deliveryRangeLabelPropertyProperty().addListener((observable, oldValue, newValue) ->
                showBeakerLevels(newValue, backFlowRangeLabel.getText(), ledBeakerController.isSelected()));

        flowController.backFlowRangeLabelPropertyProperty().addListener((observable, oldValue, newValue) ->
                showBeakerLevels(deliveryRangeLabel.getText(), newValue, ledBeakerController.isSelected()));

        ledBeakerController.selectedProperty().addListener((observable, oldValue, newValue) ->
                showBeakerLevels(deliveryRangeLabel.getText(), backFlowRangeLabel.getText(), newValue));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if ((deliveryRangeLabel.getText().isEmpty()) && (backFlowRangeLabel.getText().isEmpty())) {

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

    }

    public void changeFlow(String value) {

        if (value == null)
            textField.setText(null);
        else if (value.equals(textField.getText()))
            setNewLevel(value);
        else
            textField.setText(value);

    }

    private void setNewLevel(String value) {

        double currentVal;
        double coefficient;

        switch (beakerType) {
            case DELIVERY:
                if (deliveryRangeLabel.getText().isEmpty())
                    break;
                coefficient = getDeliveryCoefficient();
                currentVal = round(convertDataToDouble(value) * coefficient);
                double lowDeliveryFlowLevelValue = round(flowController.getCurrentDeliveryFlowLevels()[0] * coefficient);
                double highDeliveryFlowLevelValue = round(flowController.getCurrentDeliveryFlowLevels()[1] * coefficient);
                setBeakerLevel(currentVal, lowDeliveryFlowLevelValue, highDeliveryFlowLevelValue);
                break;
            case BACKFLOW:
                if (backFlowRangeLabel.getText().isEmpty())
                    break;
                coefficient = getBackFlowCoefficient();
                currentVal = round(convertDataToDouble(value) * coefficient);
                double lowBackFlowLevelValue = round(flowController.getCurrentBackFlowLevels()[0] * coefficient);
                double highBackFlowLevelValue = round(flowController.getCurrentBackFlowLevels()[1] * coefficient);
                setBeakerLevel(currentVal, lowBackFlowLevelValue, highBackFlowLevelValue);
        }

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

    private void showBeakerLevels(String deliveryRangeLabel, String backFlowRangeLabel, boolean ledSelected) {

        if (ledSelected) {
            switch (beakerType) {
                case DELIVERY:
                    if (!deliveryRangeLabel.isEmpty()) {
                        double[] currentDeliveryFlowLevels = flowController.getCurrentDeliveryFlowLevels();
                        textTop.setText(String.valueOf(currentDeliveryFlowLevels[1]));
                        textBottom.setText(String.valueOf(currentDeliveryFlowLevels[0]));
                        setArcs();
                        AnchorPane.setBottomAnchor(textTop, AnchorPane.getBottomAnchor(arcTickTop) + TEXT_DEVIATION);
                        AnchorPane.setBottomAnchor(textBottom, AnchorPane.getBottomAnchor(arcTickBottom) + TEXT_DEVIATION);

                    } else {
                        makeEmpty();
                    }
                    break;
                case BACKFLOW:
                    if (!backFlowRangeLabel.isEmpty()) {
                        double[] currentBackFlowLevels = flowController.getCurrentBackFlowLevels();
                        textTop.setText(String.valueOf(currentBackFlowLevels[1]));
                        textBottom.setText(String.valueOf(currentBackFlowLevels[0]));
                        setArcs();
                        AnchorPane.setBottomAnchor(textTop, AnchorPane.getBottomAnchor(arcTickTop) + TEXT_DEVIATION);
                        AnchorPane.setBottomAnchor(textBottom, AnchorPane.getBottomAnchor(arcTickBottom) + TEXT_DEVIATION);
                    } else {
                        makeEmpty();
                    }
                    break;
                default:
                    break;
            }
        } else {
            makeEmpty();
        }

    }

    private void setArcs() {

        setBiggerArc(arcTickTop, PERCENT_075);
        setLowerArc(arcTickBottom, PERCENT_025);
        arcTickTop.setOpacity(1d);
        arcTickBottom.setOpacity(1d);

    }

    // TODO: доделать resize для FlowMaster/Stream
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

}
