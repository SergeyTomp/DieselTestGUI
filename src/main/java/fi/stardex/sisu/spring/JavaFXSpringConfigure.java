package fi.stardex.sisu.spring;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.measurement.Measurements;
import fi.stardex.sisu.model.CodingReportModel;
import fi.stardex.sisu.model.DelayReportModel;
import fi.stardex.sisu.model.ManufacturerPumpModel;
import fi.stardex.sisu.model.RLC_ReportModel;
import fi.stardex.sisu.pdf.PDFService;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.repos.ISADetectionRepository;
import fi.stardex.sisu.persistence.repos.InjectorTypeRepository;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.*;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.*;
import fi.stardex.sisu.store.FlowReport;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.GUI_TypeController;
import fi.stardex.sisu.ui.controllers.ISADetectionController;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import fi.stardex.sisu.ui.controllers.additional.TabSectionController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.additional.tabs.*;
import fi.stardex.sisu.ui.controllers.additional.tabs.info.*;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.DelayReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.FlowReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.RLC_ReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.report.ReportController;
import fi.stardex.sisu.ui.controllers.additional.tabs.settings.ConnectionController;
import fi.stardex.sisu.ui.controllers.additional.tabs.settings.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.CRSectionController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.dialogs.*;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.ui.controllers.pumps.PumpTabSectionController;
import fi.stardex.sisu.settings.*;
import fi.stardex.sisu.ui.controllers.uis.RootLayoutController;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.StardexVersion;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import java.util.prefs.Preferences;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;

@Configuration
@Import(JavaFXSpringConfigurePumps.class)
@ComponentScan(value = "fi.stardex.sisu")
public class JavaFXSpringConfigure extends ViewLoader{

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

    // Depends on нужен для инициализации листенера в бине pumpsOEMListController
    @Bean
    @Autowired
    @DependsOn("pumpsOEMListController")
    public GUI_TypeController gui_typeController(Preferences rootPrefs,
                                                 RootLayoutController rootLayoutController,
                                                 TabSectionController tabSectionController,
                                                 PumpTabSectionController pumpTabSectionController,
                                                 DimasGUIEditionState dimasGUIEditionState,
                                                 ViewHolder mainSectionPumps,
                                                 ViewHolder pumpHighPressureSection,
                                                 ViewHolder pumpTabSection,
                                                 ViewHolder settings,
                                                 ViewHolder connection,
                                                 ManufacturerPumpModel manufacturerPumpModel) {
        GUI_TypeController gui_typeController = rootLayoutController.getGui_typeController();
        gui_typeController.setRootPrefs(rootPrefs);
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
        return gui_typeController;
    }

    @Bean
    public ViewHolder mainSection() {
        return loadView("/fxml/sections/Main/MainSection.fxml");
    }

