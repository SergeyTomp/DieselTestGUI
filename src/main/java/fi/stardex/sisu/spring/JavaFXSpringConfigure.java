package fi.stardex.sisu.spring;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.measurement.Measurements;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.model.updateModels.InjectorSectionUpdateModel;
import fi.stardex.sisu.model.updateModels.PiezoRepairUpdateModel;
import fi.stardex.sisu.pdf.PDFService;
import fi.stardex.sisu.persistence.repos.ISADetectionRepository;
import fi.stardex.sisu.persistence.repos.InjectorTypeRepository;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.*;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.settings.*;
import fi.stardex.sisu.states.*;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.GUI_TypeController;
import fi.stardex.sisu.ui.controllers.ISADetectionController;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import fi.stardex.sisu.ui.controllers.additional.TabSectionController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.FirmwareDialogController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.additional.tabs.*;
import fi.stardex.sisu.ui.controllers.additional.tabs.info.*;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.DelayReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.FlowReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.RLC_ReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.ReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.settings.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.settings.FirmwareButtonController;
import fi.stardex.sisu.ui.controllers.additional.tabs.settings.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.*;
import fi.stardex.sisu.ui.controllers.dialogs.*;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.ui.controllers.pumps.CalibrationTestErrorController;
import fi.stardex.sisu.ui.controllers.pumps.PumpTabSectionController;
import fi.stardex.sisu.ui.controllers.pumps.SCVCalibrationController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpHighPressureSectionPwrController;
import fi.stardex.sisu.ui.controllers.pumps.pressure.PumpRegulatorSectionTwoController;
import fi.stardex.sisu.ui.controllers.uis.RootLayoutController;
import fi.stardex.sisu.model.updateModels.TachometerUltimaUpdateModel;
import fi.stardex.sisu.model.updateModels.TestBenchSectionUpdateModel;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.util.prefs.Preferences;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;

@Configuration
@Import(JavaFXSpringConfigurePumps.class)
@ComponentScan(value = "fi.stardex.sisu")
@PropertySource("classpath:properties/app.properties")
public class JavaFXSpringConfigure extends ViewLoader{

    @Value("${stardex.version}")
    public String softVersionNum;

    public JavaFXSpringConfigure(I18N i18N) {
        super(i18N);
        // Для работы JUnit, иначе бросается исключение toolkit not initialized
        new JFXPanel();
    }

    @Bean
    public ViewHolder rootLayout() {
        return loadView("/fxml/RootLayout.fxml");
    }

    @Bean
    public RootLayoutController rootLayoutController() {
        return (RootLayoutController) rootLayout().getController();
    }

    @Bean
    public ViewHolder gui_type() {
        return loadView("/fxml/GUI_Type.fxml");
    }

    // Depends on нужен для инициализации листенера в бине pumpsOEMListController,
    // чтобы GUI_TypeController инстанцировался только после этого,
    // иначе при входе в режиме Pump не всегда появляется список ОЕМ пока не переключиться в инжекторы и вернуться обратно
    @Bean
    @Autowired
    @DependsOn({"pumpsOEMListController", "checkAndInitializeBD", "mainSectionController"})
    public GUI_TypeController gui_typeController(Preferences rootPreferences,
                                                 RootLayoutController rootLayoutController,
                                                 TabSectionController tabSectionController,
                                                 PumpTabSectionController pumpTabSectionController,
                                                 DimasGUIEditionState dimasGUIEditionState,
                                                 ViewHolder mainSectionPumps,
                                                 ViewHolder pumpHighPressureSection,
                                                 ViewHolder pumpTabSection,
                                                 ViewHolder settings,
                                                 ViewHolder connection,
                                                 ManufacturerPumpModel manufacturerPumpModel,
                                                 PumpsStartButtonState pumpsStartButtonState,
                                                 GUI_TypeModel gui_typeModel,
                                                 MainSectionModel mainSectionModel,
                                                 InjectorSectionPwrState injectorSectionPwrState) {
        GUI_TypeController gui_typeController = rootLayoutController.getGui_typeController();
        gui_typeController.setRootPreferences(rootPreferences);
        gui_typeController.setMainSection(mainSection().getView());
        gui_typeController.setMainSectionPumps(mainSectionPumps.getView());
        gui_typeController.setCRSection(crSection().getView());
        gui_typeController.setUISSection(uisSection().getView());
        gui_typeController.setTabSection(tabSection().getView());
        gui_typeController.setSettingsGridPaneCR(tabSectionController.getSettingsGridPane());
        gui_typeController.setSettingsGridPanePumps(pumpTabSectionController.getSettingsGridPane());
        gui_typeController.setMainSectionGridPane(rootLayoutController.getMainSectionGridPane());
        gui_typeController.setAdditionalSectionGridPane(rootLayoutController.getAdditionalSectionGridPane());
        gui_typeController.setPumpSection(pumpHighPressureSection.getView());
        gui_typeController.setTabSectionPumps(pumpTabSection.getView());
        gui_typeController.setSettings(settings.getView());
        gui_typeController.setConnection(connection.getView());
        gui_typeController.setDimasGUIEditionState(dimasGUIEditionState);
        gui_typeController.setManufacturerPumpModel(manufacturerPumpModel);
        gui_typeController.setPumpsStartButtonState(pumpsStartButtonState);
        gui_typeController.setGui_typeModel(gui_typeModel);
        gui_typeController.setMainSectionModel(mainSectionModel);
        gui_typeController.setInjectorSectionPwrState(injectorSectionPwrState);
        return gui_typeController;
    }

    @Bean
    public ViewHolder mainSection() {
        return loadView("/fxml/sections/Main/MainSection.fxml");
    }

