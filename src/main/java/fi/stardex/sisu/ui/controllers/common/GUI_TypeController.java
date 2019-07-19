package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.model.pump.ManufacturerPumpModel;
import fi.stardex.sisu.states.DimasGUIEditionState;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.states.PumpsStartButtonState;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class GUI_TypeController {

    private final Logger logger = LoggerFactory.getLogger(GUI_TypeController.class);

    public enum GUIType {
        CR_Inj, CR_Pump, UIS, HEUI
    }

    @FXML
    private ComboBox<GUIType> gui_typeComboBox;

    private Preferences rootPreferences;

    private Parent mainSection;

    private Parent mainSectionPumps;

    private Parent mainSectionUIS;

    private Parent crSection;

    private Parent pumpSection;

    private Parent uisSection;

    private Parent tabSection;

    private Parent tabSectionPumps;

    private GridPane mainSectionGridPane;

    private GridPane additionalSectionGridPane;

    private GridPane settingsGridPaneCR;

    private GridPane settingsGridPanePumps;

    private Parent activeMainSection;

    private Parent activeChangeableSection;

    private Parent activeTabSection;

    private Parent settings;

    private Parent connection;

    private DimasGUIEditionState dimasGUIEditionState;

    private ManufacturerPumpModel manufacturerPumpModel;

    private GUI_TypeModel gui_typeModel;

    private PumpsStartButtonState pumpsStartButtonState;

    private MainSectionModel mainSectionModel;

    private InjectorSectionPwrState injectorSectionPwrState;

    public ComboBox<GUIType> getGui_typeComboBox() {
        return gui_typeComboBox;
    }

    public void setManufacturerPumpModel(ManufacturerPumpModel manufacturerPumpModel) {
        this.manufacturerPumpModel = manufacturerPumpModel;
    }
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

    public void setUisSection(Parent uisSection) {
        this.uisSection = uisSection;
    }

    public void setTabSection(Parent tabSection) {
        this.tabSection = tabSection;
    }
    public void setMainSectionGridPane(GridPane mainSectionGridPane) {
        this.mainSectionGridPane = mainSectionGridPane;
    }
    public void setAdditionalSectionGridPane(GridPane additionalSectionGridPane) {
        this.additionalSectionGridPane = additionalSectionGridPane;
    }
    public void setSettingsGridPaneCR(GridPane settingsGridPaneCR) {
        this.settingsGridPaneCR = settingsGridPaneCR;
    }
    public void setSettingsGridPanePumps(GridPane settingsGridPanePumps) {
        this.settingsGridPanePumps = settingsGridPanePumps;
    }
    public void setMainSectionPumps(Parent mainSectionPumps) {
        this.mainSectionPumps = mainSectionPumps;
    }
    public void setPumpSection(Parent pumpSection) {
        this.pumpSection = pumpSection;
    }
    public void setTabSectionPumps(Parent tabSectionPumps) {
        this.tabSectionPumps = tabSectionPumps;
    }
    public void setSettings(Parent settings) {
        this.settings = settings;
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

    @PostConstruct
    private void init() {

        gui_typeComboBox.getItems().addAll(GUIType.values());

        gui_typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {

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

        GUIType currentGUIType = GUIType.valueOf(rootPreferences.get("GUI_Type", GUIType.CR_Inj.toString()));

        gui_typeComboBox.getSelectionModel().select(currentGUIType);
        gui_typeComboBox.visibleProperty().bind(dimasGUIEditionState.isDimasGuiEditionProperty().not());
        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(newValue));
        mainSectionModel.startButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(isStarted()));
        injectorSectionPwrState.powerButtonProperty().addListener((observableValue, oldValue, newValue) -> gui_typeComboBox.setDisable(isStarted()));
    }

    private void changeToCRInj() {

        clearSections();

        mainSectionGridPane.add(mainSection, 0, 1);
        additionalSectionGridPane.add(crSection, 0, 1);
        additionalSectionGridPane.add(tabSection, 0, 2);

        if (settingsGridPaneCR.getChildren().isEmpty()) {
            settingsGridPaneCR.add(connection, 0, 0);
            settingsGridPaneCR.add(settings, 1, 0);
        }

        settings.getChildrenUnmodifiable().stream().filter(node -> node.getId().equals("isDIMASCheckBox")).forEach(node -> node.setVisible(true));

        activeMainSection = mainSection;
        activeChangeableSection = crSection;
        activeTabSection = tabSection;

        logger.info("Changed to CrInj");

    }

    private void changeToCRPump() {

        clearSections();

        mainSectionGridPane.add(mainSectionPumps, 0, 1);
        additionalSectionGridPane.add(pumpSection, 0, 1);
        additionalSectionGridPane.add(tabSectionPumps, 0, 2);

        if (settingsGridPanePumps.getChildren().isEmpty()) {
            settingsGridPanePumps.add(connection, 0, 0);
            settingsGridPanePumps.add(settings, 1, 0);
        }

        settings.getChildrenUnmodifiable().stream().filter(node -> node.getId().equals("isDIMASCheckBox")).forEach(node -> node.setVisible(false));


        activeMainSection = mainSectionPumps;
        activeChangeableSection = pumpSection;
        activeTabSection = tabSectionPumps;

        manufacturerPumpModel.initManufacturerPumpList();

        logger.info("Changed to CrPump");

    }

    private void changeToHEUI() {

        changeToCRInj();
    }
    //TODO do not forget to hide/show DIMASCheckBox after implementation
    private void changeToUIS() {

        clearSections();

        mainSectionGridPane.add(mainSectionUIS, 0, 1);

        additionalSectionGridPane.add(uisSection, 0, 1);
        activeMainSection = mainSectionUIS;
        activeChangeableSection = uisSection;
        activeTabSection = null;

        logger.info("Changed to UIS");

    }

    private void clearSections() {

        mainSectionGridPane.getChildren().remove(activeMainSection);
        additionalSectionGridPane.getChildren().removeAll(activeChangeableSection, activeTabSection);

    }

    private boolean isStarted() {
        return mainSectionModel.startButtonProperty().get() || injectorSectionPwrState.powerButtonProperty().get();
    }
}