    @Bean
    @Autowired
    public MainSectionController mainSectionController(@Lazy Enabler enabler,
                                                       VoltAmpereProfileController voltAmpereProfileController,
                                                       InjectorSectionController injectorSectionController,
                                                       InjectorsRepository injectorsRepository,
                                                       InjectorTestRepository injectorTestRepository,
                                                       @Lazy ModbusRegisterProcessor flowModbusWriter,
                                                       HighPressureSectionController highPressureSectionController,
                                                       @Lazy Measurements measurements,
                                                       @Lazy FlowReport flowReport,
                                                       TestBenchSectionController testBenchSectionController,
                                                       InfoController infoController,
                                                       DelayController delayController,
                                                       BoostU_State boostU_state,
                                                       BoostUadjustmentState boostUadjustmentState,
                                                       CodingReportModel codingReportModel,
                                                       DelayReportModel delayReportModel,
                                                       RLC_ReportModel rlc_reportModel) {
        MainSectionController mainSectionController = (MainSectionController) mainSection().getController();
        mainSectionController.setEnabler(enabler);
        mainSectionController.setManufacturerMenuDialog(manufacturerMenuDialog());
        mainSectionController.setNewEditInjectorDialog(newEditInjectorDialog());
        mainSectionController.setNewEditTestDialog(newEditTestDialog());
        mainSectionController.setVoltAmpereProfileController(voltAmpereProfileController);
        mainSectionController.setWidthCurrentSignalSpinner(injectorSectionController.getWidthCurrentSignalSpinner());
        mainSectionController.setFreqCurrentSignalSpinner(injectorSectionController.getFreqCurrentSignalSpinner());
        mainSectionController.setInjectorSectionStartToggleButton(injectorSectionController.getInjectorSectionStartToggleButton());
        mainSectionController.setInjectorsRepository(injectorsRepository);
        mainSectionController.setInjectorTestRepository(injectorTestRepository);
        mainSectionController.setFlowModbusWriter(flowModbusWriter);
        mainSectionController.setHighPressureSectionController(highPressureSectionController);
        mainSectionController.setI18N(i18N);
        mainSectionController.setMeasurements(measurements);
        mainSectionController.setFlowReport(flowReport);
        mainSectionController.setInfoController(infoController);
        mainSectionController.setTargetRPMSpinner(testBenchSectionController.getTargetRPMSpinner());
        mainSectionController.setDelayController(delayController);
        mainSectionController.setPrintDialogPanel(printDialogPanel());
        mainSectionController.setBoostU_state(boostU_state);
        mainSectionController.setBoostUadjustmentState(boostUadjustmentState);
        mainSectionController.setDelayReportModel(delayReportModel);
        mainSectionController.setRlc_reportModel(rlc_reportModel);
        mainSectionController.setCodingReportModel(codingReportModel);
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
                                                                 @Lazy ModbusRegisterProcessor standModbusWriter,
                                                                 DimasGUIEditionState dimasGUIEditionState) {
        TestBenchSectionController testBenchController = rootLayoutController.getTestBenchSectionController();
        testBenchController.setStandModbusWriter(standModbusWriter);
        testBenchController.setI18N(i18N);
        testBenchController.setDimasGUIEditionState(dimasGUIEditionState);
        return testBenchController;
    }

    @Bean
    @Autowired
    @DependsOn("regulatorsQTYController")
    public HighPressureSectionController highPressureSectionController(CRSectionController crSectionController,
                                                                       @Lazy ModbusRegisterProcessor ultimaModbusWriter,
                                                                       PressureSensorState pressureSensorState,
                                                                       RegulatorsQTYState regulatorsQTYState) {
        HighPressureSectionController highPressureSectionController = crSectionController.getHighPressureSectionController();
        highPressureSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        highPressureSectionController.setI18N(i18N);
        highPressureSectionController.setPressureSensorState(pressureSensorState);
        highPressureSectionController.setRegulatorsQTYState(regulatorsQTYState);
        return highPressureSectionController;
    }

