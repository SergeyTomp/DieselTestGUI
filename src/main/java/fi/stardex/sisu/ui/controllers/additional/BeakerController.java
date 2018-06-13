package fi.stardex.sisu.ui.controllers.additional;

import fi.stardex.sisu.beakers.BeakerMode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private final static BigDecimal ELLIPSE_TOP_FUEL_DEVIATION = new BigDecimal(8);

    private static List<BeakerController> beakerControllers = new ArrayList<>();

    public static List<BeakerController> getBeakerControllers() {
        return beakerControllers;
    }

//    private ComboBox<Formula> comboBox
//    private LedController ledController
//    private CleverField textField
//    private BeakerPlace beakerPlace
//    private Rescaler rescaler
//    private SavedInjectorBeakerData savedInjectorBeakerData


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

    BeakerMode beakerMode;

    @PostConstruct
    public void init() {
        beakerControllers.add(this);

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
//            reinstallBeaker();
            lineLeft.setEndY(newValue.doubleValue());
            lineRight.setEndY(newValue.doubleValue());
        });

        ((StackPane) beakerPane.getParent()).widthProperty().addListener((observable, oldValue, newValue) -> {
            AnchorPane.setLeftAnchor(textTop, arcTickTop.getCenterX() + arcTickTop.getRadiusX() - textTop.getWrappingWidth() / 2);
            AnchorPane.setLeftAnchor(textBottom, arcTickBottom.getCenterX() + arcTickBottom.getRadiusX() - textBottom.getWrappingWidth() / 2);
        });
    }
}
