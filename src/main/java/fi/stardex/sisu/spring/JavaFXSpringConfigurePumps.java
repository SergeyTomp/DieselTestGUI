package fi.stardex.sisu.spring;

import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.CustomPumpState;
import fi.stardex.sisu.states.PumpHighPressureSectionPwrState;
import fi.stardex.sisu.states.PumpsStartButtonState;
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
                                                         PumpModel pumpModel,
                                                         PumpReportModel pumpReportModel,
                                                         PumpsStartButtonState pumpsStartButtonState,
                                                         GUI_TypeModel gui_typeModel) {
        PumpsOEMListController pumpsOEMListController = mainSectionPumpsController.getPumpsOEMListController();
        pumpsOEMListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsOEMListController.setPumpModel(pumpModel);
        pumpsOEMListController.setPumpReportModel(pumpReportModel);
        pumpsOEMListController.setPumpsStartButtonState(pumpsStartButtonState);
        pumpsOEMListController.setGui_typeModel(gui_typeModel);
        return pumpsOEMListController;
    }

    @Bean
    @Autowired
    public PumpsModelsListController pumpsModelsListController(MainSectionPumpsController mainSectionPumpsController,
                                                               ManufacturerPumpModel manufacturerPumpModel,
                                                               PumpModel pumpModel,
                                                               CustomPumpState customPumpState,
                                                               PumpTestModel pumpTestModel,
                                                               PumpTestListModel pumpTestListModel,
                                                               PumpReportModel pumpReportModel,
                                                               PumpsStartButtonState pumpsStartButtonState) {
        PumpsModelsListController pumpsModelsListController = mainSectionPumpsController.getPumpsModelsListController();
        pumpsModelsListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsModelsListController.setPumpModel(pumpModel);
        pumpsModelsListController.setCustomPumpState(customPumpState);
        pumpsModelsListController.setPumpTestModel(pumpTestModel);
        pumpsModelsListController.setPumpTestListModel(pumpTestListModel);
        pumpsModelsListController.setPumpReportModel(pumpReportModel);
        pumpsModelsListController.setI18N(i18N);
        pumpsModelsListController.setPumpsStartButtonState(pumpsStartButtonState);
        return pumpsModelsListController;
    }

    @Bean
    @Autowired
    public StoreResetPrintController storeResetPrintController(MainSectionPumpsController mainSectionPumpsController,
                                                               ModbusRegisterProcessor flowModbusWriter,
                                                               PumpReportModel pumpReportModel,
                                                               PumpsStartButtonState pumpsStartButtonState,
                                                               PumpTestModeModel pumpTestModeModel,
                                                               ViewHolder printDialogPanel) {
        StoreResetPrintController storeResetPrintController = mainSectionPumpsController.getStoreResetPrintController();
        storeResetPrintController.setFlowModbusWriter(flowModbusWriter);
        storeResetPrintController.setPumpReportModel(pumpReportModel);
        storeResetPrintController.setPumpsStartButtonState(pumpsStartButtonState);
        storeResetPrintController.setPumpTestModeModel(pumpTestModeModel);
        storeResetPrintController.setPrintDialogPanel(printDialogPanel);
        return storeResetPrintController;
    }

    @Bean
    @Autowired
    public TestModeController testModeController(MainSectionPumpsController mainSectionPumpsController,
                                                 PumpTestModeModel pumpTestModeModel,
                                                 PumpsStartButtonState pumpsStartButtonState) {
        TestModeController testModeController = mainSectionPumpsController.getTestModeController();
        testModeController.setPumpTestModeModel(pumpTestModeModel);
        testModeController.setI18N(i18N);
        testModeController.setPumpsStartButtonState(pumpsStartButtonState);
        return testModeController;
    }

    @Bean
    @Autowired
    public PumpFieldController pumpFieldController(MainSectionPumpsController mainSectionPumpsController,
                                                   PumpModel pumpModel,
                                                   PumpsStartButtonState pumpsStartButtonState) {
        PumpFieldController pumpFieldController = mainSectionPumpsController.getPumpFieldController();
        pumpFieldController.setPumpModel(pumpModel);
        pumpFieldController.setPumpsStartButtonState(pumpsStartButtonState);
        return pumpFieldController;
    }

    @Bean
    @Autowired
    public PumpTestListController pumpTestListController(MainSectionPumpsController mainSectionPumpsController,
                                                         PumpTestModel pumpTestModel,
                                                         PumpTestListModel pumpTestListModel,
                                                         PumpTestModeModel pumpTestModeModel,
                                                         AutoTestListLastChangeModel autoTestListLastChangeModel,
                                                         PumpsStartButtonState pumpsStartButtonState) {
        PumpTestListController pumpTestListController = mainSectionPumpsController.getPumpTestListController();
        pumpTestListController.setPumpTestModel(pumpTestModel);
        pumpTestListController.setPumpTestListModel(pumpTestListModel);
        pumpTestListController.setPumpTestModeModel(pumpTestModeModel);
        pumpTestListController.setAutoTestListLastChangeModel(autoTestListLastChangeModel);
        pumpTestListController.setPumpsStartButtonState(pumpsStartButtonState);
        return pumpTestListController;
    }

    @Bean
    @Autowired
    public TestSpeedController testSpeedController(MainSectionPumpsController mainSectionPumpsController,
                                                   PumpTestModel pumpTestModel,
                                                   PumpTimeProgressModel pumpTimeProgressModel,
                                                   PumpTestSpeedModel pumpTestSpeedModel,
                                                   PumpsStartButtonState pumpsStartButtonState,
                                                   PumpTestModeModel pumpTestModeModel) {
        TestSpeedController testSpeedController = mainSectionPumpsController.getTestSpeedController();
        testSpeedController.setPumpTestModel(pumpTestModel);
        testSpeedController.setPumpTimeProgressModel(pumpTimeProgressModel);
        testSpeedController.setI18N(i18N);
        testSpeedController.setPumpTestSpeedModel(pumpTestSpeedModel);
        testSpeedController.setPumpsStartButtonState(pumpsStartButtonState);
        testSpeedController.setPumpTestModeModel(pumpTestModeModel);
        return testSpeedController;
    }

    @Bean
    @Autowired
    public StartButtonController startButtonController(MainSectionPumpsController mainSectionPumpsController,
                                                       PumpsStartButtonState pumpsStartButtonState,
                                                       PumpTestListModel pumpTestListModel,
                                                       PumpTestModeModel pumpTestModeModel) {
        StartButtonController startButtonController = mainSectionPumpsController.getStartButtonController();
        startButtonController.setPumpsStartButtonState(pumpsStartButtonState);
        startButtonController.setPumpTestListModel(pumpTestListModel);
        startButtonController.setPumpTestModeModel(pumpTestModeModel);
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
                                                       Rescaler pumpDeliveryRescaler,
                                                       FlowUnitsModel pumpDeliveryFlowUnitsModel,
                                                       FlowRangeModel pumpDeliveryFlowRangeModel,
                                                       PumpFlowValuesModel pumpDeliveryFlowValuesModel,
                                                       PumpFlowTemperaturesModel pumpDeliveryFlowTemperaturesModel,
                                                       PumpTestModel pumpTestModel,
                                                       FlowViewModel flowViewModel,
                                                       PumpReportModel pumpReportModel) {
        PumpBeakerController pumpDeliveryController = pumpFlowController.getPumpDeliveryController();
        pumpDeliveryController.setI18N(i18N);
        pumpDeliveryController.setBeakerType(BeakerType.DELIVERY);
        pumpDeliveryController.setRescaler(pumpDeliveryRescaler);
        pumpDeliveryController.setPumpFlowValuesModel(pumpDeliveryFlowValuesModel);
        pumpDeliveryController.setPumpFlowTemperaturesModel(pumpDeliveryFlowTemperaturesModel);
        pumpDeliveryController.setPumpTestModel(pumpTestModel);
        pumpDeliveryController.setFlowViewModel(flowViewModel);
        pumpDeliveryController.setName("PumpDelivery");
        pumpDeliveryController.setFlowRangeModel(pumpDeliveryFlowRangeModel);
        pumpDeliveryController.setFlowUnitsModel(pumpDeliveryFlowUnitsModel);
        pumpDeliveryController.setPumpReportModel(pumpReportModel);
        return pumpDeliveryController;
    }

    @Bean
    @Autowired
    public PumpBeakerController pumpBackflowController(PumpFlowController pumpFlowController,
                                                       Rescaler pumpBackFlowRescaler,
                                                       FlowUnitsModel pumpBackFlowUnitsModel,
                                                       FlowRangeModel pumpBackFlowRangeModel,
                                                       PumpFlowValuesModel pumpBackFlowValuesModel,
                                                       PumpFlowTemperaturesModel pumpBackFlowTemperaturesModel,
                                                       PumpTestModel pumpTestModel,
                                                       FlowViewModel flowViewModel,
                                                       PumpReportModel pumpReportModel) {
        PumpBeakerController pumpBackflowController = pumpFlowController.getPumpBackflowController();
        pumpBackflowController.setI18N(i18N);
        pumpBackflowController.setBeakerType(BeakerType.BACKFLOW);
        pumpBackflowController.setRescaler(pumpBackFlowRescaler);
        pumpBackflowController.setPumpFlowValuesModel(pumpBackFlowValuesModel);
        pumpBackflowController.setPumpFlowTemperaturesModel(pumpBackFlowTemperaturesModel);
        pumpBackflowController.setPumpTestModel(pumpTestModel);
        pumpBackflowController.setFlowViewModel(flowViewModel);
        pumpBackflowController.setName("PumpBackFlow");
        pumpBackflowController.setFlowUnitsModel(pumpBackFlowUnitsModel);
        pumpBackflowController.setFlowRangeModel(pumpBackFlowRangeModel);
        pumpBackflowController.setPumpReportModel(pumpReportModel);
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
    public PumpReportController pumpReportController(PumpTabSectionController pumpTabSectionController,
                                                     I18N i18N,
                                                     PumpReportModel pumpReportModel,
                                                     PumpsStartButtonState pumpsStartButtonState) {
        PumpReportController pumpReportController = pumpTabSectionController.getPumpReportController();
        pumpReportController.setI18N(i18N);
        pumpReportController.setPumpReportModel(pumpReportModel);
        pumpReportController.setPumpsStartButtonState(pumpsStartButtonState);
        return pumpReportController;
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
    public PumpRegulatorSectionOneController pumpRegulatorSectionOneController(PumpHighPressureSectionController pumpHighPressureSectionController,
                                                                               PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState,
                                                                               PressureSensorModel pressureSensorModel,
                                                                               ModbusRegisterProcessor ultimaModbusWriter,
                                                                               HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                                               RegulationModesModel regulationModesModel,
                                                                               PumpTestModel pumpTestModel,
                                                                               PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel) {
        PumpRegulatorSectionOneController pumpRegulatorSectionOneController = pumpHighPressureSectionController.getPumpRegulatorSectionOneController();
        pumpRegulatorSectionOneController.setI18N(i18N);
        pumpRegulatorSectionOneController.setPumpHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpRegulatorSectionOneController.setPressureSensorModel(pressureSensorModel);
        pumpRegulatorSectionOneController.setUltimaModbusWriter(ultimaModbusWriter);
        pumpRegulatorSectionOneController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpRegulatorSectionOneController.setRegulationModesModel(regulationModesModel);
        pumpRegulatorSectionOneController.setPumpTestModel(pumpTestModel);
        pumpRegulatorSectionOneController.setPumpPressureRegulatorOneModel(pumpPressureRegulatorOneModel);
        return pumpRegulatorSectionOneController;
    }

    @Bean
    @Autowired
    public PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController(PumpHighPressureSectionController pumpHighPressureSectionController,
                                                                               PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState,
                                                                               ModbusRegisterProcessor ultimaModbusWriter,
                                                                               HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                                               RegulationModesModel regulationModesModel,
                                                                               PumpModel pumpModel,
                                                                               PumpTestModel pumpTestModel,
                                                                               PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel) {
        PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController = pumpHighPressureSectionController.getPumpRegulatorSectionTwoController();
        pumpRegulatorSectionTwoController.setI18N(i18N);
        pumpRegulatorSectionTwoController.setHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpRegulatorSectionTwoController.setUltimaModbusWriter(ultimaModbusWriter);
        pumpRegulatorSectionTwoController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpRegulatorSectionTwoController.setRegulationModesModel(regulationModesModel);
        pumpRegulatorSectionTwoController.setPumpModel(pumpModel);
        pumpRegulatorSectionTwoController.setPumpTestModel(pumpTestModel);
        pumpRegulatorSectionTwoController.setPumpPressureRegulatorOneModel(pumpPressureRegulatorOneModel);
        return pumpRegulatorSectionTwoController;
    }

    @Bean
    @Autowired
    public PumpRegulatorSectionThreeController pumpRegulatorSectionThreeController(PumpHighPressureSectionController pumpHighPressureSectionController,
                                                                                   PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState,
                                                                                   ModbusRegisterProcessor ultimaModbusWriter,
                                                                                   HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                                                   RegulationModesModel regulationModesModel,
                                                                                   PumpModel pumpModel,
                                                                                   PumpTestModel pumpTestModel) {
        PumpRegulatorSectionThreeController pumpRegulatorSectionThreeController = pumpHighPressureSectionController.getPumpRegulatorSectionThreeController();
        pumpRegulatorSectionThreeController.setI18N(i18N);
        pumpRegulatorSectionThreeController.setHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpRegulatorSectionThreeController.setUltimaModbusWriter(ultimaModbusWriter);
        pumpRegulatorSectionThreeController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpRegulatorSectionThreeController.setRegulationModesModel(regulationModesModel);
        pumpRegulatorSectionThreeController.setPumpModel(pumpModel);
        pumpRegulatorSectionThreeController.setPumpTestModel(pumpTestModel);
        return pumpRegulatorSectionThreeController;
    }

    @Bean
    @Autowired
    public PumpHighPressureSectionLcdController pumpHighPressureSectionLcdController(PumpHighPressureSectionController pumpHighPressureSectionController,
                                                                                     HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        PumpHighPressureSectionLcdController pumpHighPressureSectionLcdController = pumpHighPressureSectionController.getPumpHighPressureSectionLcdController();
        pumpHighPressureSectionLcdController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        return pumpHighPressureSectionLcdController;
    }

    @Bean
    @Autowired
    public PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController(PumpHighPressureSectionController pumpHighPressureSectionController,
                                                                                     PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState) {
        PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController = pumpHighPressureSectionController.getPumpHighPressureSectionPwrController();
        pumpHighPressureSectionPwrController.setPumpHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        return pumpHighPressureSectionPwrController;
    }

}
