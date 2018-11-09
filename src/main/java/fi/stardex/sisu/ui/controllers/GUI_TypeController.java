package fi.stardex.sisu.ui.controllers;

import fi.stardex.sisu.states.DimasGUIEditionState;
import fi.stardex.sisu.model.ManufacturerPumpModel;
import fi.stardex.sisu.ui.controllers.additional.TabSectionController;
import fi.stardex.sisu.ui.controllers.pumps.PumpTabSectionController;
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
        CR_Inj, CR_Pump, UIS
    }

    @FXML
    private ComboBox<GUIType> gui_typeComboBox;

    private Preferences rootPrefs;

    private Parent mainSection;

    private Parent mainSectionPumps;

    private Parent crSection;

    private Parent pumpSection;

    private Parent uisSection;

    private Parent tabSection;

    private Parent tabSectionPumps;

    private GridPane mainSectionGridPane;

    private GridPane additionalSectionGridPane;

    private Parent activeMainSection;

    private Parent activeChangeableSection;

    private Parent activeTabSection;

    private Parent settings;

    private Parent connection;

    private DimasGUIEditionState dimasGUIEditionState;

    private TabSectionController tabSectionController;

    private PumpTabSectionController pumpTabSectionController;

    private ManufacturerPumpModel manufacturerPumpModel;

    public ComboBox<GUIType> getGui_typeComboBox() {
        return gui_typeComboBox;
    }

    public void setManufacturerPumpModel(ManufacturerPumpModel manufacturerPumpModel) {
        this.manufacturerPumpModel = manufacturerPumpModel;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    public void setMainSection(Parent mainSection) {
        this.mainSection = mainSection;
    }

    public void setCRSection(Parent crSection) {
        this.crSection = crSection;
    }

    public void setUISSection(Parent uisSection) {
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

    public void setMainSectionPumps(Parent mainSectionPumps) {
        this.mainSectionPumps = mainSectionPumps;
    }

    public void setPumpSection(Parent pumpSection) {
        this.pumpSection = pumpSection;
    }

    public void setTabSectionPumps(Parent tabSectionPumps) {
        this.tabSectionPumps = tabSectionPumps;
    }

    public void setTabSectionController(TabSectionController tabSectionController) {
        this.tabSectionController = tabSectionController;
    }

    public void setPumpTabSectionController(PumpTabSectionController pumpTabSectionController) {
        this.pumpTabSectionController = pumpTabSectionController;
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

    @PostConstruct
    private void init() {

        gui_typeComboBox.getItems().addAll(GUIType.values());

        gui_typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {

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
            }

            rootPrefs.put("GUI_Type", newValue.toString());

        });

        GUIType currentGUIType = GUIType.valueOf(rootPrefs.get("GUI_Type", GUIType.CR_Inj.toString()));

        gui_typeComboBox.getSelectionModel().select(currentGUIType);
        gui_typeComboBox.visibleProperty().bind(dimasGUIEditionState.isDimasGuiEditionProperty().not());

    }

    private void changeToCRInj() {

        clearSections();

        mainSectionGridPane.add(mainSection, 0, 1);
        additionalSectionGridPane.add(crSection, 0, 1);
        tabSectionController.getSettingsGridPane().add(connection, 0, 0);
        settings.getChildrenUnmodifiable().stream().filter(node -> node.getId().equals("isDIMASCheckBox")).forEach(node -> node.setVisible(true));
        tabSectionController.getSettingsGridPane().add(settings, 1, 0);
        additionalSectionGridPane.add(tabSection, 0, 2);

        activeMainSection = mainSection;
        activeChangeableSection = crSection;
        activeTabSection = tabSection;

        logger.info("Changed to CrInj");

    }

    private void changeToCRPump() {

        clearSections();

        mainSectionGridPane.add(mainSectionPumps, 0, 1);
        additionalSectionGridPane.add(pumpSection, 0, 1);
        pumpTabSectionController.getSettingsGridPane().add(connection, 0, 0);
        settings.getChildrenUnmodifiable().stream().filter(node -> node.getId().equals("isDIMASCheckBox")).forEach(node -> node.setVisible(false));
        pumpTabSectionController.getSettingsGridPane().add(settings, 1, 0);

        additionalSectionGridPane.add(tabSectionPumps, 0, 2);

        activeMainSection = mainSectionPumps;
        activeChangeableSection = pumpSection;
        activeTabSection = tabSectionPumps;

        manufacturerPumpModel.initManufacturerPumpList();

        logger.info("Changed to CrPump");

    }
    //TODO do not forget to hide/show DIMASCheckBox after implementation
    private void changeToUIS() {

        clearSections();

        additionalSectionGridPane.add(uisSection, 0, 1);
        activeMainSection = null;
        activeChangeableSection = uisSection;
        activeTabSection = null;

        logger.info("Changed to UIS");

    }

    private void clearSections() {

        mainSectionGridPane.getChildren().remove(activeMainSection);
        additionalSectionGridPane.getChildren().removeAll(activeChangeableSection, activeTabSection);

    }

}

