package fi.stardex.sisu.spring;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.LanguageModel;
import fi.stardex.sisu.model.PressureSensorModel;
import fi.stardex.sisu.model.RegulationModesModel;
import fi.stardex.sisu.model.pump.*;
import fi.stardex.sisu.model.updateModels.DifferentialFmUpdateModel;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.persistence.repos.pump.PumpModelService;
import fi.stardex.sisu.persistence.repos.pump.PumpProducerService;
import fi.stardex.sisu.persistence.repos.pump.PumpTestNameService;
import fi.stardex.sisu.persistence.repos.pump.PumpTestService;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.CustomPumpState;
import fi.stardex.sisu.states.PumpHighPressureSectionPwrState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.pumps.PumpInfoController;
import fi.stardex.sisu.ui.controllers.pumps.PumpReportController;
import fi.stardex.sisu.ui.controllers.pumps.PumpTabSectionController;
import fi.stardex.sisu.ui.controllers.pumps.dialogs.CustomProducerPumpDialogController;
import fi.stardex.sisu.ui.controllers.pumps.dialogs.CustomPumpDialogController;
import fi.stardex.sisu.ui.controllers.pumps.dialogs.CustomPumpTestDialogController;
import fi.stardex.sisu.ui.controllers.pumps.flow.PumpBeakerController;
import fi.stardex.sisu.ui.controllers.pumps.flow.PumpFlowController;
import fi.stardex.sisu.ui.controllers.pumps.flow.PumpFlowTextAreaController;
import fi.stardex.sisu.ui.controllers.pumps.main.*;
import fi.stardex.sisu.ui.controllers.pumps.pressure.*;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.embed.swing.JFXPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.prefs.Preferences;

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
                                                         GUI_TypeModel gui_typeModel,
                                                         CustomPumpProducerDialogModel customPumpProducerDialogModel,
                                                         PumpProducerService pumpProducerService) {
        PumpsOEMListController pumpsOEMListController = mainSectionPumpsController.getPumpsOEMListController();
        pumpsOEMListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsOEMListController.setPumpModel(pumpModel);
        pumpsOEMListController.setPumpReportModel(pumpReportModel);
        pumpsOEMListController.setPumpsStartButtonState(pumpsStartButtonState);
        pumpsOEMListController.setGui_typeModel(gui_typeModel);
        pumpsOEMListController.setCustomPumpProducerDialogModel(customPumpProducerDialogModel);
        pumpsOEMListController.setProducerService(pumpProducerService);
        return pumpsOEMListController;
    }

    @Bean
    @Autowired
    public PumpsModelsListController pumpsModelsListController(MainSectionPumpsController mainSectionPumpsController,
                                                               ManufacturerPumpModel manufacturerPumpModel,
                                                               PumpModel pumpModel,
                                                               CustomPumpState customPumpState,
                                                               PumpTestListModel pumpTestListModel,
                                                               PumpReportModel pumpReportModel,
                                                               PumpsStartButtonState pumpsStartButtonState,
                                                               CustomPumpDialogModel customPumpDialogModel,
                                                               PumpModelService pumpModelService) {
        PumpsModelsListController pumpsModelsListController = mainSectionPumpsController.getPumpsModelsListController();
        pumpsModelsListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsModelsListController.setPumpModel(pumpModel);
        pumpsModelsListController.setCustomPumpState(customPumpState);
        pumpsModelsListController.setPumpTestListModel(pumpTestListModel);
        pumpsModelsListController.setPumpReportModel(pumpReportModel);
        pumpsModelsListController.setI18N(i18N);
        pumpsModelsListController.setPumpsStartButtonState(pumpsStartButtonState);
        pumpsModelsListController.setCustomPumpDialogModel(customPumpDialogModel);
        pumpsModelsListController.setPumpModelService(pumpModelService);
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
                                                         PumpsStartButtonState pumpsStartButtonState,
                                                         PumpModel pumpModel,
                                                         PumpTestService pumpTestService,
                                                         CustomPumpState customPumpState,
                                                         CustomPumpTestDialogModel customPumpTestDialogModel) {
        PumpTestListController pumpTestListController = mainSectionPumpsController.getPumpTestListController();
        pumpTestListController.setPumpTestModel(pumpTestModel);
        pumpTestListController.setPumpTestListModel(pumpTestListModel);
        pumpTestListController.setPumpTestModeModel(pumpTestModeModel);
        pumpTestListController.setAutoTestListLastChangeModel(autoTestListLastChangeModel);
        pumpTestListController.setPumpsStartButtonState(pumpsStartButtonState);
        pumpTestListController.setPumpModel(pumpModel);
        pumpTestListController.setPumpTestService(pumpTestService);
        pumpTestListController.setCustomPumpState(customPumpState);
        pumpTestListController.setCustomPumpTestDialogModel(customPumpTestDialogModel);
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
    public PumpFlowController pumpFlowController(PumpTabSectionController pumpTabSectionController,
                                                 DifferentialFmUpdateModel differentialFmUpdateModel,
                                                 I18N i18N,
                                                 GUI_TypeModel gui_typeModel) {
        PumpFlowController pumpFlowController = pumpTabSectionController.getPumpFlowController();
        pumpFlowController.setDifferentialFmUpdateModel(differentialFmUpdateModel);
        pumpFlowController.setI18N(i18N);
        pumpFlowController.setGui_typeModel(gui_typeModel);
        return pumpFlowController;
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
                                                       PumpReportModel pumpReportModel,
                                                       ModbusRegisterProcessor flowModbusWriter,
                                                       Preferences rootPrefs,
                                                       FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion) {
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
        pumpDeliveryController.setFlowModbusWriter(flowModbusWriter);
        pumpDeliveryController.setRootPrefs(rootPrefs);
        pumpDeliveryController.setFlowFirmwareVersion(flowFirmwareVersion);
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
                                                       PumpReportModel pumpReportModel,
                                                       ModbusRegisterProcessor flowModbusWriter,
                                                       Preferences rootPrefs) {
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
        pumpBackflowController.setFlowModbusWriter(flowModbusWriter);
        pumpBackflowController.setRootPrefs(rootPrefs);
        return pumpBackflowController;
    }

    @Bean
    @Autowired
    public PumpFlowTextAreaController pumpFlowTextAreaController(PumpFlowController pumpFlowController,
                                                                 PumpModel pumpModel,
                                                                 PumpTestModel pumpTestModel,
                                                                 I18N i18N,
                                                                 LanguageModel languageModel,
                                                                 PumpModelService pumpModelService) {
        PumpFlowTextAreaController pumpFlowTextAreaController = pumpFlowController.getPumpFlowTextAreaController();
        pumpFlowTextAreaController.setPumpModel(pumpModel);
        pumpFlowTextAreaController.setPumpTestModel(pumpTestModel);
        pumpFlowTextAreaController.setI18N(i18N);
        pumpFlowTextAreaController.setLanguageModel(languageModel);
        pumpFlowTextAreaController.setPumpModelService(pumpModelService);
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
                                                                               PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel,
                                                                               GUI_TypeModel gui_typeModel) {
        PumpRegulatorSectionOneController pumpRegulatorSectionOneController = pumpHighPressureSectionController.getPumpRegulatorSectionOneController();
        pumpRegulatorSectionOneController.setI18N(i18N);
        pumpRegulatorSectionOneController.setPumpHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpRegulatorSectionOneController.setPressureSensorModel(pressureSensorModel);
        pumpRegulatorSectionOneController.setUltimaModbusWriter(ultimaModbusWriter);
        pumpRegulatorSectionOneController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpRegulatorSectionOneController.setRegulationModesModel(regulationModesModel);
        pumpRegulatorSectionOneController.setPumpTestModel(pumpTestModel);
        pumpRegulatorSectionOneController.setPumpPressureRegulatorOneModel(pumpPressureRegulatorOneModel);
        pumpRegulatorSectionOneController.setGui_typeModel(gui_typeModel);
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
                                                                               PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel,
                                                                               GUI_TypeModel gui_typeModel) {
        PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController = pumpHighPressureSectionController.getPumpRegulatorSectionTwoController();
        pumpRegulatorSectionTwoController.setI18N(i18N);
        pumpRegulatorSectionTwoController.setHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpRegulatorSectionTwoController.setUltimaModbusWriter(ultimaModbusWriter);
        pumpRegulatorSectionTwoController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpRegulatorSectionTwoController.setRegulationModesModel(regulationModesModel);
        pumpRegulatorSectionTwoController.setPumpModel(pumpModel);
        pumpRegulatorSectionTwoController.setPumpTestModel(pumpTestModel);
        pumpRegulatorSectionTwoController.setPumpPressureRegulatorOneModel(pumpPressureRegulatorOneModel);
        pumpRegulatorSectionTwoController.setGui_typeModel(gui_typeModel);
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
                                                                                   PumpTestModel pumpTestModel,
                                                                                   GUI_TypeModel gui_typeModel) {
        PumpRegulatorSectionThreeController pumpRegulatorSectionThreeController = pumpHighPressureSectionController.getPumpRegulatorSectionThreeController();
        pumpRegulatorSectionThreeController.setI18N(i18N);
        pumpRegulatorSectionThreeController.setHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpRegulatorSectionThreeController.setUltimaModbusWriter(ultimaModbusWriter);
        pumpRegulatorSectionThreeController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpRegulatorSectionThreeController.setRegulationModesModel(regulationModesModel);
        pumpRegulatorSectionThreeController.setPumpModel(pumpModel);
        pumpRegulatorSectionThreeController.setPumpTestModel(pumpTestModel);
        pumpRegulatorSectionThreeController.setGui_typeModel(gui_typeModel);
        return pumpRegulatorSectionThreeController;
    }

    @Bean
    @Autowired
    public PumpHighPressureSectionLcdController pumpHighPressureSectionLcdController(PumpHighPressureSectionController pumpHighPressureSectionController,
                                                                                     HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                                                     FirmwareVersion<FlowFirmwareVersion.FlowVersions> flowFirmwareVersion) {
        PumpHighPressureSectionLcdController pumpHighPressureSectionLcdController = pumpHighPressureSectionController.getPumpHighPressureSectionLcdController();
        pumpHighPressureSectionLcdController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        pumpHighPressureSectionLcdController.setFlowFirmwareVersion(flowFirmwareVersion);
        return pumpHighPressureSectionLcdController;
    }

    @Bean
    @Autowired
    public PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController(PumpHighPressureSectionController pumpHighPressureSectionController,
                                                                                     PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState,
                                                                                     GUI_TypeModel gui_typeModel) {
        PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController = pumpHighPressureSectionController.getPumpHighPressureSectionPwrController();
        pumpHighPressureSectionPwrController.setPumpHighPressureSectionPwrState(pumpHighPressureSectionPwrState);
        pumpHighPressureSectionPwrController.setGui_typeModel(gui_typeModel);
        return pumpHighPressureSectionPwrController;
    }

    @Bean
    public ViewHolder customProducerPumpDialog() {
        return loadView("/fxml/pumps/dialogs/CustomProducerPumpDialog.fxml");
    }

    @Bean
    @Autowired
    public CustomProducerPumpDialogController customProducerPumpDialogController(GUI_TypeModel guiTypeModel,
                                                                                 I18N i18N,
                                                                                 PumpProducerService pumpProducerService,
                                                                                 CustomPumpProducerDialogModel customPumpProducerDialogModel,
                                                                                 ManufacturerPumpModel manufacturerPumpModel) {
        CustomProducerPumpDialogController customProducerPumpDialogController = (CustomProducerPumpDialogController)customProducerPumpDialog().getController();
        customProducerPumpDialogController.setCustomPumpProducerDialogModel(customPumpProducerDialogModel);
        customProducerPumpDialogController.setGuiTypeModel(guiTypeModel);
        customProducerPumpDialogController.setI18N(i18N);
        customProducerPumpDialogController.setPumpProducerService(pumpProducerService);
        customProducerPumpDialogController.setDialogViev(customProducerPumpDialog().getView());
        customProducerPumpDialogController.setManufacturerPumpModel(manufacturerPumpModel);
        return customProducerPumpDialogController;
    }

    @Bean
    public ViewHolder customPumpDialog() {
        return loadView("/fxml/pumps/dialogs/CustomPumpDialog.fxml");
    }

    @Bean
    @Autowired
    public CustomPumpDialogController customPumpDialogController(GUI_TypeModel guiTypeModel,
                                                                 I18N i18N,
                                                                 PumpModelService pumpModelService,
                                                                 PumpTestService pumpTestService,
                                                                 PumpModel pumpModel,
                                                                 CustomPumpDialogModel customPumpDialogModel,
                                                                 ManufacturerPumpModel manufacturerPumpModel,
                                                                 PumpTestListModel pumpTestListModel) {
        CustomPumpDialogController customPumpDialogController = (CustomPumpDialogController)customPumpDialog().getController();
        customPumpDialogController.setDialogViev(customPumpDialog().getView());
        customPumpDialogController.setCustomPumpDialogModel(customPumpDialogModel);
        customPumpDialogController.setI18N(i18N);
        customPumpDialogController.setGuiTypeModel(guiTypeModel);
        customPumpDialogController.setPumpModelService(pumpModelService);
        customPumpDialogController.setPumpTestService(pumpTestService);
        customPumpDialogController.setPumpModel(pumpModel);
        customPumpDialogController.setManufacturerPumpModel(manufacturerPumpModel);
        customPumpDialogController.setPumpTestListModel(pumpTestListModel);
        return customPumpDialogController;
    }

    @Bean
    public ViewHolder customPumpTestDialog() {
        return loadView("/fxml/pumps/dialogs/CustomPumpTestDialog.fxml");
    }

    @Bean
    @Autowired
    public CustomPumpTestDialogController customPumpTestDialogController(I18N i18N,
                                                                         GUI_TypeModel guiTypeModel,
                                                                         PumpTestService pumpTestService,
                                                                         CustomPumpTestDialogModel customPumpTestDialogModel,
                                                                         PumpTestNameService pumpTestNameService,
                                                                         PumpTestListModel pumpTestListModel,
                                                                         PumpTestModel pumpTestModel,
                                                                         PumpModel pumpModel) {
        CustomPumpTestDialogController customPumpTestDialogController = (CustomPumpTestDialogController)customPumpTestDialog().getController();
        customPumpTestDialogController.setI18N(i18N);
        customPumpTestDialogController.setGuiTypeModel(guiTypeModel);
        customPumpTestDialogController.setPumpTestService(pumpTestService);
        customPumpTestDialogController.setCustomPumpTestDialogModel(customPumpTestDialogModel);
        customPumpTestDialogController.setPumpTestNameService(pumpTestNameService);
        customPumpTestDialogController.setDialogViev(customPumpTestDialog().getView());
        customPumpTestDialogController.setPumpTestListModel(pumpTestListModel);
        customPumpTestDialogController.setPumpTestModel(pumpTestModel);
        customPumpTestDialogController.setPumpModel(pumpModel);
        return customPumpTestDialogController;
    }
}
