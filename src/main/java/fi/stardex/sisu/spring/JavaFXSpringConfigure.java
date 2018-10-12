package fi.stardex.sisu.spring;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.measurement.Measurements;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.repos.ISADetectionRepository;
import fi.stardex.sisu.persistence.repos.InjectorTypeRepository;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.*;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.store.FlowReport;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.ISADetectionController;
import fi.stardex.sisu.ui.controllers.RootLayoutController;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.additional.BeakerController;
import fi.stardex.sisu.ui.controllers.additional.dialogs.VoltAmpereProfileController;
import fi.stardex.sisu.ui.controllers.additional.tabs.*;
import fi.stardex.sisu.ui.controllers.cr.CRSectionController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditInjectorDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditTestDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditVOAPDialogController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.VisualUtils;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.StardexVersion;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;

@Configuration
@ComponentScan(value = "fi.stardex.sisu")
public class JavaFXSpringConfigure {

    private final I18N i18N;
    private final UTF8Control utf8Control;

    private final Logger logger = LoggerFactory.getLogger(JavaFXSpringConfigure.class);

    public JavaFXSpringConfigure(I18N i18N) {
        this.i18N = i18N;
        this.utf8Control = new UTF8Control();
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
    public ViewHolder mainSection() {
        return loadView("/fxml/sections/Main/MainSection.fxml");
    }

    @Bean
    @Autowired
    public MainSectionController mainSectionController(@Lazy Enabler enabler,
                                                       ApplicationAppearanceChanger applicationAppearanceChanger,
                                                       VoltAmpereProfileController voltAmpereProfileController,
                                                       InjectorSectionController injectorSectionController,
                                                       InjectorsRepository injectorsRepository,
                                                       InjectorTestRepository injectorTestRepository,
                                                       @Lazy ModbusRegisterProcessor flowModbusWriter,
                                                       HighPressureSectionController highPressureSectionController,
                                                       Preferences rootPrefs, @Lazy Measurements measurements,
                                                       @Lazy FlowReport flowReport,
                                                       InfoController infoController) {
        MainSectionController mainSectionController = (MainSectionController) mainSection().getController();
        mainSectionController.setEnabler(enabler);
        mainSectionController.setApplicationAppearanceChanger(applicationAppearanceChanger);
        mainSectionController.setManufacturerMenuDialog(manufacturerMenuDialog());
        mainSectionController.setNewEditInjectorDialog(newEditInjectorDialog());
        mainSectionController.setNewEditTestDialog(newEditTestDialog());
        mainSectionController.setVoltAmpereProfileController(voltAmpereProfileController);
        mainSectionController.setWidthCurrentSignalSpinner(injectorSectionController.getWidthCurrentSignalSpinner());
        mainSectionController.setFreqCurrentSignalSpinner(injectorSectionController.getFreqCurrentSignalSpinner());
        mainSectionController.setInjectorsRepository(injectorsRepository);
        mainSectionController.setInjectorTestRepository(injectorTestRepository);
        mainSectionController.setFlowModbusWriter(flowModbusWriter);
        mainSectionController.setHighPressureSectionController(highPressureSectionController);
        mainSectionController.setI18N(i18N);
        mainSectionController.setRootPrefs(rootPrefs);
        mainSectionController.setMeasurements(measurements);
        mainSectionController.setFlowReport(flowReport);
        mainSectionController.setInfoController(infoController);
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
    public TestBenchSectionController testBenchSectionController(CRSectionController crSectionController,
                                                                 @Lazy ModbusRegisterProcessor standModbusWriter) {
        TestBenchSectionController testBenchController = crSectionController.getTestBenchSectionController();
        testBenchController.setStandModbusWriter(standModbusWriter);
        testBenchController.setI18N(i18N);
        return testBenchController;
    }

    @Bean
    @Autowired
    public HighPressureSectionController highPressureSectionController(CRSectionController crSectionController,
                                                                       @Lazy ModbusRegisterProcessor ultimaModbusWriter) {
        HighPressureSectionController highPressureSectionController = crSectionController.getHighPressureSectionController();
        highPressureSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        highPressureSectionController.setI18N(i18N);
        return highPressureSectionController;
    }

    @Bean
    @Autowired
    public InjectorSectionController injectorSectionController(SettingsController settingsController, @Lazy Enabler enabler,
                                                               @Lazy ModbusRegisterProcessor ultimaModbusWriter,
                                                               TimerTasksManager timerTasksManager, DelayController delayController) {
        InjectorSectionController injectorSectionController = crSectionController().getInjectorSectionController();
        injectorSectionController.setInjectorsConfigComboBox(settingsController.getInjectorsConfigComboBox());
        injectorSectionController.setEnabler(enabler);
        injectorSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        injectorSectionController.setTimerTasksManager(timerTasksManager);
        injectorSectionController.setDelayController(delayController);
        injectorSectionController.setI18N(i18N);
        return injectorSectionController;
    }

    @Bean
    public ViewHolder uisSection() {
        return loadView("/fxml/sections/UIS/UISSection.fxml");
    }

    @Bean
    public ViewHolder additionalSection() {
        return loadView("/fxml/sections/Additional/AdditionalSection.fxml");
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
        return loadView("/fxml/sections/Additional/tabs/DefaultOEM.fxml");
    }

    @Bean ViewHolder infoBosch(){
        return loadView("/fxml/sections/Additional/tabs/Bosch.fxml");
    }

    @Bean ViewHolder infoSiemens(){
        return loadView("/fxml/sections/Additional/tabs/Siemens.fxml");
    }

    @Bean ViewHolder infoDenso(){
        return loadView("/fxml/sections/Additional/tabs/Denso.fxml");
    }

    @Bean ViewHolder infoCaterpillar(){
        return loadView("/fxml/sections/Additional/tabs/Caterpillar.fxml");
    }

    @Bean ViewHolder infoAZPI(){
        return loadView("/fxml/sections/Additional/tabs/AZPI.fxml");
    }

    @Bean ViewHolder infoDelphi(){
        return loadView("/fxml/sections/Additional/tabs/Delphi.fxml");
    }

    @Bean
    public AdditionalSectionController additionalSectionController() {
        AdditionalSectionController additionalSectionController = (AdditionalSectionController)additionalSection().getController();
        additionalSectionController.setI18N(i18N);
        return additionalSectionController;
    }

    @Bean
    @Autowired
    public CodingController codingController(AdditionalSectionController additionalSectionController){
        CodingController codingController = additionalSectionController.getCodingController();
        codingController.setI18N(i18N);
        return codingController;
    }

    @Bean
    @Autowired
    public FlowController flowController(AdditionalSectionController additionalSectionController) {
        FlowController flowController = additionalSectionController.getFlowController();
        flowController.setI18N(i18N);
        return flowController;
    }

    @Bean
    @Autowired
    public ReportController reportController(AdditionalSectionController additionalSectionController) {
        return additionalSectionController.getReportController();
    }

    @Bean
    @Autowired
    public FlowReportController flowReportController(ReportController reportController, @Lazy FlowReport flowReport) {
        FlowReportController flowReportController = reportController.getFlowReportController();
        flowReportController.setFlowReport(flowReport);
        return flowReportController;
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
    public ConnectionController connectionController(Preferences rootPrefs) {
        ConnectionController connectionController = additionalSectionController().getConnectionController();
        connectionController.setI18N(i18N);
        connectionController.setRootPrefs(rootPrefs);
        return connectionController;
    }

    @Bean
    @Autowired
    public VoltageController voltageController(AdditionalSectionController additionalSectionController, InjectorSectionController injectorSectionController) {
        VoltageController voltageController = additionalSectionController.getVoltageController();
        voltageController.setVoltAmpereProfileDialog(voltAmpereProfileDialog());
        voltageController.setParentController(additionalSectionController);
        voltageController.setInjectorSectionController(injectorSectionController);
        voltageController.setI18N(i18N);
        return voltageController;
    }

    @Bean
    @Autowired
    public DelayController delayController(AdditionalSectionController additionalSectionController,
                                           DelayCalculator delayCalculator) {
        DelayController delayController = additionalSectionController.getDelayController();
        delayController.setDelayCalculator(delayCalculator);
        delayController.setAdditionalSectionController(additionalSectionController);
        delayController.setI18N(i18N);
        return delayController;
    }

    @Bean
    @Autowired
    public SettingsController settingsController(AdditionalSectionController additionalSectionController,
                                                 Preferences rootPrefs,
                                                 HighPressureSectionController highPressureSectionController) {
        SettingsController settingsController = additionalSectionController.getSettingsController();
        settingsController.setI18N(i18N);
        settingsController.setRootPrefs(rootPrefs);
        settingsController.setHighPressureSectionController(highPressureSectionController);
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
                                         AdditionalSectionController additionalSectionController){
        InfoController infoController = additionalSectionController.getInfoController();
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
    public ApplicationAppearanceChanger applicationAppearanceChanger(ViewHolder crSection, ViewHolder uisSection,
                                                                     ViewHolder additionalSection, RootLayoutController rootLayoutController) {
        return new ApplicationAppearanceChanger(crSection.getView(), uisSection.getView(),
                additionalSection.getView(), rootLayoutController.getSectionLayout());
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
                                       SettingsController settingsController,
                                       ModbusRegisterProcessor ultimaModbusWriter,
                                       RegisterProvider ultimaRegisterProvider,
                                       AdditionalSectionController additionalSectionController) {
        RLCController RLCController = additionalSectionController.getRlCController();
        RLCController.setInjectorSectionController(injectorSectionController);
        RLCController.setSettingsController(settingsController);
        RLCController.setUltimaModbusWriter(ultimaModbusWriter);
        RLCController.setUltimaRegisterProvider(ultimaRegisterProvider);
        RLCController.setI18N(i18N);
        return RLCController;

    }

    private ViewHolder loadView(String url) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url),
                ResourceBundle.getBundle("properties.labels", i18N.getLocale(), utf8Control));
        ViewHolder viewHolder = new ViewHolder();
        try {
            viewHolder.setView(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Exception while load view {}", url, e);
        }
        viewHolder.setController(fxmlLoader.getController());
        return viewHolder;
    }

}