    @Bean
    @Autowired
    public MainSectionController mainSectionController(InjectorsRepository injectorsRepository,
                                                       InjectorTestRepository injectorTestRepository,
                                                       @Lazy ModbusRegisterProcessor flowModbusWriter,
                                                       @Lazy Measurements measurements,
                                                       BoostUadjustmentState boostUadjustmentState,
                                                       CodingReportModel codingReportModel,
                                                       DelayReportModel delayReportModel,
                                                       RLC_ReportModel rlc_reportModel,
                                                       FlowReportModel flowReportModel,
                                                       InjectorTestModel injectorTestModel,
                                                       InjectorModel injectorModel,
                                                       ModbusRegisterProcessor ultimaModbusWriter,
                                                       ManufacturerRepository manufacturerRepository,
                                                       GUI_TypeModel gui_typeModel,
                                                       ManufacturerMenuDialogModel manufacturerMenuDialogModel,
                                                       MainSectionModel mainSection_model,
                                                       InjectorSectionPwrState injectorSectionPwrState,
                                                       NewEditInjectorDialogModel newEditInjectorDialogModel) {
        MainSectionController mainSectionController = (MainSectionController) mainSection().getController();
        mainSectionController.setNewEditInjectorDialog(newEditInjectorDialog());
        mainSectionController.setNewEditTestDialog(newEditTestDialog());
        mainSectionController.setInjectorsRepository(injectorsRepository);
        mainSectionController.setInjectorTestRepository(injectorTestRepository);
        mainSectionController.setFlowModbusWriter(flowModbusWriter);
        mainSectionController.setI18N(i18N);
        mainSectionController.setMeasurements(measurements);
        mainSectionController.setPrintDialogPanel(printDialogPanel());
        mainSectionController.setBoostUadjustmentState(boostUadjustmentState);
        mainSectionController.setDelayReportModel(delayReportModel);
        mainSectionController.setRlc_reportModel(rlc_reportModel);
        mainSectionController.setCodingReportModel(codingReportModel);
        mainSectionController.setFlowReportModel(flowReportModel);
        mainSectionController.setInjectorTestModel(injectorTestModel);
        mainSectionController.setInjectorModel(injectorModel);
        mainSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        mainSectionController.setManufacturerRepository(manufacturerRepository);
        mainSectionController.setGui_typeModel(gui_typeModel);
        mainSectionController.setManufacturerMenuDialogModel(manufacturerMenuDialogModel);
        mainSectionController.setMainSectionModel(mainSection_model);
        mainSectionController.setInjectorSectionPwrState(injectorSectionPwrState);
        mainSectionController.setNewEditInjectorDialogModel(newEditInjectorDialogModel);
        return mainSectionController;
    }

    @Bean
    public ViewHolder crSection() {
        return loadView("/fxml/sections/CR/CRSection.fxml");
    }

    @Bean
    public CRSectionController crSectionController() {
        return (CRSectionController) crSection().getController();
    }

    @Bean
    @Autowired
    public TestBenchSectionController testBenchSectionController(RootLayoutController rootLayoutController,
                                                                 @Lazy ModbusRegisterProcessor flowModbusWriter,
                                                                 @Lazy ModbusRegisterProcessor standModbusWriter,
                                                                 DimasGUIEditionState dimasGUIEditionState,
                                                                 FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                                                 FirmwareVersion<StandVersions> standFirmwareVersion,
                                                                 PumpTestModel pumpTestModel,
                                                                 PumpModel pumpModel,
                                                                 InjectorTestModel injectorTestModel,
                                                                 TestBenchSectionUpdateModel testBenchSectionUpdateModel,
                                                                 TachometerUltimaUpdateModel tachometerUltimaUpdateModel,
                                                                 TestBenchSectionModel testBenchSectionModel,
                                                                 ModbusConnect flowModbusConnect,
                                                                 ModbusConnect standModbusConnect) {
        TestBenchSectionController testBenchSectionController = rootLayoutController.getTestBenchSectionController();
        testBenchSectionController.setFlowModbusWriter(flowModbusWriter);
        testBenchSectionController.setStandModbusWriter(standModbusWriter);
        testBenchSectionController.setI18N(i18N);
        testBenchSectionController.setDimasGUIEditionState(dimasGUIEditionState);
        testBenchSectionController.setFlowFirmwareVersion(flowFirmwareVersion);
        testBenchSectionController.setStandFirmwareVersion(standFirmwareVersion);
        testBenchSectionController.setPumpTestModel(pumpTestModel);
        testBenchSectionController.setPumpModel(pumpModel);
        testBenchSectionController.setInjectorTestModel(injectorTestModel);
        testBenchSectionController.setTestBenchSectionUpdateModel(testBenchSectionUpdateModel);
        testBenchSectionController.setTachometerUltimaUpdateModel(tachometerUltimaUpdateModel);
        testBenchSectionController.setTestBenchSectionModel(testBenchSectionModel);
        testBenchSectionController.setFlowModbusConnect(flowModbusConnect);
        testBenchSectionController.setStandModbusConnect(standModbusConnect);
        return testBenchSectionController;
    }

    @Bean
    @Autowired
    @DependsOn("regulatorsQTYController")
    public InjectorHighPressureSectionController injectorHighPressureSectionController(CRSectionController crSectionController,
                                                                                       RegulatorsQTYModel regulatorsQTYModel){
        InjectorHighPressureSectionController injectorHighPressureSectionController = crSectionController.getInjectorHighPressureSectionController();
        injectorHighPressureSectionController.setRegulatorsQTYModel(regulatorsQTYModel);
        return injectorHighPressureSectionController;
    }

    @Bean
    @Autowired
    public HighPressureSectionOneController highPressureSectionOneController(InjectorHighPressureSectionController injectorHighPressureSectionController,
                                                                             HighPressureSectionPwrState highPressureSectionPwrState,
                                                                             PressureSensorModel pressureSensorModel,
                                                                             ModbusRegisterProcessor ultimaModbusWriter,
                                                                             HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                                             InjectorTestModel injectorTestModel,
                                                                             PressureRegulatorOneModel pressureRegulatorOneModel,
                                                                             I18N i18N,
                                                                             RegulationModesModel regulationModesModel){
        HighPressureSectionOneController highPressureSectionOneController = injectorHighPressureSectionController.getHighPressureSectionOneController();
        highPressureSectionOneController.setHighPressureSectionPwrState(highPressureSectionPwrState);
        highPressureSectionOneController.setPressureSensorModel(pressureSensorModel);
        highPressureSectionOneController.setUltimaModbusWriter(ultimaModbusWriter);
        highPressureSectionOneController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        highPressureSectionOneController.setInjectorTestModel(injectorTestModel);
        highPressureSectionOneController.setPressureRegulatorOneModel(pressureRegulatorOneModel);
        highPressureSectionOneController.setI18N(i18N);
        highPressureSectionOneController.setRegulationModesModel(regulationModesModel);
        return highPressureSectionOneController;
    }

    @Bean
    @Autowired

    public HighPressureSectionTwoController highPressureSectionTwoController(InjectorHighPressureSectionController injectorHighPressureSectionController,
                                                                             HighPressureSectionPwrState highPressureSectionPwrState,
                                                                             ModbusRegisterProcessor ultimaModbusWriter,
                                                                             HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                                             I18N i18N,
                                                                             RegulationModesModel regulationModesModel){
        HighPressureSectionTwoController highPressureSectionTwoController = injectorHighPressureSectionController.getHighPressureSectionTwoController();
        highPressureSectionTwoController.setHighPressureSectionPwrState(highPressureSectionPwrState);
        highPressureSectionTwoController.setUltimaModbusWriter(ultimaModbusWriter);
        highPressureSectionTwoController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        highPressureSectionTwoController.setI18N(i18N);
        highPressureSectionTwoController.setRegulationModesModel(regulationModesModel);
        return highPressureSectionTwoController;
    }

