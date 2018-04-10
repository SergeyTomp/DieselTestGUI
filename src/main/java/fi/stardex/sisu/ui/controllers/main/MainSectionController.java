package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.view.GUIType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

public class MainSectionController {

    private List<String> versions = new LinkedList<>();

    {
        versions.add("CR");
        versions.add("UIS");
    }

    @FXML
    private ComboBox<String> versionComboBox;
    @FXML
    private RadioButton pumpRB;
    @FXML
    private RadioButton injRB;

    @Autowired
    private ApplicationConfigHandler applicationConfigHandler;
    @Autowired
    private ApplicationAppearanceChanger applicationAppearanceChanger;

    @PostConstruct
    private void init() {
        versionComboBox.getItems().addAll(versions);

        GUIType gui_type = GUIType.getByString(applicationConfigHandler.get("GUI_Type"));
        changeGUIType(gui_type);
    }

    private void changeGUIType(GUIType gui_type) {
        switch (gui_type) {
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
    }

    private void changeToCRInj() {
        versionComboBox.getSelectionModel().select("CR");
        injRB.setSelected(true);
    }

    private void changeToCRPump() {
        versionComboBox.getSelectionModel().select("CR");
        pumpRB.setSelected(true);
    }

    private void changeToUIS() {
        versionComboBox.getSelectionModel().select("UIS");
        injRB.setSelected(true);
        pumpRB.setDisable(true);
    }
}
