package fi.stardex.sisu.spring;

import fi.stardex.sisu.model.*;
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
                                                               CustomPumpState customPumpState,
                                                               PumpTestModel pumpTestModel,
                                                               PumpTestListModel pumpTestListModel) {
        PumpsModelsListController pumpsModelsListController = mainSectionPumpsController.getPumpsModelsListController();
        pumpsModelsListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsModelsListController.setPumpModel(pumpModel);
        pumpsModelsListController.setCustomPumpState(customPumpState);
        pumpsModelsListController.setPumpTestModel(pumpTestModel);
        pumpsModelsListController.setPumpTestListModel(pumpTestListModel);
        return pumpsModelsListController;
    }

    @Bean
    @Autowired
    public StoreResetPrintController storeResetPrintController(MainSectionPumpsController mainSectionPumpsController) {
        return mainSectionPumpsController.getStoreResetPrintController();
    }

    @Bean
    @Autowired
    public TestModeController testModeController(MainSectionPumpsController mainSectionPumpsController,
                                                 PumpTestModeModel pumpTestModeModel) {
        TestModeController testModeController = mainSectionPumpsController.getTestModeController();
        testModeController.setPumpTestModeModel(pumpTestModeModel);
        return testModeController;
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
    public PumpTestListController pumpTestListController(MainSectionPumpsController mainSectionPumpsController,
                                                         PumpTestModel pumpTestModel,
                                                         PumpTestListModel pumpTestListModel,
                                                         PumpTestModeModel pumpTestModeModel,
                                                         AutoTestListLastChangeModel autoTestListLastChangeModel) {
        PumpTestListController pumpTestListController = mainSectionPumpsController.getPumpTestListController();
        pumpTestListController.setPumpTestModel(pumpTestModel);
        pumpTestListController.setPumpTestListModel(pumpTestListModel);
        pumpTestListController.setPumpTestModeModel(pumpTestModeModel);
        pumpTestListController.setAutoTestListLastChangeModel(autoTestListLastChangeModel);
        return pumpTestListController;
    }

    @Bean
    @Autowired
    public TestSpeedController testSpeedController(MainSectionPumpsController mainSectionPumpsController,
                                                   PumpTestModel pumpTestModel,
                                                   PumpTimeProgressModel pumpTimeProgressModel) {
        TestSpeedController testSpeedController = mainSectionPumpsController.getTestSpeedController();
        testSpeedController.setPumpTestModel(pumpTestModel);
        testSpeedController.setPumpTimeProgressModel(pumpTimeProgressModel);
        return testSpeedController;
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
                                                       Rescaler deliveryRescaler,
                                                       DeliveryFlowUnitsModel deliveryFlowUnitsModel,
                                                       DeliveryFlowRangeModel deliveryFlowRangeModel,
                                                       PumpFlowValuesModel pumpDeliveryFlowValuesModel,
                                                       PumpFlowTemperaturesModel pumpDeliveryFlowTemperaturesModel,
                                                       PumpTestModel pumpTestModel,
                                                       FlowViewModel flowViewModel) {
        PumpBeakerController pumpDeliveryController = pumpFlowController.getPumpDeliveryController();
        pumpDeliveryController.setI18N(i18N);
        pumpDeliveryController.setBeakerType(BeakerType.DELIVERY);
        pumpDeliveryController.setRescaler(deliveryRescaler);
        pumpDeliveryController.setDeliveryFlowRangeModel(deliveryFlowRangeModel);
        pumpDeliveryController.setDeliveryFlowUnitsModel(deliveryFlowUnitsModel);
        pumpDeliveryController.setPumpFlowValuesModel(pumpDeliveryFlowValuesModel);
        pumpDeliveryController.setPumpFlowTemperaturesModel(pumpDeliveryFlowTemperaturesModel);
        pumpDeliveryController.setPumpTestModel(pumpTestModel);
        pumpDeliveryController.setFlowViewModel(flowViewModel);
        pumpDeliveryController.setName("PumpDelivery");
        return pumpDeliveryController;
    }

    @Bean
    @Autowired
    public PumpBeakerController pumpBackflowController(PumpFlowController pumpFlowController,
                                                       Rescaler backFlowRescaler,
                                                       BackFlowUnitsModel backFlowUnitsModel,
                                                       BackFlowRangeModel backFlowRangeModel,
                                                       PumpFlowValuesModel pumpBackFlowValuesModel,
                                                       PumpFlowTemperaturesModel pumpBackFlowTemperaturesModel,
                                                       PumpTestModel pumpTestModel,
                                                       FlowViewModel flowViewModel) {
        PumpBeakerController pumpBackflowController = pumpFlowController.getPumpBackflowController();
        pumpBackflowController.setI18N(i18N);
        pumpBackflowController.setBeakerType(BeakerType.BACKFLOW);
        pumpBackflowController.setRescaler(backFlowRescaler);
        pumpBackflowController.setBackFlowUnitsModel(backFlowUnitsModel);
        pumpBackflowController.setBackFlowRangeModel(backFlowRangeModel);
        pumpBackflowController.setPumpFlowValuesModel(pumpBackFlowValuesModel);
        pumpBackflowController.setPumpFlowTemperaturesModel(pumpBackFlowTemperaturesModel);
        pumpBackflowController.setPumpTestModel(pumpTestModel);
        pumpBackflowController.setFlowViewModel(flowViewModel);
        pumpBackflowController.setName("PumpBackFlow");
        return pumpBackflowController;
    }

    @Bean
    @Autowired
    public PumpFlowTextAreaController pumpFlowTextAreaController(PumpFlowController pumpFlowController,
                                                                 PumpModel pumpModel,
                                                                 PumpTestModel pumpTestModel,
                                                                 I18N i18N,
                                                                 LanguageModel languageModel) {
        PumpFlowTextAreaController pumpFlowTextAreaController = pumpFlowController.getPumpFlowTextAreaController();
        pumpFlowTextAreaController.setPumpModel(pumpModel);
        pumpFlowTextAreaController.setPumpTestModel(pumpTestModel);
        pumpFlowTextAreaController.setI18N(i18N);
        pumpFlowTextAreaController.setLanguageModel(languageModel);
        return pumpFlowTextAreaController;
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
