package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.rescalers.Rescaler;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
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
import java.util.ArrayList;
import java.util.List;

public class BeakerController {

    private static final Logger logger = LoggerFactory.getLogger(BeakerController.class);

    private static final double ELLIPSE_TOP_FUEL_DEVIATION = 8;

    private static final String REGEX = "[0-9.]*[^.]";

    private static List<BeakerController> beakerControllers = new ArrayList<>();

    public static List<BeakerController> getBeakerControllers() {
        return beakerControllers;
    }

    private TextField textField;

    private Rescaler rescaler;

    private DataConverter dataConverter;

    private String name;

    private float currentMaxLevel;

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

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public void setRescaler(Rescaler rescaler) {
        this.rescaler = rescaler;
    }

    public void setDataConverter(DataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PostConstruct
    public void init() {

        beakerControllers.add(this);

        setupResizeable();

        setupRescaler();

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

    private void setupRescaler() {

        rescaler.getMapOfLevels().put(name, 0f);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null || newValue.equals("") || newValue.equals("0.0") || newValue.equals("0")) {
                rescaler.getMapOfLevels().put(name, 0f);
                makeEmpty();
                return;
            }

            if (!newValue.matches(REGEX)) {
                rescaler.getMapOfLevels().put(name, 0f);
                return;
            }

            float currentVal = dataConverter.roundToOneDecimalPlace(dataConverter.convertDataToFloat(newValue));

            rescaler.getObservableMapOfLevels().put(name, currentVal);

        });

        rescaler.getObservableMapOfLevels().addListener((MapChangeListener<String, Float>) change -> {

            currentMaxLevel = rescaler.getMapOfLevels().values().stream().max(Float::compare).get();
            Platform.runLater(() -> setLevel((rectangleBeaker.getHeight() / 2) * (rescaler.getMapOfLevels().get(name) / currentMaxLevel)));

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

}
