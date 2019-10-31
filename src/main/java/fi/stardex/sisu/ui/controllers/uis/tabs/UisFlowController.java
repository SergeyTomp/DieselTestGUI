package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.UisFlowModel;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.FlowUnitObtainer.createUisDeliveryFlowUnitBinding;

public class UisFlowController {

    @FXML private Label flowLabel;
    @FXML private Label flowRangeLabel;
    @FXML private ComboBox<String> flowUnitsComboBox;
    @FXML private AnchorPane uisBeakerOne;
    @FXML private AnchorPane uisBeakerTwo;
    @FXML private AnchorPane uisBeakerThree;
    @FXML private AnchorPane uisBeakerFour;
    @FXML private AnchorPane uisBeakerFive;
    @FXML private AnchorPane uisBeakerSix;
    @FXML private AnchorPane uisBeakerSeven;
    @FXML private AnchorPane uisBeakerEight;

    @FXML private UisBeakerController uisBeakerOneController;
    @FXML private UisBeakerController uisBeakerTwoController;
    @FXML private UisBeakerController uisBeakerThreeController;
    @FXML private UisBeakerController uisBeakerFourController;
    @FXML private UisBeakerController uisBeakerFiveController;
    @FXML private UisBeakerController uisBeakerSixController;
    @FXML private UisBeakerController uisBeakerSevenController;
    @FXML private UisBeakerController uisBeakerEightController;

    private static final String MILLILITRE_PER_MINUTE = "ml/min";
    private static final String LITRE_PER_HOUR = "l/h";
    private I18N i18N;
    private UisFlowModel uisFlowModel;

    public UisBeakerController getUisBeakerOneController() {
        return uisBeakerOneController;
    }
    public UisBeakerController getUisBeakerTwoController() {
        return uisBeakerTwoController;
    }
    public UisBeakerController getUisBeakerThreeController() {
        return uisBeakerThreeController;
    }
    public UisBeakerController getUisBeakerFourController() {
        return uisBeakerFourController;
    }
    public UisBeakerController getUisBeakerFiveController() {
        return uisBeakerFiveController;
    }
    public UisBeakerController getUisBeakerSixController() {
        return uisBeakerSixController;
    }
    public UisBeakerController getUisBeakerSevenController() {
        return uisBeakerSevenController;
    }
    public UisBeakerController getUisBeakerEightController() {
        return uisBeakerEightController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setUisFlowModel(UisFlowModel uisFlowModel) {
        this.uisFlowModel = uisFlowModel;
    }

    @PostConstruct
    public void init() {
        bindingI18N();
        flowUnitsComboBox.getItems().setAll(MILLILITRE_PER_MINUTE, LITRE_PER_HOUR);
        createUisDeliveryFlowUnitBinding(flowUnitsComboBox);
        uisFlowModel.getFlowUnitsProperty().bind(flowUnitsComboBox.getSelectionModel().selectedItemProperty());
        flowUnitsComboBox.getSelectionModel().selectFirst();
        flowRangeLabel.textProperty().bind(uisFlowModel.getFlowRangeLabelProperty());
    }

    private void bindingI18N(){
        flowLabel.textProperty().bind(i18N.createStringBinding("h4.flow.label.delivery"));
    }
}
