package fi.stardex.sisu.spring;

import fi.stardex.sisu.model.ManufacturerPumpModel;
import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.states.CustomPumpState;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.pumps.PumpInfoController;
import fi.stardex.sisu.ui.controllers.pumps.PumpReportController;
import fi.stardex.sisu.ui.controllers.pumps.PumpTabSectionController;
import fi.stardex.sisu.ui.controllers.pumps.flow.PumpBeakerController;
import fi.stardex.sisu.ui.controllers.pumps.flow.PumpFlowController;
import fi.stardex.sisu.ui.controllers.pumps.flow.PumpFlowTextAreaController;
import fi.stardex.sisu.ui.controllers.pumps.main.*;
import fi.stardex.sisu.ui.controllers.pumps.pressure.*;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.rescalers.Rescaler;
import javafx.embed.swing.JFXPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "fi.stardex.sisu")
public class JavaFXSpringConfigurePumps extends ViewLoader {

    public JavaFXSpringConfigurePumps(I18N i18N) {
        super(i18N);
        // Для работы JUnit, иначе бросается исключение toolkit not initialized
        new JFXPanel();
    }

    @Bean
    public ViewHolder mainSectionPumps() {
        return loadView("/fxml/pumps/main/MainSectionPumps.fxml");
    }

    @Bean
    ViewHolder pumpHighPressureSection() {
        return loadView("/fxml/pumps/pressure/PumpHighPressureSection.fxml");
    }

    @Bean
    public ViewHolder pumpTabSection() {
        return loadView("/fxml/pumps/TabSectionPumps.fxml");
    }

    @Bean
    @Autowired
    public MainSectionPumpsController mainSectionPumpsController(PumpModel pumpModel) {
        MainSectionPumpsController mainSectionPumpsController = (MainSectionPumpsController) mainSectionPumps().getController();
        mainSectionPumpsController.setPumpModel(pumpModel);
        return mainSectionPumpsController;
    }

    @Bean
    @Autowired
    public PumpsOEMListController pumpsOEMListController(MainSectionPumpsController mainSectionPumpsController,
                                                         ManufacturerPumpModel manufacturerPumpModel,
                                                         PumpModel pumpModel) {
        PumpsOEMListController pumpsOEMListController = mainSectionPumpsController.getPumpsOEMListController();
        pumpsOEMListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsOEMListController.setPumpModel(pumpModel);
        return pumpsOEMListController;
    }

    @Bean
    @Autowired
    public PumpsModelsListController pumpsModelsListController(MainSectionPumpsController mainSectionPumpsController,
                                                               ManufacturerPumpModel manufacturerPumpModel,
                                                               PumpModel pumpModel,
                                                               CustomPumpState customPumpState) {
        PumpsModelsListController pumpsModelsListController = mainSectionPumpsController.getPumpsModelsListController();
        pumpsModelsListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsModelsListController.setPumpModel(pumpModel);
        pumpsModelsListController.setCustomPumpState(customPumpState);
        return pumpsModelsListController;
    }

    @Bean
    @Autowired
    public StoreResetPrintController storeResetPrintController(MainSectionPumpsController mainSectionPumpsController) {
        return mainSectionPumpsController.getStoreResetPrintController();
    }

    @Bean
    @Autowired
    public TestModeController testModeController(MainSectionPumpsController mainSectionPumpsController) {
        return mainSectionPumpsController.getTestModeController();
    }

    @Bean
    @Autowired
    public PumpFieldController pumpFieldController(MainSectionPumpsController mainSectionPumpsController,
                                                   PumpModel pumpModel) {
        PumpFieldController pumpFieldController = mainSectionPumpsController.getPumpFieldController();
        pumpFieldController.setPumpModel(pumpModel);
        return pumpFieldController;
    }

    @Bean
    @Autowired
    public TestListController testListController(MainSectionPumpsController mainSectionPumpsController) {
        return mainSectionPumpsController.getTestListController();
    }

    @Bean
    @Autowired
    public TestSpeedController testSpeedController(MainSectionPumpsController mainSectionPumpsController) {
        return mainSectionPumpsController.getTestSpeedController();
    }

