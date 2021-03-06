package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.PiezoRepairModel;
import fi.stardex.sisu.model.Step3Model;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.model.updateModels.TestBenchSectionUpdateModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.states.DimasGUIEditionState;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.GUI_type;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.UltimaFirmwareVersion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.Main_version_0;
import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.UIS_to_CR_pulseControlSwitch;

public class GUI_TypeController {

    private final Logger logger = LoggerFactory.getLogger(GUI_TypeController.class);

    @FXML private ImageView logoImage;
    @FXML private ComboBox<GUI_type> gui_typeComboBox;
    private Preferences rootPreferences;
    private Parent mainSection;
    private Parent mainSectionPumps;
    private Parent mainSectionUIS;
    private Parent crSection;
    private Parent pumpSection;
    private Parent tabSection;
    private Parent tabSectionPumps;
    private Parent uisTabSection;
    private Parent injectorSectionUis;
    private GridPane mainSectionGridPane;
    private GridPane additionalSectionGridPane;
    private GridPane settingsGridPaneCR;
    private GridPane settingsGridPanePumps;
    private GridPane uisSettingsGridPane;
    private Parent activeMainSection;
    private Parent activeChangeableSection;
    private Parent activeTabSection;
    private Parent settings;
    private Parent connection;
    private Parent uisSettings;
    private DimasGUIEditionState dimasGUIEditionState;
    private GUI_TypeModel gui_typeModel;
    private PumpsStartButtonState pumpsStartButtonState;
    private MainSectionModel mainSectionModel;
    private InjectorSectionPwrState injectorSectionPwrState;
    private MainSectionUisModel mainSectionUisModel;
    private UisInjectorSectionModel uisInjectorSectionModel;
    private Step3Model step3Model;
    private PiezoRepairModel piezoRepairModel;
    private TestBenchSectionUpdateModel testBenchSectionUpdateModel;
    private ModbusRegisterProcessor ultimaModbusWriter;
    private RegisterProvider ultimaRegisterProvider;
    private FirmwareVersion<UltimaFirmwareVersion.UltimaVersions> ultimaFirmwareVersion;
    @Value("${customer}")
    private String customer;
    @Value("${logo.image}")
    private String logoPath;

    private Node pumpRpmLimitSpinner;
    private Node heuiMaxPressureSpinner;
    private Node pumpRpmLimitLabel;
    private Node heuiMaxPressureLabel;
    private List<Node> limitsStackPanes;

