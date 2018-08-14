package fi.stardex.sisu.spring;

import fi.stardex.sisu.charts.TimerTasksManager;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.repos.InjectorTypeRepository;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.TestNamesRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.ViewHolder;
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
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.enums.BeakerType;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.obtainers.CurrentInjectorTestsObtainer;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import fi.stardex.sisu.util.rescalers.Rescaler;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.wrappers.StatusBarWrapper;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.StandFirmwareVersion;
import fi.stardex.sisu.version.StardexVersion;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ResourceBundle;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;

@Configuration
@ComponentScan(value = "fi.stardex.sisu")
public class JavaFXSpringConfigure {

    private final I18N i18N;
    private final UTF8Control utf8Control;

    private final Logger logger = LoggerFactory.getLogger(JavaFXSpringConfigure.class);

    public JavaFXSpringConfigure (I18N i18N) {
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
                                                       CurrentManufacturerObtainer currentManufacturerObtainer,
                                                       ApplicationConfigHandler applicationConfigHandler,
                                                       ApplicationAppearanceChanger applicationAppearanceChanger,
                                                       VoltAmpereProfileController voltAmpereProfileController,
                                                       DataConverter dataConverter,
                                                       CurrentInjectorObtainer currentInjectorObtainer,
                                                       InjectorSectionController injectorSectionController,
                                                       InjectorsRepository injectorsRepository,
                                                       InjectorTestRepository injectorTestRepository,
                                                       CurrentInjectorTestsObtainer currentInjectorTestsObtainer,
                                                       @Lazy ModbusRegisterProcessor flowModbusWriter,
                                                       RLCController RLCController) {
        MainSectionController mainSectionController = (MainSectionController) mainSection().getController();
        mainSectionController.setEnabler(enabler);
        mainSectionController.setCurrentManufacturerObtainer(currentManufacturerObtainer);
        mainSectionController.setApplicationConfigHandler(applicationConfigHandler);
        mainSectionController.setApplicationAppearanceChanger(applicationAppearanceChanger);
        mainSectionController.setManufacturerMenuDialog(manufacturerMenuDialog());
        mainSectionController.setNewEditInjectorDialog(newEditInjectorDialog());
        mainSectionController.setNewEditTestDialog(newEditTestDialog());
        mainSectionController.setVoltAmpereProfileController(voltAmpereProfileController);
        mainSectionController.setDataConverter(dataConverter);
        mainSectionController.setCurrentInjectorObtainer(currentInjectorObtainer);
        mainSectionController.setInjectorSectionController(injectorSectionController);
        mainSectionController.setInjectorsRepository(injectorsRepository);
        mainSectionController.setInjectorTestRepository(injectorTestRepository);
        mainSectionController.setCurrentInjectorTestsObtainer(currentInjectorTestsObtainer);
        mainSectionController.setFlowModbusWriter(flowModbusWriter);
        mainSectionController.setRLCController(RLCController);
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
        return testBenchController;
    }

    @Bean
    @Autowired
    public HighPressureSectionController highPressureSectionController(CRSectionController crSectionController) {
        return crSectionController.getHighPressureSectionController();
    }