    @Bean
    @Autowired
    public StartButtonController startButtonController(MainSectionPumpsController mainSectionPumpsController) {
        return mainSectionPumpsController.getStartButtonController();
    }

    @Bean
    @Autowired
    public PumpTabSectionController pumpsTabSectionController(I18N i18N) {
        PumpTabSectionController pumpTabSectionController = (PumpTabSectionController) pumpTabSection().getController();
        pumpTabSectionController.setI18N(i18N);
        return pumpTabSectionController;
    }

    @Bean
    @Autowired
    public PumpFlowController pumpFlowController(PumpTabSectionController pumpTabSectionController) {
        return pumpTabSectionController.getPumpFlowController();
    }

    @Bean
    @Autowired
    public PumpBeakerController pumpDeliveryController(PumpFlowController pumpFlowController,
                                                       Rescaler deliveryRescaler) {
        PumpBeakerController pumpDeliveryController = pumpFlowController.getPumpDeliveryController();
        pumpDeliveryController.setI18N(i18N);
        pumpDeliveryController.setBeakerType(BeakerType.DELIVERY);
        pumpDeliveryController.setRescaler(deliveryRescaler);
        return pumpDeliveryController;
    }

    @Bean
    @Autowired
    public PumpBeakerController pumpBackflowController(PumpFlowController pumpFlowController,
                                                       Rescaler backFlowRescaler) {
        PumpBeakerController pumpBackflowController = pumpFlowController.getPumpBackflowController();
        pumpBackflowController.setI18N(i18N);
        pumpBackflowController.setBeakerType(BeakerType.BACKFLOW);
        pumpBackflowController.setRescaler(backFlowRescaler);
        return pumpBackflowController;
    }

    @Bean
    @Autowired
    public PumpFlowTextAreaController pumpFlowTextAreaController(PumpFlowController pumpFlowController) {
        return pumpFlowController.getPumpFlowTextAreaController();
    }

    @Bean
    @Autowired
    public PumpReportController pumpReportController(PumpTabSectionController pumpTabSectionController) {
        return pumpTabSectionController.getPumpReportController();
    }

    @Bean
    @Autowired
    public PumpInfoController pumpInfoController(PumpTabSectionController pumpTabSectionController) {
        return pumpTabSectionController.getPumpInfoController();
    }

    @Bean
    public PumpHighPressureSectionController pumpHighPressureSectionController() {
        return (PumpHighPressureSectionController) pumpHighPressureSection().getController();
    }

    @Bean
    @Autowired
    public PumpRegulatorSectionOneController pumpRegulatorSectionOneController(PumpHighPressureSectionController pumpHighPressureSectionController) {
        PumpRegulatorSectionOneController pumpRegulatorSectionOneController = pumpHighPressureSectionController.getPumpRegulatorSectionOneController();
        pumpRegulatorSectionOneController.setI18N(i18N);
        return pumpRegulatorSectionOneController;
    }

    @Bean
    @Autowired
    public PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController(PumpHighPressureSectionController pumpHighPressureSectionController) {
        PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController = pumpHighPressureSectionController.getPumpRegulatorSectionTwoController();
        pumpRegulatorSectionTwoController.setI18N(i18N);
        return pumpRegulatorSectionTwoController;
    }

    @Bean
    @Autowired
    public PumpRegulatorSectionThreeController pumpRegulatorSectionThreeController(PumpHighPressureSectionController pumpHighPressureSectionController) {
        PumpRegulatorSectionThreeController pumpRegulatorSectionThreeController = pumpHighPressureSectionController.getPumpRegulatorSectionThreeController();
        pumpRegulatorSectionThreeController.setI18N(i18N);
        return pumpRegulatorSectionThreeController;
    }

    @Bean
    @Autowired
    public PumpHighPressureSectionLcdController pumpHighPressureSectionLcdController(PumpHighPressureSectionController pumpHighPressureSectionController) {
        return pumpHighPressureSectionController.getPumpHighPressureSectionLcdController();
    }

    @Bean
    @Autowired
    public PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController(PumpHighPressureSectionController pumpHighPressureSectionController) {
        return pumpHighPressureSectionController.getPumpHighPressureSectionPwrController();
    }

}