    @Bean
    @Autowired
    public HighPressureSectionThreeController highPressureSectionThreeController(InjectorHighPressureSectionController injectorHighPressureSectionController,
                                                                                 HighPressureSectionPwrState highPressureSectionPwrState,
                                                                                 ModbusRegisterProcessor ultimaModbusWriter,
                                                                                 HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                                                 I18N i18N,
                                                                                 RegulationModesModel regulationModesModel){
        HighPressureSectionThreeController highPressureSectionThreeController = injectorHighPressureSectionController.getHighPressureSectionThreeController();
        highPressureSectionThreeController.setHighPressureSectionPwrState(highPressureSectionPwrState);
        highPressureSectionThreeController.setUltimaModbusWriter(ultimaModbusWriter);
        highPressureSectionThreeController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        highPressureSectionThreeController.setI18N(i18N);
        highPressureSectionThreeController.setRegulationModesModel(regulationModesModel);
        return highPressureSectionThreeController;
    }

    @Bean
    @Autowired
    public HighPressureSectionLcdController highPressureSectionLcdController(InjectorHighPressureSectionController injectorHighPressureSectionController,
                                                                             HighPressureSectionUpdateModel highPressureSectionUpdateModel){
        HighPressureSectionLcdController highPressureSectionLcdController = injectorHighPressureSectionController.getHighPressureSectionLcdController();
        highPressureSectionLcdController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        return highPressureSectionLcdController;
    }

    @Bean
    @Autowired
    public HighPressureSectionPwrController highPressureSectionPwrController(InjectorHighPressureSectionController injectorHighPressureSectionController,
                                                                             HighPressureSectionPwrState highPressureSectionPwrState){
        HighPressureSectionPwrController highPressureSectionPwrController = injectorHighPressureSectionController.getHighPressureSectionPwrController();
        highPressureSectionPwrController.setHighPressureSectionPwrState(highPressureSectionPwrState);
        return highPressureSectionPwrController;
    }

    @Bean
    @Autowired
    public InjectorSectionController injectorSectionController(@Lazy ModbusRegisterProcessor ultimaModbusWriter,
                                                               TimerTasksManager timerTasksManager,
                                                               DelayController delayController,
                                                               InjConfigurationModel injConfigurationModel,
                                                               InjectorTypeModel injectorTypeModel,
                                                               VoltAmpereProfileDialogModel voltAmpereProfileDialogModel,
                                                               BoostUadjustmentState boostUadjustmentState,
                                                               Devices devices,
                                                               CoilOnePulseParametersModel coilOnePulseParametersModel,
                                                               InjectorSectionUpdateModel injectorSectionUpdateModel,
                                                               CoilTwoPulseParametersModel coilTwoPulseParametersModel,
                                                               InjectorModel injectorModel,
                                                               InjectorTestModel injectorTestModel,
                                                               InjectorControllersState injectorControllersState,
                                                               GUI_TypeModel gui_typeModel,
                                                               InjectorSectionPwrState injectorSectionPwrState) {
        InjectorSectionController injectorSectionController = crSectionController().getInjectorSectionController();
        injectorSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        injectorSectionController.setTimerTasksManager(timerTasksManager);
        injectorSectionController.setDelayController(delayController);
        injectorSectionController.setI18N(i18N);
        injectorSectionController.setInjConfigurationModel(injConfigurationModel);
        injectorSectionController.setInjectorTypeModel(injectorTypeModel);
        injectorSectionController.setVoltAmpereProfileDialogModel(voltAmpereProfileDialogModel);
        injectorSectionController.setBoostUadjustmentState(boostUadjustmentState);
        injectorSectionController.setDevices(devices);
        injectorSectionController.setCoilOnePulseParametersModel(coilOnePulseParametersModel);
        injectorSectionController.setInjectorSectionUpdateModel(injectorSectionUpdateModel);
        injectorSectionController.setCoilTwoPulseParametersModel(coilTwoPulseParametersModel);
        injectorSectionController.setInjectorModel(injectorModel);
        injectorSectionController.setInjectorTestModel(injectorTestModel);
        injectorSectionController.setInjectorControllersState(injectorControllersState);
        injectorSectionController.setGui_typeModel(gui_typeModel);
        injectorSectionController.setInjectorSectionPwrState(injectorSectionPwrState);
        return injectorSectionController;
    }

    @Bean
    public ViewHolder uisSection() {
        return loadView("/fxml/sections/UIS/UISSection.fxml");
    }

    @Bean
    public ViewHolder tabSection() {
        return loadView("/fxml/sections/Additional/TabSection.fxml");
    }

    @Bean
    public ViewHolder isaDetection() {
        return loadView("/fxml/ISADetection.fxml");
    }

    @Bean
    @Autowired
    public ISADetectionController isaDetectionController(@Lazy ViewHolder rootLayout, InjectorSectionController injectorSectionController,
                                                         @Lazy Measurements measurements, MainSectionController mainSectionController,
                                                         ISADetectionRepository isaDetectionRepository,
                                                         VoltAmpereProfileController voltAmpereProfileController,
                                                         FlowController flowController,
                                                         HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                         PressureRegulatorOneModel pressureRegulatorOneModel,
                                                         InjectorTestModel injectorTestModel,
                                                         InjectorControllersState injectorControllersState) {
        ISADetectionController isaDetectionController = (ISADetectionController) isaDetection().getController();
        isaDetectionController.setISAParent(isaDetection().getView());
        isaDetectionController.setRootParent(rootLayout.getView());
        isaDetectionController.setInjectorSectionController(injectorSectionController);
        isaDetectionController.setMeasurements(measurements);
        isaDetectionController.setMainSectionStartToggleButton(mainSectionController.getStartToggleButton());
        isaDetectionController.setResetButton(mainSectionController.getResetButton());
        isaDetectionController.setISADetectionRepository(isaDetectionRepository);
        isaDetectionController.setBoostUSpinner(voltAmpereProfileController.getBoostUSpinner());
        isaDetectionController.setVoltAmpereProfileApplyButton(voltAmpereProfileController.getApplyButton());
        isaDetectionController.setDelivery1TextField(flowController.getDelivery1TextField());
        isaDetectionController.setDelivery2TextField(flowController.getDelivery2TextField());
        isaDetectionController.setDelivery3TextField(flowController.getDelivery3TextField());
        isaDetectionController.setDelivery4TextField(flowController.getDelivery4TextField());
        isaDetectionController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        isaDetectionController.setPressureRegulatorOneModel(pressureRegulatorOneModel);
        isaDetectionController.setInjectorTestModel(injectorTestModel);
        isaDetectionController.setInjectorControllersState(injectorControllersState);
        return isaDetectionController;
    }

