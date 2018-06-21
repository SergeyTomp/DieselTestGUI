package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.beakers.BeakerMode;
import fi.stardex.sisu.util.Rescaler;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import javafx.application.Platform;
import javafx.fxml.FXML;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BeakerController {

    private static final Logger logger = LoggerFactory.getLogger(BeakerController.class);

    private final static BigDecimal PERCENT_075 = new BigDecimal(0.75);
    private final static BigDecimal PERCENT_025 = new BigDecimal(0.25);
    private final static BigDecimal PERCENT_05 = new BigDecimal(0.5);
    private final static BigDecimal ARC_DEVIATION = new BigDecimal(11);
    private final static BigDecimal TEXT_DEVIATION = new BigDecimal(3);
    private static final double ELLIPSE_TOP_FUEL_DEVIATION = 8;

    private static List<BeakerController> beakerControllers = new ArrayList<>();

    public static List<BeakerController> getBeakerControllers() {
        return beakerControllers;
    }

    private TextField textField;

    private Rescaler rescaler;

    private FirmwareDataConverter firmwareDataConverter;

    private float lastFuelLevel;

    private float currentFuelLevel;

    private String name;

    Label temperatureLabel;
    Label temperature2Label;

    @FXML
    private AnchorPane beakerPane;
    @FXML
    private Ellipse ellipseTopFuel;
    @FXML
    private Ellipse ellipseBottomFuel;
    @FXML
    private Arc arcTickTop;
    @FXML
    private Arc arcTickBottom;
    @FXML
    private Text textTop;
    @FXML
    private Text textBottom;
    @FXML
    private Rectangle rectangleBeaker;
    @FXML
    private Rectangle rectangleFuel;
    @FXML
    private Ellipse ellipseBottomBeaker;
    @FXML
    private Ellipse ellipseTopBeaker;
    @FXML
    private ImageView imageViewCenter;
    @FXML
    private ImageView imageViewTop;
    @FXML
    private ImageView imageViewBottom;
    @FXML
    private Line lineLeft;
    @FXML
    private Line lineRight;

    private static final String REGEX = "[0-9.]*[^.]";

    BeakerMode beakerMode;

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public void setRescaler(Rescaler rescaler) {
        this.rescaler = rescaler;
    }

    public void setFirmwareDataConverter(FirmwareDataConverter firmwareDataConverter) {
        this.firmwareDataConverter = firmwareDataConverter;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PostConstruct
    public void init() {

        beakerControllers.add(this);

        rescaler.getMapOfLevels().put(name, 0f);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null || newValue.equals("") || newValue.equals("0.0") || newValue.equals("0")) {
                makeEmpty();
                return;
            }

            if (!newValue.matches(REGEX)) {
                rescaler.getMapOfLevels().put(name, 0f);
                return;
            }


            float currentVal = firmwareDataConverter.roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(newValue));

            System.err.println(currentVal);
            rescaler.getMapOfLevels().put(name, currentVal);

            System.err.println(rescaler.getMapOfLevels());

            Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * (currentVal / rescaler.getMapOfLevels().lastEntry().getValue())));

//            rescaler.getSetOfLevels().add(currentVal);
//
//            float ratio = currentVal / rescaler.getSetOfLevels().last();

//            Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * ratio));

//            if (newValue != null && !newValue.equals("0.0") && !newValue.equals("0") && !newValue.equals("") && newValue.matches(REGEX)) {
//
//                currentFuelLevel = firmwareDataConverter.roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(newValue));
//
//                if (oldValue != null && !oldValue.equals("0.0") && !oldValue.equals("0") && !oldValue.equals("") && oldValue.matches(REGEX))
//                    lastFuelLevel = firmwareDataConverter.roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(oldValue));
//
//                rescaler.getSetOfLevels().add(currentFuelLevel);
//                rescaler.getSetOfLevels().remove(lastFuelLevel);
//
//                rescaler.setMediumLevel(currentFuelLevel);
//
//                ratio = currentFuelLevel / rescaler.getSetOfLevels().last();
//
//                Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * ratio));





//            if (newValue != null && !newValue.equals("0.0") && !newValue.equals("0") && !newValue.equals("") && newValue.matches(REGEX)) {
//                currentFuelMidHeight = firmwareDataConverter.roundToOneDecimalPlace(firmwareDataConverter.convertDataToFloat(newValue));
//                if (currentFuelMidHeight > rescaler.getMediumLevel() * 2) {
//                    rescaler.setMediumLevel(currentFuelMidHeight);
//                    ratio = 1;
//                    Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * ratio));
//                } else {
//                    ratio = currentFuelMidHeight / rescaler.getMediumLevel();
//                    Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * ratio));
//                }
//
//            } else
//                makeEmpty();

        });

//        rescaler.mediumLevelProperty().addListener((observable, oldValue, newValue) -> {
//            if (!textField.getText().equals(""))
//                Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * ratio));
//        });

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
            setHalfFuelLevel((rectangleBeaker.getHeight() / 2));
            lineLeft.setEndY(newValue.doubleValue());
            lineRight.setEndY(newValue.doubleValue());
        });

        ((StackPane) beakerPane.getParent()).widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setLeftAnchor(textTop, arcTickTop.getCenterX() + arcTickTop.getRadiusX() - textTop.getWrappingWidth() / 2);
            AnchorPane.setLeftAnchor(textBottom, arcTickBottom.getCenterX() + arcTickBottom.getRadiusX() - textBottom.getWrappingWidth() / 2);
        });

        makeEmpty();
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

    private double opacityByLevel(double level) {
        return level == 0 ? 0 : 1;
    }

}