    @Bean
    @Autowired
    public InjectorSectionController injectorSectionController(SettingsController settingsController,
                                                               @Lazy ModbusRegisterProcessor ultimaModbusWriter,
                                                               TimerTasksManager timerTasksManager, DelayController delayController) {
        InjectorSectionController injectorSectionController = crSectionController().getInjectorSectionController();
        injectorSectionController.setSettingsController(settingsController);
        injectorSectionController.setUltimaModbusWriter(ultimaModbusWriter);
        injectorSectionController.setTimerTasksManager(timerTasksManager);
        injectorSectionController.setDelayController(delayController);
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
    public AdditionalSectionController additionalSectionController() {
        return (AdditionalSectionController) additionalSection().getController();
    }

    @Bean
    @Autowired
    public FlowController flowController(AdditionalSectionController additionalSectionController) {
        return additionalSectionController.getFlowController();
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery1Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerDelivery1Controller = flowController.getBeakerDelivery1Controller();
        beakerDelivery1Controller.setFlowController(flowController);
        beakerDelivery1Controller.setTextField(flowController.getDelivery1TextField());
        beakerDelivery1Controller.setLedController(injectorSectionController.getLedBeaker1Controller());
        beakerDelivery1Controller.setRescaler(deliveryRescaler);
        beakerDelivery1Controller.setDataConverter(dataConverter);
        beakerDelivery1Controller.setName("Delivery1");
        beakerDelivery1Controller.setBeakerType(BeakerType.DELIVERY);
        return beakerDelivery1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery2Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerDelivery2Controller = flowController.getBeakerDelivery2Controller();
        beakerDelivery2Controller.setFlowController(flowController);
        beakerDelivery2Controller.setTextField(flowController.getDelivery2TextField());
        beakerDelivery2Controller.setLedController(injectorSectionController.getLedBeaker2Controller());
        beakerDelivery2Controller.setRescaler(deliveryRescaler);
        beakerDelivery2Controller.setDataConverter(dataConverter);
        beakerDelivery2Controller.setName("Delivery2");
        beakerDelivery2Controller.setBeakerType(BeakerType.DELIVERY);
        return beakerDelivery2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery3Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerDelivery3Controller = flowController.getBeakerDelivery3Controller();
        beakerDelivery3Controller.setFlowController(flowController);
        beakerDelivery3Controller.setTextField(flowController.getDelivery3TextField());
        beakerDelivery3Controller.setLedController(injectorSectionController.getLedBeaker3Controller());
        beakerDelivery3Controller.setRescaler(deliveryRescaler);
        beakerDelivery3Controller.setDataConverter(dataConverter);
        beakerDelivery3Controller.setName("Delivery3");
        beakerDelivery3Controller.setBeakerType(BeakerType.DELIVERY);
        return beakerDelivery3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerDelivery4Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler deliveryRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerDelivery4Controller = flowController.getBeakerDelivery4Controller();
        beakerDelivery4Controller.setFlowController(flowController);
        beakerDelivery4Controller.setTextField(flowController.getDelivery4TextField());
        beakerDelivery4Controller.setLedController(injectorSectionController.getLedBeaker4Controller());
        beakerDelivery4Controller.setRescaler(deliveryRescaler);
        beakerDelivery4Controller.setDataConverter(dataConverter);
        beakerDelivery4Controller.setName("Delivery4");
        beakerDelivery4Controller.setBeakerType(BeakerType.DELIVERY);
        return beakerDelivery4Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow1Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerBackFlow1Controller = flowController.getBeakerBackFlow1Controller();
        beakerBackFlow1Controller.setFlowController(flowController);
        beakerBackFlow1Controller.setTextField(flowController.getBackFlow1TextField());
        beakerBackFlow1Controller.setLedController(injectorSectionController.getLedBeaker1Controller());
        beakerBackFlow1Controller.setRescaler(backFlowRescaler);
        beakerBackFlow1Controller.setDataConverter(dataConverter);
        beakerBackFlow1Controller.setName("Backflow1");
        beakerBackFlow1Controller.setBeakerType(BeakerType.BACKFLOW);
        return beakerBackFlow1Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow2Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerBackFlow2Controller = flowController.getBeakerBackFlow2Controller();
        beakerBackFlow2Controller.setFlowController(flowController);
        beakerBackFlow2Controller.setTextField(flowController.getBackFlow2TextField());
        beakerBackFlow2Controller.setLedController(injectorSectionController.getLedBeaker2Controller());
        beakerBackFlow2Controller.setRescaler(backFlowRescaler);
        beakerBackFlow2Controller.setDataConverter(dataConverter);
        beakerBackFlow2Controller.setName("Backflow2");
        beakerBackFlow2Controller.setBeakerType(BeakerType.BACKFLOW);
        return beakerBackFlow2Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow3Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerBackFlow3Controller = flowController.getBeakerBackFlow3Controller();
        beakerBackFlow3Controller.setFlowController(flowController);
        beakerBackFlow3Controller.setTextField(flowController.getBackFlow3TextField());
        beakerBackFlow3Controller.setLedController(injectorSectionController.getLedBeaker3Controller());
        beakerBackFlow3Controller.setRescaler(backFlowRescaler);
        beakerBackFlow3Controller.setDataConverter(dataConverter);
        beakerBackFlow3Controller.setName("Backflow3");
        beakerBackFlow3Controller.setBeakerType(BeakerType.BACKFLOW);
        return beakerBackFlow3Controller;
    }

    @Bean
    @Autowired
    public BeakerController beakerBackFlow4Controller(FlowController flowController,
                                                      InjectorSectionController injectorSectionController,
                                                      Rescaler backFlowRescaler,
                                                      DataConverter dataConverter) {
        BeakerController beakerBackFlow4Controller = flowController.getBeakerBackFlow4Controller();
        beakerBackFlow4Controller.setFlowController(flowController);
        beakerBackFlow4Controller.setTextField(flowController.getBackFlow4TextField());
        beakerBackFlow4Controller.setLedController(injectorSectionController.getLedBeaker4Controller());
        beakerBackFlow4Controller.setRescaler(backFlowRescaler);
        beakerBackFlow4Controller.setDataConverter(dataConverter);
        beakerBackFlow4Controller.setName("Backflow4");
        beakerBackFlow4Controller.setBeakerType(BeakerType.BACKFLOW);
        return beakerBackFlow4Controller;
    }

    @Bean
    @Autowired
    public ConnectionController connectionController(AdditionalSectionController additionalSectionController,
                                                     ApplicationConfigHandler applicationConfigHandler) {
        ConnectionController connectionController = additionalSectionController.getConnectionController();
        connectionController.setApplicationConfigHandler(applicationConfigHandler);
        return connectionController;
    }

    @Bean
    @Autowired
    public VoltageController voltageController(AdditionalSectionController additionalSectionController, DataConverter dataConverter, InjectorSectionController injectorSectionController) {
        VoltageController voltageController = additionalSectionController.getVoltageController();
        voltageController.setVoltAmpereProfileDialog(voltAmpereProfileDialog());
        voltageController.setParentController(additionalSectionController);
        voltageController.setInjectorSectionController(injectorSectionController);
        voltageController.setFirmwareDataConverter(dataConverter);
        return voltageController;
    }

    @Bean
    @Autowired
    public DelayController delayController(AdditionalSectionController additionalSectionController,
                                           DelayCalculator delayCalculator) {
        DelayController delayController = additionalSectionController.getDelayController();
        delayController.setDelayCalculator(delayCalculator);
        delayController.setAdditionalSectionController(additionalSectionController);
        return delayController;
    }

    @Bean
    @Autowired
    public SettingsController settingsController(AdditionalSectionController additionalSectionController) {
        SettingsController settingsController = additionalSectionController.getSettingsController();
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
                                                                   VoltageController voltageController,
                                                                   DataConverter dataConverter) {
        VoltAmpereProfileController voltAmpereProfileController = (VoltAmpereProfileController) voltAmpereProfileDialog().getController();
        voltAmpereProfileController.setUltimaModbusWriter(ultimaModbusWriter);
        voltAmpereProfileController.setInjectorSectionController(injectorSectionController);
        voltAmpereProfileController.setWidthSpinner(injectorSectionController.getWidthCurrentSignal());
        voltAmpereProfileController.setVoltageController(voltageController);
        voltAmpereProfileController.setFirmwareDataConverter(dataConverter);
        return voltAmpereProfileController;
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
                                                                           InjectorsRepository injectorsRepository,
                                                                           CurrentManufacturerObtainer currentManufacturerObtainer,
                                                                           CurrentInjectorObtainer currentInjectorObtainer) {
        NewEditInjectorDialogController newEditInjectorDialogController = (NewEditInjectorDialogController) newEditInjectorDialog().getController();
        newEditInjectorDialogController.setInjectorTypeRepository(injectorTypeRepository);
        newEditInjectorDialogController.setInjectorTestRepository(injectorTestRepository);
        newEditInjectorDialogController.setVoltAmpereProfileRepository(voltAmpereProfileRepository);
        newEditInjectorDialogController.setInjectorsRepository(injectorsRepository);
        newEditInjectorDialogController.setNewEditVOAPDialog(newEditVOAPDialog());
        newEditInjectorDialogController.setCurrentManufacturerObtainer(currentManufacturerObtainer);
        newEditInjectorDialogController.setCurrentInjectorObtainer(currentInjectorObtainer);
        return newEditInjectorDialogController;
    }

    @Bean
    public ViewHolder newEditTestDialog() {
        return loadView("/fxml/dialogs/NewEditTestDialog.fxml");
    }

    @Bean
    @Autowired
    public NewEditTestDialogController newEditTestDialogController(CurrentInjectorObtainer currentInjectorObtainer,
                                                                   InjectorTestRepository injectorTestRepository,
                                                                   TestNamesRepository testNamesRepositor) {
        NewEditTestDialogController newEditTestDialogController = (NewEditTestDialogController) newEditTestDialog().getController();
        newEditTestDialogController.setCurrentInjectorObtainer(currentInjectorObtainer);
        newEditTestDialogController.setInjectorTestRepository(injectorTestRepository);
        newEditTestDialogController.setTestNamesRepository(testNamesRepositor);
        return newEditTestDialogController;
    }

    private ViewHolder loadView(String url) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url), ResourceBundle.getBundle("properties.labels", i18N.getLocale(), utf8Control));
        ViewHolder viewHolder = new ViewHolder();
        try {
            viewHolder.setView(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Exception while load view {}", url, e);
        }
        viewHolder.setController(fxmlLoader.getController());
        return viewHolder;
    }

    @Bean
    @Autowired
    public RLCController rlcController(InjectorSectionController injectorSectionController,
                                       SettingsController settingsController,
                                       ModbusRegisterProcessor ultimaModbusWriter,
                                       RegisterProvider ultimaRegisterProvider,
                                       CurrentInjectorObtainer currentInjectorObtainer,
                                       MeasurementResultsStorage measurementResultsStorage,
                                       AdditionalSectionController additionalSectionController){
        RLCController RLCController = additionalSectionController.getRlCController();
        RLCController.setInjectorSectionController(injectorSectionController);
        RLCController.setSettingsController(settingsController);
        RLCController.setUltimaModbusWriter(ultimaModbusWriter);
        RLCController.setUltimaRegisterProvider(ultimaRegisterProvider);
        RLCController.setCurrentInjectorObtainer(currentInjectorObtainer);
        RLCController.setMeasurementResultsStorage(measurementResultsStorage);
        return RLCController;

    }
}
