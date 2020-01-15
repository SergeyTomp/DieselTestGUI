package fi.stardex.sisu.ui.controllers.pumps.flow;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.updateModels.DifferentialFmUpdateModel;
import fi.stardex.sisu.util.ProgressIndicator;
import fi.stardex.sisu.util.enums.GUI_type;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;

import javax.annotation.PostConstruct;

public class PumpFlowController {

    @FXML private AnchorPane pumpFlowTextArea;
    @FXML private AnchorPane pumpDelivery;
    @FXML private AnchorPane pumpBackflow;
    @FXML private Label calibrationProgressLabel;
    @FXML private Label calibrationPercentLabel;
    @FXML private Label percentLabel;
    @FXML private ProgressBar calibrationProgressBar;

    @FXML private PumpBeakerController pumpDeliveryController;
    @FXML private PumpBeakerController pumpBackflowController;
    @FXML private PumpFlowTextAreaController pumpFlowTextAreaController;

    private I18N i18N;
    private DifferentialFmUpdateModel differentialFmUpdateModel;
    private ProgressIndicator progressIndicator;
    private GUI_TypeModel gui_typeModel;


    public PumpBeakerController getPumpDeliveryController() {
        return pumpDeliveryController;
    }
    public PumpBeakerController getPumpBackflowController() {
        return pumpBackflowController;
    }
    public PumpFlowTextAreaController getPumpFlowTextAreaController() {
        return pumpFlowTextAreaController;
    }

    public void setDifferentialFmUpdateModel(DifferentialFmUpdateModel differentialFmUpdateModel) {
        this.differentialFmUpdateModel = differentialFmUpdateModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    @PostConstruct
    public void init() {

        // Temporarily calibration is deactivated for Cr_Pump.
        // Uncomment setupCalibrationProgressBar() and delete showNode(...) below for activation.
        // Do not forget to do necessary uncommenting in DifferentialFMController.
//        setupCalibrationProgressBar();
        showNode(false, calibrationPercentLabel, calibrationProgressBar, calibrationProgressLabel, percentLabel);

        bindingI18N();
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

            if (oldValue == GUI_type.CR_Pump) {
                progressIndicator.stopIndication();
                return;
            }
            if (newValue == GUI_type.CR_Pump) {
                progressIndicator.startIndication();
            }
        });

        if (gui_typeModel.guiTypeProperty().get() == GUI_type.CR_Pump) {
            progressIndicator.startIndication();
        }
    }

    private void bindingI18N() {
        calibrationProgressLabel.textProperty().bind(i18N.createStringBinding("differentialFM.calibrationLabel"));
    }

    private void showNode(boolean show, Node... nodes) {
        for (Node node : nodes)
            node.setVisible(show);

    }
}