    @Bean
    public ViewHolder infoDefault(){
        return loadView("/fxml/sections/Additional/tabs/info/DefaultOEM.fxml");
    }

    @Bean ViewHolder infoBosch(){
        return loadView("/fxml/sections/Additional/tabs/info/Bosch.fxml");
    }

    @Bean ViewHolder infoSiemens(){
        return loadView("/fxml/sections/Additional/tabs/info/Siemens.fxml");
    }

    @Bean ViewHolder infoDenso(){
        return loadView("/fxml/sections/Additional/tabs/info/Denso.fxml");
    }

    @Bean ViewHolder infoCaterpillar(){
        return loadView("/fxml/sections/Additional/tabs/info/Caterpillar.fxml");
    }

    @Bean ViewHolder infoAZPI(){
        return loadView("/fxml/sections/Additional/tabs/info/AZPI.fxml");
    }

    @Bean ViewHolder infoDelphi(){
        return loadView("/fxml/sections/Additional/tabs/info/Delphi.fxml");
    }

    @Bean
    public TabSectionController tabSectionController() {
        TabSectionController tabSectionController = (TabSectionController) tabSection().getController();
        tabSectionController.setI18N(i18N);
        return tabSectionController;
    }

    @Bean
    @Autowired
    public CodingController codingController(TabSectionController tabSectionController,
                                             CodingReportModel codingReportModel){
        CodingController codingController = tabSectionController.getCodingController();
        codingController.setI18N(i18N);
        codingController.setCodingReportModel(codingReportModel);
        return codingController;
    }

    @Bean
    @Autowired
    public FlowController flowController(TabSectionController tabSectionController,
                                         FlowValuesModel flowValuesModel,
                                         DeliveryFlowUnitsModel deliveryFlowUnitsModel,
                                         DeliveryFlowRangeModel deliveryFlowRangeModel,
                                         BackFlowUnitsModel backFlowUnitsModel,
                                         BackFlowRangeModel backFlowRangeModel,
                                         MainSectionModel mainSectionModel) {
        FlowController flowController = tabSectionController.getFlowController();
        flowController.setI18N(i18N);
        flowController.setFlowValuesModel(flowValuesModel);
        flowController.setDeliveryFlowUnitsModel(deliveryFlowUnitsModel);
        flowController.setDeliveryFlowRangeModel(deliveryFlowRangeModel);
        flowController.setBackFlowUnitsModel(backFlowUnitsModel);
        flowController.setBackFlowRangeModel(backFlowRangeModel);
        flowController.setMainSectionModel(mainSectionModel);
        return flowController;
    }

    @Bean
    @Autowired
    public ReportController reportController(TabSectionController tabSectionController,
                                             I18N i18N) {
        ReportController reportController = tabSectionController.getReportController();
        reportController.setI18N(i18N);
        return reportController;
    }

    @Bean
    @Autowired
    public FlowReportController flowReportController(ReportController reportController,
                                                     I18N i18N,
                                                     FlowReportModel flowReportModel,
                                                     MainSectionModel mainSectionModel) {
        FlowReportController flowReportController = reportController.getFlowReportController();
        flowReportController.setI18N(i18N);
        flowReportController.setFlowReportModel(flowReportModel);
        flowReportController.setMainSectionModel(mainSectionModel);
        return flowReportController;
    }

    @Bean
    @Autowired
    public RLC_ReportController rlcReportController(ReportController reportController,
                                                    I18N i18N,
                                                    RLC_ReportModel rlc_reportModel){
        RLC_ReportController rlcReportController = reportController.getRLCreportController();
        rlcReportController.setI18N(i18N);
        rlcReportController.setRlc_reportModel(rlc_reportModel);
        return rlcReportController;
    }