    @Bean
    @Autowired
    public InjectorSectionController injectorSectionController(@Lazy Enabler enabler,
                                                               @Lazy ModbusRegisterProcessor ultimaModbusWriter,
                                                               TimerTasksManager timerTasksManager,
                                                               DelayController delayController,
                                                               InjConfigurationState injConfigurationState,
                                                               InjectorTypeToggleState injectorTypeToggleState,
                                                               BoostU_State boostU_state,
                                                               BoostUadjustmentState boostUadjustmentState,
                                                               Devices devices) {
        InjectorSectionController injectorSectionController = crSectionController().getInjectorSectionController();
        injectorSectionController.setEnabler(enabler);
        injectorSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        injectorSectionController.setTimerTasksManager(timerTasksManager);
        injectorSectionController.setDelayController(delayController);
        injectorSectionController.setI18N(i18N);
        injectorSectionController.setInjConfigurationState(injConfigurationState);
        injectorSectionController.setInjectorTypeToggleState(injectorTypeToggleState);
        injectorSectionController.setBoostU_state(boostU_state);
        injectorSectionController.setBoostUadjustmentState(boostUadjustmentState);
        injectorSectionController.setDevices(devices);
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
                                                         HighPressureSectionController highPressureSectionController,
                                                         FlowController flowController) {
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
        isaDetectionController.setPressReg1Spinner(highPressureSectionController.getPressReg1Spinner());
        isaDetectionController.setPressureLcd(highPressureSectionController.getPressureLcd());
        isaDetectionController.setDelivery1TextField(flowController.getDelivery1TextField());
        isaDetectionController.setDelivery2TextField(flowController.getDelivery2TextField());
        isaDetectionController.setDelivery3TextField(flowController.getDelivery3TextField());
        isaDetectionController.setDelivery4TextField(flowController.getDelivery4TextField());
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
    public FlowController flowController(TabSectionController tabSectionController) {
        FlowController flowController = tabSectionController.getFlowController();
        flowController.setI18N(i18N);
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
    public FlowReportController flowReportController(ReportController reportController, @Lazy FlowReport flowReport,
                                                     I18N i18N, @Lazy Enabler enabler, MainSectionController mainSectionController) {
        FlowReportController flowReportController = reportController.getFlowReportController();
        flowReportController.setFlowReport(flowReport);
        flowReportController.setI18N(i18N);
        flowReportController.setEnabler(enabler);
        flowReportController.setMainSectionStartToggleButton(mainSectionController.getStartToggleButton());
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
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler) {
        BeakerController beakerDelivery1Controller = flowController.getBeakerDelivery1Controller();
        setupBeakerController(beakerDelivery1Controller, flowController, flowController.getDelivery1TextField(),
                injectorSectionController.getLed1ToggleButton(), deliveryRescaler, "Delivery1", BeakerType.DELIVERY);
        return beakerDelivery1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery2Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler) {
        BeakerController beakerDelivery2Controller = flowController.getBeakerDelivery2Controller();
        setupBeakerController(beakerDelivery2Controller, flowController, flowController.getDelivery2TextField(),
                injectorSectionController.getLed2ToggleButton(), deliveryRescaler, "Delivery2", BeakerType.DELIVERY);
        return beakerDelivery2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery3Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler) {
        BeakerController beakerDelivery3Controller = flowController.getBeakerDelivery3Controller();
        setupBeakerController(beakerDelivery3Controller, flowController, flowController.getDelivery3TextField(),
                injectorSectionController.getLed3ToggleButton(), deliveryRescaler, "Delivery3", BeakerType.DELIVERY);
        return beakerDelivery3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery4Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler) {
        BeakerController beakerDelivery4Controller = flowController.getBeakerDelivery4Controller();
        setupBeakerController(beakerDelivery4Controller, flowController, flowController.getDelivery4TextField(),
                injectorSectionController.getLed4ToggleButton(), deliveryRescaler, "Delivery4", BeakerType.DELIVERY);
        return beakerDelivery4Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow1Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler) {
        BeakerController beakerBackFlow1Controller = flowController.getBeakerBackFlow1Controller();
        setupBeakerController(beakerBackFlow1Controller, flowController, flowController.getBackFlow1TextField(),
                injectorSectionController.getLed1ToggleButton(), backFlowRescaler, "Backflow1", BeakerType.BACKFLOW);
        return beakerBackFlow1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow2Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler) {
        BeakerController beakerBackFlow2Controller = flowController.getBeakerBackFlow2Controller();
        setupBeakerController(beakerBackFlow2Controller, flowController, flowController.getBackFlow2TextField(),
                injectorSectionController.getLed2ToggleButton(), backFlowRescaler, "Backflow2", BeakerType.BACKFLOW);
        return beakerBackFlow2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow3Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler) {
        BeakerController beakerBackFlow3Controller = flowController.getBeakerBackFlow3Controller();
        setupBeakerController(beakerBackFlow3Controller, flowController, flowController.getBackFlow3TextField(),
                injectorSectionController.getLed3ToggleButton(), backFlowRescaler, "Backflow3", BeakerType.BACKFLOW);
        return beakerBackFlow3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow4Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler) {
        BeakerController beakerBackFlow4Controller = flowController.getBeakerBackFlow4Controller();
        setupBeakerController(beakerBackFlow4Controller, flowController, flowController.getBackFlow4TextField(),
                injectorSectionController.getLed4ToggleButton(), backFlowRescaler, "Backflow4", BeakerType.BACKFLOW);
        return beakerBackFlow4Controller;
    }

    private void setupBeakerController(BeakerController beakerController, FlowController flowController, TextField textField,
                                       ToggleButton ledBeakerController, Rescaler rescaler, String name, BeakerType beakerType) {

        beakerController.setFlowController(flowController);
        beakerController.setTextField(textField);
        beakerController.setDeliveryRangeLabel(flowController.getDeliveryRangeLabel());
        beakerController.setBackFlowRangeLabel(flowController.getBackFlowRangeLabel());
        beakerController.setLedController(ledBeakerController);
        beakerController.setRescaler(rescaler);
        beakerController.setName(name);
        beakerController.setBeakerType(beakerType);

    }

    @Bean
    @Autowired
    public VoltageController voltageController(TabSectionController tabSectionController, InjectorSectionController injectorSectionController) {
        VoltageController voltageController = tabSectionController.getVoltageController();
        voltageController.setVoltAmpereProfileDialog(voltAmpereProfileDialog());
        voltageController.setParentController(tabSectionController);
        voltageController.setInjectorSectionController(injectorSectionController);
        voltageController.setI18N(i18N);
        return voltageController;
    }

    @Bean
    @Autowired
    public DelayController delayController(TabSectionController tabSectionController,
                                           DelayCalculator delayCalculator,
                                           DelayReportModel delayReportModel) {
        DelayController delayController = tabSectionController.getDelayController();
        delayController.setDelayCalculator(delayCalculator);
        delayController.setTabSectionController(tabSectionController);
        delayController.setI18N(i18N);
        delayController.setDelayReportModel(delayReportModel);
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
                                                                   InjectorSectionController injectorSectionController,
                                                                   VoltageController voltageController) {
        VoltAmpereProfileController voltAmpereProfileController = (VoltAmpereProfileController) voltAmpereProfileDialog().getController();
        voltAmpereProfileController.setUltimaModbusWriter(ultimaModbusWriter);
        voltAmpereProfileController.setInjectorSectionController(injectorSectionController);
        voltAmpereProfileController.setWidthSpinner(injectorSectionController.getWidthCurrentSignalSpinner());
        voltAmpereProfileController.setVoltageController(voltageController);
        voltAmpereProfileController.setI18N(i18N);
        return voltAmpereProfileController;
    }

    @Bean
    @Autowired
    public BoschController boschController(ViewHolder infoBosch,
                                           BoschRepository boschRepository){
        BoschController boschController = (BoschController) infoBosch.getController();
        boschController.setBoschRepository(boschRepository);
        return boschController;
    }

    @Bean
    @Autowired
    public SiemensController siemensController(ViewHolder infoSiemens,
                                               SiemensReferenceRepository siemensReferenceRepository){
        SiemensController siemensController = (SiemensController)infoSiemens.getController();
        siemensController.setSiemensReferenceRepository(siemensReferenceRepository);
        return siemensController;
    }

    @Bean
    @Autowired
    public DensoController densoController(ViewHolder infoDenso,
                                           DensoRepository densoRepository){
        DensoController densoController = (DensoController)infoDenso.getController();
        densoController.setDensoRepository(densoRepository);
        return densoController;
    }

    @Bean
    @Autowired
    public DelphiController delphiController(ViewHolder infoDelphi,
                                             DelphiRepository delphiRepository){
        DelphiController delphiController = (DelphiController)infoDelphi.getController();
        delphiController.setDelphiRepository(delphiRepository);
        return delphiController;
    }

    @Bean
    @Autowired
    public CaterpillarController caterpillarController(ViewHolder infoCaterpillar,
                                                       CaterpillarRepository caterpillarRepository){
        CaterpillarController caterpillarController = (CaterpillarController)infoCaterpillar.getController();
        caterpillarController.setCaterpillarRepository(caterpillarRepository);
        return caterpillarController;
    }

    @Bean
    @Autowired
    public AZPIController azpiController(ViewHolder infoAZPI,
                                         AZPIRepository azpiRepository){
        AZPIController azpiController = (AZPIController) infoAZPI.getController();
        azpiController.setAZPIRepository(azpiRepository);
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
                                         TabSectionController tabSectionController){
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
        return infoController;
    }

    @Bean
    @Autowired
    public StatusBarWrapper statusBar(Devices devices, FirmwareVersion<FlowVersions> flowFirmwareVersion,
                                      FirmwareVersion<StandVersions> standFirmwareVersion) {
        return new StatusBarWrapper(devices, "Ready", "Device not connected",
                StardexVersion.VERSION, flowFirmwareVersion, standFirmwareVersion);
    }

    @Bean
    public ViewHolder manufacturerMenuDialog() {
        return loadView("/fxml/dialogs/ManufacturerMenuDialog.fxml");
    }

    @Bean
    @Autowired
    public ManufacturerMenuDialogController manufacturerMenuDialogController(ListView<Manufacturer> manufacturerList,
                                                                             ManufacturerRepository manufacturerRepository) {
        ManufacturerMenuDialogController manufacturerMenuDialogController = (ManufacturerMenuDialogController) manufacturerMenuDialog().getController();
        manufacturerMenuDialogController.setManufacturerList(manufacturerList);
        manufacturerMenuDialogController.setManufacturerRepository(manufacturerRepository);
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
    @Autowired
    public PrintDialogPanelController newPrintDialogPanelController(MainSectionController mainSectionController,
                                                                    PDFService pdfService,
                                                                    I18N i18N){
        PrintDialogPanelController printDialogPanelController = (PrintDialogPanelController) printDialogPanel().getController();
        printDialogPanelController.setMainSectionController(mainSectionController);
        printDialogPanelController.setPdfService(pdfService);
        printDialogPanelController.setI18N(i18N);
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
                                                                           InjectorsRepository injectorsRepository) {
        NewEditInjectorDialogController newEditInjectorDialogController = (NewEditInjectorDialogController) newEditInjectorDialog().getController();
        newEditInjectorDialogController.setInjectorTypeRepository(injectorTypeRepository);
        newEditInjectorDialogController.setInjectorTestRepository(injectorTestRepository);
        newEditInjectorDialogController.setVoltAmpereProfileRepository(voltAmpereProfileRepository);
        newEditInjectorDialogController.setInjectorsRepository(injectorsRepository);
        newEditInjectorDialogController.setNewEditVOAPDialog(newEditVOAPDialog());
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
    public RLCController rlcController(InjectorSectionController injectorSectionController,
                                       ModbusRegisterProcessor ultimaModbusWriter,
                                       RegisterProvider ultimaRegisterProvider,
                                       TabSectionController tabSectionController,
                                       InjConfigurationState injConfigurationState,
                                       InjectorTypeToggleState injectorTypeToggleState,
                                       RLC_ReportModel rlc_reportModel) {
        RLCController RLCController = tabSectionController.getRlCController();
        RLCController.setInjectorSectionController(injectorSectionController);
        RLCController.setUltimaModbusWriter(ultimaModbusWriter);
        RLCController.setUltimaRegisterProvider(ultimaRegisterProvider);
        RLCController.setI18N(i18N);
        RLCController.setInjConfigurationState(injConfigurationState);
        RLCController.setInjConfigurationState(injConfigurationState);
        RLCController.setInjectorTypeToggleState(injectorTypeToggleState);
        RLCController.setRlc_reportModel(rlc_reportModel);
        return RLCController;

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
    public FlowViewController flowViewController(FlowViewState flowViewState,
                                                 Preferences preferences,
                                                 SettingsController settingsController){
        FlowViewController flowViewController = settingsController.getFlowViewController();
        flowViewController.setFlowViewState(flowViewState);
        flowViewController.setRootPrefs(preferences);
        return flowViewController;
    }

    @Bean
    @Autowired
    public InjConfigurationController injConfigurationController(InjConfigurationState injConfigurationState,
                                                                 Preferences preferences,
                                                                 InjectorTypeToggleState injectorTypeToggleState,
                                                                 SettingsController settingsController){
        InjConfigurationController injConfigurationController = settingsController.getInjConfigurationController();
        injConfigurationController.setInjConfigurationState(injConfigurationState);
        injConfigurationController.setI18N(i18N);
        injConfigurationController.setRootPrefs(preferences);
        injConfigurationController.setInjectorTypeToggleState(injectorTypeToggleState);
        return injConfigurationController;
    }

    @Bean
    @Autowired
    public LanguageController languageController(LanguageState languageState,
                                                 Preferences preferences,
                                                 SettingsController settingsController){
        LanguageController languageController = settingsController.getLanguageController();
        languageController.setLanguageState(languageState);
        languageController.setRootPrefs(preferences);
        languageController.setI18N(i18N);
        return languageController;
    }

    @Bean
    @Autowired
    public PressureSensorController pressureSensorController(PressureSensorState pressureSensorState,
                                                             Preferences preferences,
                                                             SettingsController settingsController){
        PressureSensorController pressureSensorController = settingsController.getPressureSensorController();
        pressureSensorController.setPressureSensorState(pressureSensorState);
        pressureSensorController.setRootPrefs(preferences);
        return pressureSensorController;
    }

    @Bean
    @Autowired
    public RegulatorsQTYController regulatorsQTYController(RegulatorsQTYState regulatorsQTYState,
                                                           Preferences preferences,
                                                           SettingsController settingsController){
        RegulatorsQTYController regulatorsQTYController = settingsController.getRegulatorsQTYController();
        regulatorsQTYController.setPressureSensorState(regulatorsQTYState);
        regulatorsQTYController.setRootPrefs(preferences);
        return regulatorsQTYController;
    }
}