    public void setRootPreferences(Preferences rootPreferences) {
        this.rootPreferences = rootPreferences;
    }
    public void setMainSection(Parent mainSection) {
        this.mainSection = mainSection;
    }
    public void setCRSection(Parent crSection) {
        this.crSection = crSection;
    }
    public void setMainSectionUIS(Parent mainSectionUIS) {
        this.mainSectionUIS = mainSectionUIS;
    }
    public void setInjectorSectionUis(Parent injectorSectionUis) {
        this.injectorSectionUis = injectorSectionUis;
    }
    public void setMainSectionGridPane(GridPane mainSectionGridPane) {
        this.mainSectionGridPane = mainSectionGridPane;
    }
    public void setAdditionalSectionGridPane(GridPane additionalSectionGridPane) {
        this.additionalSectionGridPane = additionalSectionGridPane;
    }
    public void setMainSectionPumps(Parent mainSectionPumps) {
        this.mainSectionPumps = mainSectionPumps;
    }
    public void setPumpSection(Parent pumpSection) {
        this.pumpSection = pumpSection;
    }
    public void setConnection(Parent connection) {
        this.connection = connection;
    }
    public void setDimasGUIEditionState(DimasGUIEditionState dimasGUIEditionState) {
        this.dimasGUIEditionState = dimasGUIEditionState;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }
    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }
    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }
    public void setInjectorSectionPwrState(InjectorSectionPwrState injectorSectionPwrState) {
        this.injectorSectionPwrState = injectorSectionPwrState;
    }
    public void setTabSection(Parent tabSection) {
        this.tabSection = tabSection;
    }
    public void setTabSectionPumps(Parent tabSectionPumps) {
        this.tabSectionPumps = tabSectionPumps;
    }
    public void setUisTabSection(Parent uisTabSection) {
        this.uisTabSection = uisTabSection;
    }
    public void setSettings(Parent settings) {
        this.settings = settings;
    }
    public void setUisSettings(Parent uisSettings) {
        this.uisSettings = uisSettings;
    }
    public void setSettingsGridPaneCR(GridPane settingsGridPaneCR) {
        this.settingsGridPaneCR = settingsGridPaneCR;
    }
    public void setSettingsGridPanePumps(GridPane settingsGridPanePumps) {
        this.settingsGridPanePumps = settingsGridPanePumps;
    }
    public void setUisSettingsGridPane(GridPane uisSettingsGridPane) {
        this.uisSettingsGridPane = uisSettingsGridPane;
    }
    public void setStep3Model(Step3Model step3Model) {
        this.step3Model = step3Model;
    }
    public void setPiezoRepairModel(PiezoRepairModel piezoRepairModel) {
        this.piezoRepairModel = piezoRepairModel;
    }
    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }
    public void setTestBenchSectionUpdateModel(TestBenchSectionUpdateModel testBenchSectionUpdateModel) {
        this.testBenchSectionUpdateModel = testBenchSectionUpdateModel;
    }
    public void setUltimaRegisterProvider(RegisterProvider ultimaRegisterProvider) {
        this.ultimaRegisterProvider = ultimaRegisterProvider;
    }
    public void setUltimaFirmwareVersion(FirmwareVersion<UltimaFirmwareVersion.UltimaVersions> ultimaFirmwareVersion) {
        this.ultimaFirmwareVersion = ultimaFirmwareVersion;
    }

    @PostConstruct
    private void init() {

        switch (customer) {
            case "merlin":
                logoImage.setImage(new Image(logoPath));
                logoImage.setFitHeight(80);
                break;
            case "stardex":
            default:
                break;
        }

        gui_typeComboBox.getItems().addAll(GUI_type.values());

        gui_typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {

                System.err.println(newValue);
                switch (newValue) {
                    case CR_Inj:
                        changeToCRInj();
                        break;
                    case CR_Pump:
                        changeToCRPump();
                        break;
                    case UIS:
                        changeToUIS();
                        break;
                    case HEUI:
                        changeToHEUI();
                        break;
                }
                gui_typeModel.guiTypeProperty().setValue(newValue);
                rootPreferences.put("GUI_Type", newValue.toString());
            }
        });

        pumpRpmLimitSpinner = getNode("limitControlsStackPane", "pumpRpmLimitSpinner");
        heuiMaxPressureSpinner = getNode("limitControlsStackPane", "heuiMaxPressureSpinner");
        pumpRpmLimitLabel = getNode("limitLabelsStackPane", "pumpRpmLimitLabel");
        heuiMaxPressureLabel = getNode("limitLabelsStackPane", "heuiMaxPressureLabel");

        limitsStackPanes = settings.getChildrenUnmodifiable()
                .stream()
                .filter(node -> node.getId() != null)
                .filter(node -> node.getId().equals("limitControlsStackPane") || node.getId().equals("limitLabelsStackPane")).collect(Collectors.toList());


        GUI_type currentGUIType = GUI_type.getType(rootPreferences.get("GUI_Type", GUI_type.CR_Inj.toString()));
        /**Uncomment string below and comment string above in case of problems with initial gui-type search in rootPreferences.*/