    @Bean
    @Autowired
    public DelayReportController delayReportController(ReportController reportController,
                                                       I18N i18N,
                                                       DelayReportModel delayReportModel){
        DelayReportController delayReportController = reportController.getDelayReportController();
        delayReportController.setI18N(i18N);
        delayReportController.setDelayReportModel(delayReportModel);
        return delayReportController;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery1Controller(FlowController flowController,
                                                      Rescaler deliveryRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerDelivery1Controller = flowController.getBeakerDelivery1Controller();
        setupBeakerController(beakerDelivery1Controller, flowController, flowController.getDelivery1TextField(),
                injectorControllersState.getLedBeaker1ToggleButton(), deliveryRescaler, "Delivery1", BeakerType.DELIVERY);
        return beakerDelivery1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery2Controller(FlowController flowController,
                                                      Rescaler deliveryRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerDelivery2Controller = flowController.getBeakerDelivery2Controller();
        setupBeakerController(beakerDelivery2Controller, flowController, flowController.getDelivery2TextField(),
                injectorControllersState.getLedBeaker2ToggleButton(), deliveryRescaler, "Delivery2", BeakerType.DELIVERY);
        return beakerDelivery2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery3Controller(FlowController flowController,
                                                      Rescaler deliveryRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerDelivery3Controller = flowController.getBeakerDelivery3Controller();
        setupBeakerController(beakerDelivery3Controller, flowController, flowController.getDelivery3TextField(),
                injectorControllersState.getLedBeaker3ToggleButton(), deliveryRescaler, "Delivery3", BeakerType.DELIVERY);
        return beakerDelivery3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery4Controller(FlowController flowController,
                                                      Rescaler deliveryRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerDelivery4Controller = flowController.getBeakerDelivery4Controller();
        setupBeakerController(beakerDelivery4Controller, flowController, flowController.getDelivery4TextField(),
                injectorControllersState.getLedBeaker4ToggleButton(), deliveryRescaler, "Delivery4", BeakerType.DELIVERY);
        return beakerDelivery4Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow1Controller(FlowController flowController,
                                                      Rescaler backFlowRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerBackFlow1Controller = flowController.getBeakerBackFlow1Controller();
        setupBeakerController(beakerBackFlow1Controller, flowController, flowController.getBackFlow1TextField(),
                injectorControllersState.getLedBeaker1ToggleButton(), backFlowRescaler, "Backflow1", BeakerType.BACKFLOW);
        return beakerBackFlow1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow2Controller(FlowController flowController,
                                                      Rescaler backFlowRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerBackFlow2Controller = flowController.getBeakerBackFlow2Controller();
        setupBeakerController(beakerBackFlow2Controller, flowController, flowController.getBackFlow2TextField(),
                injectorControllersState.getLedBeaker2ToggleButton(), backFlowRescaler, "Backflow2", BeakerType.BACKFLOW);
        return beakerBackFlow2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow3Controller(FlowController flowController,
                                                      Rescaler backFlowRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerBackFlow3Controller = flowController.getBeakerBackFlow3Controller();
        setupBeakerController(beakerBackFlow3Controller, flowController, flowController.getBackFlow3TextField(),
                injectorControllersState.getLedBeaker3ToggleButton(), backFlowRescaler, "Backflow3", BeakerType.BACKFLOW);
        return beakerBackFlow3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow4Controller(FlowController flowController,
                                                      Rescaler backFlowRescaler,
                                                      InjectorControllersState injectorControllersState) {
        BeakerController beakerBackFlow4Controller = flowController.getBeakerBackFlow4Controller();
        setupBeakerController(beakerBackFlow4Controller, flowController, flowController.getBackFlow4TextField(),
                injectorControllersState.getLedBeaker4ToggleButton(), backFlowRescaler, "Backflow4", BeakerType.BACKFLOW);
        return beakerBackFlow4Controller;
    }

    private void setupBeakerController(BeakerController beakerController, FlowController flowController, TextField textField,
                                       ToggleButton ledToggleButton, Rescaler rescaler, String name, BeakerType beakerType) {

        beakerController.setFlowController(flowController);
        beakerController.setTextField(textField);
        beakerController.setDeliveryRangeLabel(flowController.getDeliveryRangeLabel());
        beakerController.setBackFlowRangeLabel(flowController.getBackFlowRangeLabel());
        beakerController.setLedButton(ledToggleButton);
        beakerController.setRescaler(rescaler);
        beakerController.setName(name);
        beakerController.setBeakerType(beakerType);

    }

    @Bean
    @Autowired
    public VoltageController voltageController(TabSectionController tabSectionController,
                                               InjectorTypeModel injectorTypeModel,
                                               CoilOnePulseParametersModel coilOnePulseParametersModel,
                                               InjectorSectionUpdateModel injectorSectionUpdateModel,
                                               VoltAmpereProfileDialogModel voltAmpereProfileDialogModel,
                                               InjectorTestModel injectorTestModel,
                                               InjectorModel injectorModel,
                                               CoilTwoPulseParametersModel coilTwoPulseParametersModel,
                                               InjectorSectionPwrState injectorSectionPwrState,
                                               VoltageTabModel voltageTabModel) {
        VoltageController voltageController = tabSectionController.getVoltageController();
        voltageController.setParentController(tabSectionController);
        voltageController.setI18N(i18N);
        voltageController.setInjectorTypeModel(injectorTypeModel);
        voltageController.setCoilOnePulseParametersModel(coilOnePulseParametersModel);
        voltageController.setInjectorSectionUpdateModel(injectorSectionUpdateModel);
        voltageController.setVoltAmpereProfileDialogModel(voltAmpereProfileDialogModel);
        voltageController.setInjectorTestModel(injectorTestModel);
        voltageController.setInjectorModel(injectorModel);
        voltageController.setCoilTwoPulseParametersModel(coilTwoPulseParametersModel);
        voltageController.setInjectorSectionPwrState(injectorSectionPwrState);
        voltageController.setVoltageTabModel(voltageTabModel);
        return voltageController;
    }

    @Bean
    @Autowired
    public DelayController delayController(TabSectionController tabSectionController,
                                           DelayCalculator delayCalculator,
                                           DelayReportModel delayReportModel,
                                           InjectorTestModel injectorTestModel) {
        DelayController delayController = tabSectionController.getDelayController();
        delayController.setDelayCalculator(delayCalculator);
        delayController.setTabSectionController(tabSectionController);
        delayController.setI18N(i18N);
        delayController.setDelayReportModel(delayReportModel);
        delayController.setInjectorTestModel(injectorTestModel);
         return delayController;
    }

    @Bean
    public ViewHolder connection(){
        return loadView("/fxml/sections/Additional/tabs/settings/Connection.fxml");
    }


    @Bean
    @Autowired
    public ConnectionController connectionController(Preferences rootPrefs,
                                                     ViewHolder connection){
        ConnectionController connectionController = (ConnectionController)connection.getController();
        connectionController.setI18N(i18N);
        connectionController.setRootPrefs(rootPrefs);
        return connectionController;
    }



    @Bean
    @Autowired
    SettingsController settingsController(ViewHolder settings){
        SettingsController settingsController = (SettingsController)settings.getController();
        settingsController.setI18N(i18N);
        return settingsController;
    }

    @Bean(value = "voltAmpereProfileDialog")
    public ViewHolder voltAmpereProfileDialog() {
        return loadView("/fxml/sections/Additional/dialogs/voltAmpereProfileDialog.fxml");
    }

    @Bean
    @Autowired
    public VoltAmpereProfileController voltAmpereProfileController(ModbusRegisterProcessor ultimaModbusWriter,
                                                                   VoltAmpereProfileDialogModel voltAmpereProfileDialogModel,
                                                                   CoilTwoPulseParametersModel coilTwoPulseParametersModel,
                                                                   CoilOnePulseParametersModel coilOnePulseParametersModel,
                                                                   InjectorTestModel injectorTestModel,
                                                                   InjectorSectionUpdateModel injectorSectionUpdateModel,
                                                                   InjectorModel injectorModel,
                                                                   InjectorTypeModel injectorTypeModel,
                                                                   VoltageTabModel voltageTabModel) {
        VoltAmpereProfileController voltAmpereProfileController = (VoltAmpereProfileController) voltAmpereProfileDialog().getController();
        voltAmpereProfileController.setUltimaModbusWriter(ultimaModbusWriter);
        voltAmpereProfileController.setI18N(i18N);
        voltAmpereProfileController.setVoltAmpereProfileDialogModel(voltAmpereProfileDialogModel);
        voltAmpereProfileController.setCoilTwoPulseParametersModel(coilTwoPulseParametersModel);
        voltAmpereProfileController.setCoilOnePulseParametersModel(coilOnePulseParametersModel);
        voltAmpereProfileController.setInjectorTestModel(injectorTestModel);
        voltAmpereProfileController.setInjectorSectionUpdateModel(injectorSectionUpdateModel);
        voltAmpereProfileController.setInjectorModel(injectorModel);
        voltAmpereProfileController.setInjectorTypeModel(injectorTypeModel);
        voltAmpereProfileController.setVapDialogView(voltAmpereProfileDialog().getView());
        voltAmpereProfileController.setVoltageTabModel(voltageTabModel);
        return voltAmpereProfileController;
    }

    @Bean
    @Autowired
    public BoschController boschController(ViewHolder infoBosch,
                                           BoschRepository boschRepository,
                                           MainSectionModel mainSectionModel){
        BoschController boschController = (BoschController) infoBosch.getController();
        boschController.setBoschRepository(boschRepository);
        boschController.setMainSectionModel(mainSectionModel);
        return boschController;
    }

    @Bean
    @Autowired
    public SiemensController siemensController(ViewHolder infoSiemens,
                                               SiemensReferenceRepository siemensReferenceRepository,
                                               MainSectionModel mainSectionModel){
        SiemensController siemensController = (SiemensController)infoSiemens.getController();
        siemensController.setSiemensReferenceRepository(siemensReferenceRepository);
        siemensController.setMainSectionModel(mainSectionModel);
        return siemensController;
    }

    @Bean
    @Autowired
    public DensoController densoController(ViewHolder infoDenso,
                                           DensoRepository densoRepository,
                                           MainSectionModel mainSectionModel){
        DensoController densoController = (DensoController)infoDenso.getController();
        densoController.setDensoRepository(densoRepository);
        densoController.setMainSectionModel(mainSectionModel);
        return densoController;
    }

    @Bean
    @Autowired
    public DelphiController delphiController(ViewHolder infoDelphi,
                                             DelphiRepository delphiRepository,
                                             MainSectionModel mainSectionModel){
        DelphiController delphiController = (DelphiController)infoDelphi.getController();
        delphiController.setDelphiRepository(delphiRepository);
        delphiController.setMainSectionModel(mainSectionModel);
        return delphiController;
    }

    @Bean
    @Autowired
    public CaterpillarController caterpillarController(ViewHolder infoCaterpillar,
                                                       CaterpillarRepository caterpillarRepository,
                                                       MainSectionModel mainSectionModel){
        CaterpillarController caterpillarController = (CaterpillarController)infoCaterpillar.getController();
        caterpillarController.setCaterpillarRepository(caterpillarRepository);
        caterpillarController.setMainSectionModel(mainSectionModel);
        return caterpillarController;
    }

    @Bean
    @Autowired
    public AZPIController azpiController(ViewHolder infoAZPI,
                                         AZPIRepository azpiRepository,
                                         MainSectionModel mainSectionModel){
        AZPIController azpiController = (AZPIController) infoAZPI.getController();
        azpiController.setAZPIRepository(azpiRepository);
        azpiController.setMainSectionModel(mainSectionModel);
        return azpiController;
    }

    @Bean
    @Autowired
    public InfoController infoController(ViewHolder infoDefault,
                                         ViewHolder infoBosch,
                                         ViewHolder infoSiemens,
                                         ViewHolder infoDenso,
                                         ViewHolder infoCaterpillar,
                                         ViewHolder infoAZPI,
                                         ViewHolder infoDelphi,
                                         TabSectionController tabSectionController,
                                         MainSectionModel mainSectionModel){
        InfoController infoController = tabSectionController.getInfoController();
        infoController.setInfoDefault(infoDefault.getView());
        infoController.setInfoBosch(infoBosch.getView());
        infoController.setInfoSiemens(infoSiemens.getView());
        infoController.setInfoDenso(infoDenso.getView());
        infoController.setInfoCaterpillar(infoCaterpillar.getView());
        infoController.setInfoAZPI(infoAZPI.getView());
        infoController.setInfoDelphi(infoDelphi.getView());
        infoController.setBoschController((BoschController) infoBosch.getController());
        infoController.setSiemensController((SiemensController)infoSiemens.getController());
        infoController.setDelphiController((DelphiController)infoDelphi.getController());
        infoController.setDensoController((DensoController)infoDenso.getController());
        infoController.setCaterpillarController((CaterpillarController)infoCaterpillar.getController());
        infoController.setAzpiController((AZPIController)infoAZPI.getController());
        infoController.setMainSectionModel(mainSectionModel);
        return infoController;
    }

    @Bean
    @Autowired

    public StatusBarWrapper statusBar(Devices devices, FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                      FirmwareVersion<StandVersions> standFirmwareVersion) {
        return new StatusBarWrapper(devices, "Ready", "Device not connected",
                softVersionNum, flowFirmwareVersion, standFirmwareVersion);
    }

    @Bean
    public ViewHolder manufacturerMenuDialog() {
        return loadView("/fxml/dialogs/ManufacturerMenuDialog.fxml");
    }

    @Bean
    @Autowired
    public ManufacturerMenuDialogController manufacturerMenuDialogController(ManufacturerRepository manufacturerRepository,
                                                                             GUI_TypeModel gui_typeModel,
                                                                             ManufacturerMenuDialogModel manufacturerMenuDialogModel,
                                                                             MainSectionModel mainSectionModel,
                                                                             ViewHolder manufacturerMenuDialog) {
        ManufacturerMenuDialogController manufacturerMenuDialogController = (ManufacturerMenuDialogController) manufacturerMenuDialog().getController();
        manufacturerMenuDialogController.setManufacturerRepository(manufacturerRepository);
        manufacturerMenuDialogController.setGui_typeModel(gui_typeModel);
        manufacturerMenuDialogController.setManufacturerMenuDialogModel(manufacturerMenuDialogModel);
        manufacturerMenuDialogController.setMainSectionModel(mainSectionModel);
        manufacturerMenuDialogController.setManufacturerMenuDialog(manufacturerMenuDialog);
        return manufacturerMenuDialogController;
    }

    @Bean
    public ViewHolder newEditVOAPDialog() {
        return loadView("/fxml/dialogs/NewEditVOAPDialog.fxml");
    }



    @Bean
    @Autowired
    public NewEditVOAPDialogController newEditVOAPDialogController(VoltAmpereProfileRepository voltAmpereProfileRepository) {
        NewEditVOAPDialogController newEditVOAPDialogController = (NewEditVOAPDialogController) newEditVOAPDialog().getController();
        newEditVOAPDialogController.setVoltAmpereProfileRepository(voltAmpereProfileRepository);
        return newEditVOAPDialogController;
    }

    @Bean
    public ViewHolder printDialogPanel(){ return loadView("/fxml/dialogs/PrintDialogPanel.fxml"); }

    @Bean
    public ViewHolder firmwareDialog(){return loadView("/fxml/dialogs/FirmwareDialog.fxml");}

    @Bean
    @Autowired
    public PrintDialogPanelController newPrintDialogPanelController(PDFService pdfService,
                                                                    I18N i18N,
                                                                    GUI_TypeModel gui_typeModel,
                                                                    PumpModel pumpModel,
                                                                    MainSectionModel mainSectionModel){
        PrintDialogPanelController printDialogPanelController = (PrintDialogPanelController) printDialogPanel().getController();
        printDialogPanelController.setPdfService(pdfService);
        printDialogPanelController.setI18N(i18N);
        printDialogPanelController.setGui_typeModel(gui_typeModel);
        printDialogPanelController.setPumpModel(pumpModel);
        printDialogPanelController.setMainSectionModel(mainSectionModel);
        return printDialogPanelController;
    }

    @Bean
    public ViewHolder newEditInjectorDialog() {
        return loadView("/fxml/dialogs/NewEditInjectorDialog.fxml");
    }

    @Bean
    @Autowired
    public NewEditInjectorDialogController newEditInjectorDialogController(InjectorTypeRepository injectorTypeRepository,
                                                                           InjectorTestRepository injectorTestRepository,
                                                                           VoltAmpereProfileRepository voltAmpereProfileRepository,
                                                                           InjectorsRepository injectorsRepository,
                                                                           GUI_TypeModel gui_typeModel,
                                                                           NewEditInjectorDialogModel newEditInjectorDialogModel) {
        NewEditInjectorDialogController newEditInjectorDialogController = (NewEditInjectorDialogController) newEditInjectorDialog().getController();
        newEditInjectorDialogController.setInjectorTypeRepository(injectorTypeRepository);
        newEditInjectorDialogController.setInjectorTestRepository(injectorTestRepository);
        newEditInjectorDialogController.setVoltAmpereProfileRepository(voltAmpereProfileRepository);
        newEditInjectorDialogController.setInjectorsRepository(injectorsRepository);
        newEditInjectorDialogController.setNewEditVOAPDialog(newEditVOAPDialog());
        newEditInjectorDialogController.setGui_typeModel(gui_typeModel);
        newEditInjectorDialogController.setNewEditInjectorDialogModel(newEditInjectorDialogModel);
        return newEditInjectorDialogController;
    }

    @Bean
    public ViewHolder newEditTestDialog() {
        return loadView("/fxml/dialogs/NewEditTestDialog.fxml");
    }

    @Bean
    @Autowired
    public NewEditTestDialogController newEditTestDialogController(InjectorTestRepository injectorTestRepository,
                                                                   TestNamesRepository testNamesRepositor) {
        NewEditTestDialogController newEditTestDialogController = (NewEditTestDialogController) newEditTestDialog().getController();
        newEditTestDialogController.setInjectorTestRepository(injectorTestRepository);
        newEditTestDialogController.setTestNamesRepository(testNamesRepositor);
        return newEditTestDialogController;
    }

    @Bean
    @Autowired
    public RLCController rlcController(ModbusRegisterProcessor ultimaModbusWriter,
                                       RegisterProvider ultimaRegisterProvider,
                                       TabSectionController tabSectionController,
                                       InjConfigurationModel injConfigurationModel,
                                       InjectorTypeModel injectorTypeModel,
                                       RLC_ReportModel rlc_reportModel,
                                       InjectorControllersState injectorControllersState,
                                       InjectorModel injectorModel) {
        RLCController RLCController = tabSectionController.getRlCController();
        RLCController.setUltimaModbusWriter(ultimaModbusWriter);
        RLCController.setUltimaRegisterProvider(ultimaRegisterProvider);
        RLCController.setI18N(i18N);
        RLCController.setInjConfigurationModel(injConfigurationModel);
        RLCController.setInjectorTypeModel(injectorTypeModel);
        RLCController.setRlc_reportModel(rlc_reportModel);
        RLCController.setInjectorControllersState(injectorControllersState);
        RLCController.setInjectorModel(injectorModel);
        return RLCController;

    }

    @Bean
    @Autowired
    public PiezoRepairController piezoRepairController(TabSectionController tabSectionController,
                                                       PiezoRepairModel piezoRepairModel,
                                                       ModbusRegisterProcessor ultimaModbusWriter,
                                                       PiezoRepairUpdateModel piezoRepairUpdateModel){
        PiezoRepairController piezoRepairController = tabSectionController.getPiezoRepairController();
        piezoRepairController.setPiezoRepairModel(piezoRepairModel);
        piezoRepairController.setUltimaModbusWriter(ultimaModbusWriter);
        piezoRepairController.setPiezoRepairUpdateModel(piezoRepairUpdateModel);
        return piezoRepairController;
    }

    @Bean
    public ViewHolder dimasGuiEdition(){
            return loadView("/fxml/sections/Additional/tabs/settings/DimasGuiEdition.fxml");
    }

    @Bean
    public ViewHolder fastCoding(){
        return loadView("/fxml/sections/Additional/tabs/settings/FastCoding.fxml");
    }

    @Bean
    public ViewHolder flowView(){
        return loadView("/fxml/sections/Additional/tabs/settings/FlowView.fxml");
    }

    @Bean
    public ViewHolder injConfiguration(){
        return loadView("/fxml/sections/Additional/tabs/settings/InjConfiguration.fxml");
    }

    @Bean
    public ViewHolder instantFlow(){
        return loadView("/fxml/sections/Additional/tabs/settings/InstantFlow.fxml");
    }

    @Bean
    public ViewHolder language(){
        return loadView("/fxml/sections/Additional/tabs/settings/Language.fxml");
    }

    @Bean
    public ViewHolder pressureSensor(){
        return loadView("/fxml/sections/Additional/tabs/settings/PressureSensor.fxml");
    }

    @Bean
    public ViewHolder regulatorsQTY(){
        return loadView("/fxml/sections/Additional/tabs/settings/RegulatorsQTY.fxml");
    }

    @Bean
    public ViewHolder settings(){
        return loadView("/fxml/sections/Additional/tabs/settings/Settings.fxml");
    }

    @Bean
    @Autowired
    public DimasGuiEditionController dimasGuiEditionController(DimasGUIEditionState dimasGUIEditionState,
                                                               Preferences preferences,
                                                               SettingsController settingsController){
        DimasGuiEditionController dimasGuiEditionController = settingsController.getDimasGuiEditionController();
        dimasGuiEditionController.setDimasGUIEditionState(dimasGUIEditionState);
        dimasGuiEditionController.setI18N(i18N);
        dimasGuiEditionController.setRootPrefs(preferences);
        return dimasGuiEditionController;
    }

    @Bean
    @Autowired
    public FastCodingController fastCodingController(FastCodingState fastCodingState,
                                                     Preferences preferences,
                                                     SettingsController settingsController){
        FastCodingController fastCodingController = settingsController.getFastCodingController();
        fastCodingController.setFastCodingState(fastCodingState);
        fastCodingController.setI18N(i18N);
        fastCodingController.setRootPrefs(preferences);
        return fastCodingController;
    }

    @Bean
    @Autowired
    public InstantFlowController instantFlowController(InstantFlowState instantFlowState,
                                                       Preferences preferences,
                                                       SettingsController settingsController){
        InstantFlowController instantFlowController = settingsController.getInstantFlowController();
        instantFlowController.setInstantFlowState(instantFlowState);
        instantFlowController.setI18N(i18N);
        instantFlowController.setRootPrefs(preferences);
        return instantFlowController;
    }

    @Bean
    @Autowired
    public FlowViewController flowViewController(FlowViewModel flowViewModel,
                                                 Preferences preferences,
                                                 SettingsController settingsController){
        FlowViewController flowViewController = settingsController.getFlowViewController();
        flowViewController.setFlowViewModel(flowViewModel);
        flowViewController.setRootPrefs(preferences);
        return flowViewController;
    }

    @Bean
    @Autowired
    public InjConfigurationController injConfigurationController(InjConfigurationModel injConfigurationModel,
                                                                 Preferences preferences,
                                                                 InjectorTypeModel injectorTypeModel,
                                                                 SettingsController settingsController,
                                                                 VoltAmpereProfileDialogModel voltAmpereProfileDialogModel){
        InjConfigurationController injConfigurationController = settingsController.getInjConfigurationController();
        injConfigurationController.setInjConfigurationModel(injConfigurationModel);
        injConfigurationController.setRootPrefs(preferences);
        injConfigurationController.setInjectorTypeModel(injectorTypeModel);
        injConfigurationController.setVoltAmpereProfileDialogModel(voltAmpereProfileDialogModel);
        return injConfigurationController;
    }

    @Bean
    @Autowired
    public LanguageController languageController(LanguageModel languageModel,
                                                 Preferences preferences,
                                                 SettingsController settingsController){
        LanguageController languageController = settingsController.getLanguageController();
        languageController.setLanguageModel(languageModel);
        languageController.setRootPrefs(preferences);
        languageController.setI18N(i18N);
        return languageController;
    }

    @Bean
    @Autowired
    public PressureSensorController pressureSensorController(PressureSensorModel pressureSensorModel,
                                                             Preferences preferences,
                                                             SettingsController settingsController){
        PressureSensorController pressureSensorController = settingsController.getPressureSensorController();
        pressureSensorController.setPressureSensorModel(pressureSensorModel);
        pressureSensorController.setRootPrefs(preferences);
        pressureSensorController.setI18N(i18N);
        return pressureSensorController;
    }

    @Bean
    @Autowired
    public RegulatorsQTYController regulatorsQTYController(SettingsController settingsController,
                                                           RegulatorsQTYModel regulatorsQTYModel,
                                                           Preferences preferences){
        RegulatorsQTYController regulatorsQTYController = settingsController.getRegulatorsQTYController();
        regulatorsQTYController.setPressureSensorState(regulatorsQTYModel);
        regulatorsQTYController.setRootPreferences(preferences);
        return regulatorsQTYController;
    }

    @Bean
    @Autowired
    public FirmwareButtonController firmwareController(SettingsController settingsController,
                                                       I18N i18N){
        FirmwareButtonController firmwareButtonController = settingsController.getFirmwareButtonController();
        firmwareButtonController.setI18N(i18N);
        firmwareButtonController.setFirmwareWindow(firmwareDialog());
        return firmwareButtonController;
    }


    @Bean
    @Autowired
    public FirmwareDialogController firmwareDialogController(I18N i18N,
                                                             RegisterProvider ultimaRegisterProvider,
                                                             ModbusConnect ultimaModbusConnect,
                                                             ModbusConnect flowModbusConnect,
                                                             ModbusConnect standModbusConnect,
                                                             ViewHolder firmwareDialog){
        FirmwareDialogController firmwareDialogController = (FirmwareDialogController)firmwareDialog.getController();
        firmwareDialogController.setUltimaRegisterProvider(ultimaRegisterProvider);
        firmwareDialogController.setUltimaModbusConnect(ultimaModbusConnect);
        firmwareDialogController.setFlowModbusConnect(flowModbusConnect);
        firmwareDialogController.setStandModbusConnect(standModbusConnect);
        firmwareDialogController.setFirmwareDialog(firmwareDialog);
        firmwareDialogController.setI18N(i18N);
        return firmwareDialogController;
    }

    @Bean
    public ViewHolder scvCalibration() {
        return loadView("/fxml/pumps/SCVCalibration.fxml");
    }

    @Bean
    @Autowired
    public SCVCalibrationController scvCalibrationController(ViewHolder scvCalibration,
                                                             @Lazy ViewHolder rootLayout,
                                                             PumpTestModel pumpTestModel,
                                                             ModbusRegisterProcessor flowModbusWriter,
                                                             HighPressureSectionUpdateModel highPressureSectionUpdateModel,
                                                             PumpPressureRegulatorOneModel pumpPressureRegulatorOneModel,
                                                             PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController,
                                                             SCVCalibrationModel scvCalibrationModel,
                                                             PumpModel pumpModel,
                                                             PumpReportModel pumpReportModel,
                                                             PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController,
                                                             TestBenchSectionController testBenchSectionController,
                                                             TestBenchSectionModel testBenchSectionModel) {
        SCVCalibrationController scvCalibrationController = (SCVCalibrationController)scvCalibration.getController();
        scvCalibrationController.setScvParent(scvCalibration.getView());
        scvCalibrationController.setRootParent(rootLayout.getView());
        scvCalibrationController.setPumpTestModel(pumpTestModel);
        scvCalibrationController.setFlowModbusWriter(flowModbusWriter);
        scvCalibrationController.setHighPressureSectionUpdateModel(highPressureSectionUpdateModel);
        scvCalibrationController.setPumpPressureRegulatorOneModel(pumpPressureRegulatorOneModel);
        scvCalibrationController.setPumpRegulatorSectionTwoController(pumpRegulatorSectionTwoController);
        scvCalibrationController.setScvCalibrationModel(scvCalibrationModel);
        scvCalibrationController.setPumpModel(pumpModel);
        scvCalibrationController.setPumpReportModel(pumpReportModel);
        scvCalibrationController.setPumpHighPressureSectionPwrController(pumpHighPressureSectionPwrController);
        scvCalibrationController.setTestBenchSectionController(testBenchSectionController);
        scvCalibrationController.setI18N(i18N);
        scvCalibrationController.setTestBenchSectionModel(testBenchSectionModel);
        return scvCalibrationController;
    }

    @Bean
    public ViewHolder calibrationTestError() {
        return loadView("/fxml/pumps/CalibrationTestError.fxml");
    }

    @Bean
    @Autowired
    public CalibrationTestErrorController calibrationTestErrorController(ViewHolder calibrationTestError,
                                                                         @Lazy ViewHolder rootLayout) {
        CalibrationTestErrorController calibrationTestErrorController = (CalibrationTestErrorController)calibrationTestError.getController();
        calibrationTestErrorController.setI18N(i18N);
        calibrationTestErrorController.setErrorParent(calibrationTestError.getView());
        calibrationTestErrorController.setRootParent(rootLayout.getView());
        return calibrationTestErrorController;
    }
}
