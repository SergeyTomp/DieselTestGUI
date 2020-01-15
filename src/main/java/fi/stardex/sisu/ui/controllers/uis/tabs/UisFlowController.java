package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.UisFlowModel;
import fi.stardex.sisu.model.updateModels.DifferentialFmUpdateModel;
import fi.stardex.sisu.util.ProgressIndicator;
import fi.stardex.sisu.util.enums.GUI_type;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.FlowUnitObtainer.createUisDeliveryFlowUnitBinding;

public class UisFlowController {

    @FXML private Label flowRangeLabel;
    @FXML private Label flowLabel;
    @FXML private ComboBox<String> flowUnitsComboBox;
    @FXML private AnchorPane uisBeakerOne;
    @FXML private AnchorPane uisBeakerTwo;
    @FXML private AnchorPane uisBeakerThree;
    @FXML private AnchorPane uisBeakerFour;
    @FXML private AnchorPane uisBeakerFive;
    @FXML private AnchorPane uisBeakerSix;
    @FXML private AnchorPane uisBeakerSeven;
    @FXML private AnchorPane uisBeakerEight;
    @FXML private ProgressBar calibrationProgressBar;
    @FXML private Label calibrationProgressLabel;
    @FXML private Label calibrationPercentLabel;
    @FXML private Label percentLabel;

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
    private DifferentialFmUpdateModel differentialFmUpdateModel;
    private ProgressIndicator progressIndicator;
    private GUI_TypeModel gui_typeModel;

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
    public void setDifferentialFmUpdateModel(DifferentialFmUpdateModel differentialFmUpdateModel) {
        this.differentialFmUpdateModel = differentialFmUpdateModel;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    @PostConstruct
    public void init() {
        bindingI18N();
        flowUnitsComboBox.getItems().setAll(MILLILITRE_PER_MINUTE, LITRE_PER_HOUR);
        createUisDeliveryFlowUnitBinding(flowUnitsComboBox);
        uisFlowModel.getFlowUnitsProperty().bind(flowUnitsComboBox.getSelectionModel().selectedItemProperty());
        flowUnitsComboBox.getSelectionModel().selectFirst();
        flowRangeLabel.textProperty().bind(uisFlowModel.getFlowRangeLabelProperty());
        setupCalibrationProgressBar();
    }

    private void setupCalibrationProgressBar() {

        showNode(false, calibrationPercentLabel, calibrationProgressBar, calibrationProgressLabel, percentLabel);

        progressIndicator = ProgressIndicator
                .create()
                .progressBar(calibrationProgressBar)
                .progressLabel(calibrationPercentLabel)
                .progressValue(differentialFmUpdateModel.progressProperty())
                .showProgress(differentialFmUpdateModel.showProgressProperty())
                .hide(calibrationProgressBar, calibrationPercentLabel, calibrationProgressLabel, percentLabel)
                .build();

        gui_typeModel.guiTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (oldValue == GUI_type.UIS) {
                progressIndicator.stopIndication();
                return;
            }
            if (newValue == GUI_type.UIS) {
                progressIndicator.startIndication();
            }
        });

        if (gui_typeModel.guiTypeProperty().get() == GUI_type.UIS) {
            progressIndicator.startIndication();
        }
    }

    private void bindingI18N(){
        flowLabel.textProperty().bind(i18N.createStringBinding("h4.flow.label.delivery"));
        calibrationProgressLabel.textProperty().bind(i18N.createStringBinding("differentialFM.calibrationLabel"));
    }


    public void showNode(boolean show, Node... nodes) {
        for (Node node : nodes)
            node.setVisible(show);

    }
}