//        GUI_type currentGUIType = GUI_type.CR_Inj;

        gui_typeComboBox.getSelectionModel().select(currentGUIType);
        dimasGUIEditionState.isDimasGuiEditionProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setVisible(!newValue));
        step3Model.step3PauseProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(newValue));
        piezoRepairModel.startMeasureProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(newValue));
        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(newValue));
        mainSectionModel.startButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(isStarted()));
        injectorSectionPwrState.powerButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(isStarted()));
        mainSectionUisModel.startButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(isStarted()));
        uisInjectorSectionModel.injectorButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(isStarted()));
        testBenchSectionUpdateModel.sectionStartedProperty().addListener((observable, oldValue, newValue) -> gui_typeComboBox.setDisable(isStarted()));

        ultimaFirmwareVersion.versionProperty().addListener((observableValue, oldValue, newValue) -> {

            ultimaRegisterProvider.read(Main_version_0);
            if ((int)Main_version_0.getLastValue() == 93) {
                /**Attention! If DIMAS GUI Edition was set before connecting to Ultima version starting with 93.xxx,
                 * then it becomes impossible to switch to CR even if regular versions are connected to afterwards.
                 * Ways to fix:
                 * 1. delete line starting with <entry key="GUI_Type" in all files prefs.xml and restart
                 * 2. replace CR_Pump by CR_Inj in all lines <entry key="GUI_Type" value="CR_Pump"/> in all files prefs.xml and restart
                 * 3. Add gui_typeComboBox.getSelectionModel().select(GUI_type.CR_Inj); in else{} block as a first line and recompile
                 * file prefs.xml could be found in Linux by command in terminal: grep -rs "<entry key=\"GUI_Type\"" /home
                 * */
                Platform.runLater(()-> {
                    gui_typeComboBox.getSelectionModel().select(GUI_type.CR_Pump);
                    gui_typeComboBox.setVisible(false);
                });
            }
            else{
                Platform.runLater(()-> gui_typeComboBox.setVisible(!dimasGUIEditionState.isDimasGuiEditionProperty().get()));
            }
        });
    }

    private Node getNode(String stackPane, String nodeId) {

        return settings.getChildrenUnmodifiable()
                .stream()
                .filter(n -> n.getId() != null)
                .filter(n -> n.getId().equals(stackPane))
                .filter(n -> n instanceof StackPane)
                .map(n -> (StackPane) n)
                .flatMap(n -> n.getChildren().stream())
                .filter(n -> n.getId().equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    private void changeToCRInj() {

        clearSections();
        settingsGridPaneCR.getChildren().clear();

        mainSectionGridPane.add(mainSection, 0, 1);
        additionalSectionGridPane.add(crSection, 0, 1);
        additionalSectionGridPane.add(tabSection, 0, 2);

        settingsGridPaneCR.add(connection, 0, 0);
        settingsGridPaneCR.add(settings, 1, 0);

        settings.getChildrenUnmodifiable()
                .stream()
                .filter(node -> node.getId() != null)
                .filter(node -> node.getId().equals("isDIMASCheckBox"))
                .findAny()
                .ifPresent(c -> c.setVisible(true));

        settings.getChildrenUnmodifiable()
                .stream()
                .filter(node -> node.getId() != null)
                .filter(node -> node.getId().equals("differentialFlowMeterButton"))
                .findAny()
                .ifPresent(c -> c.setVisible(true));

        settings.getChildrenUnmodifiable()
                .stream()
                .filter(node -> node.getId() != null)
                .filter(node -> node.getId().equals("diffFmSettingsLabel"))
                .findAny()
                .ifPresent(c -> c.setVisible(true));

        limitsStackPanes.forEach(c -> c.setVisible(false));

        activeMainSection = mainSection;
        activeChangeableSection = crSection;
        activeTabSection = tabSection;

        ultimaModbusWriter.add(UIS_to_CR_pulseControlSwitch, 0);

        logger.info("Changed to CrInj");
    }

    private void changeToCRPump() {

        clearSections();
        settingsGridPanePumps.getChildren().clear();

        mainSectionGridPane.add(mainSectionPumps, 0, 1);
        additionalSectionGridPane.add(pumpSection, 0, 1);
        additionalSectionGridPane.add(tabSectionPumps, 0, 2);

        settingsGridPanePumps.add(connection, 0, 0);
        settingsGridPanePumps.add(settings, 1, 0);

        settings.getChildrenUnmodifiable()
                .stream()
                .filter(node -> node.getId() != null)
                .filter(node -> node.getId().equals("isDIMASCheckBox"))
                .findAny()
                .ifPresent(c -> c.setVisible(false));

        settings.getChildrenUnmodifiable()
                .stream()
                .filter(node -> node.getId() != null)
                .filter(node -> node.getId().equals("differentialFlowMeterButton"))
                .findAny()
                .ifPresent(c -> c.setVisible(false));

        settings.getChildrenUnmodifiable()
                .stream()
                .filter(node -> node.getId() != null)
                .filter(node -> node.getId().equals("diffFmSettingsLabel"))
                .findAny()
                .ifPresent(c -> c.setVisible(false));

        limitsStackPanes.forEach(c -> c.setVisible(true));
        pumpRpmLimitLabel.setVisible(true);
        heuiMaxPressureLabel.setVisible(false);
        pumpRpmLimitSpinner.toFront();
        heuiMaxPressureSpinner.toBack();

        activeMainSection = mainSectionPumps;
        activeChangeableSection = pumpSection;
        activeTabSection = tabSectionPumps;

        ultimaModbusWriter.add(UIS_to_CR_pulseControlSwitch, 0);

        logger.info("Changed to CrPump");
    }

    private void changeToHEUI() {

        changeToCRInj();

        limitsStackPanes.forEach(c -> c.setVisible(true));
        pumpRpmLimitLabel.setVisible(false);
        heuiMaxPressureLabel.setVisible(true);
        pumpRpmLimitSpinner.toBack();
        heuiMaxPressureSpinner.toFront();
    }

    private void changeToUIS() {

        clearSections();
        uisSettingsGridPane.getChildren().clear();

        mainSectionGridPane.add(mainSectionUIS, 0, 1);
        additionalSectionGridPane.add(injectorSectionUis, 0, 1);
        additionalSectionGridPane.add(uisTabSection, 0, 2);

        uisSettingsGridPane.add(connection, 0, 0);
        uisSettingsGridPane.add(uisSettings, 1, 0);

        activeMainSection = mainSectionUIS;
        activeChangeableSection = injectorSectionUis;
        activeTabSection = uisTabSection;

        ultimaModbusWriter.add(UIS_to_CR_pulseControlSwitch, 1);

        logger.info("Changed to UIS");
    }

    private void clearSections() {

        mainSectionGridPane.getChildren().remove(activeMainSection);
        additionalSectionGridPane.getChildren().removeAll(activeChangeableSection, activeTabSection);
    }

    private boolean isStarted() {
        return mainSectionModel.startButtonProperty().get()
                || injectorSectionPwrState.powerButtonProperty().get()
                || mainSectionUisModel.startButtonProperty().get()
                || uisInjectorSectionModel.injectorButtonProperty().get()
                || testBenchSectionUpdateModel.sectionStartedProperty().get();
    }
}