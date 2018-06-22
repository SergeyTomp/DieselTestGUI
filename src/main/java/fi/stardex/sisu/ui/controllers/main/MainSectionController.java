package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.Model;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.view.GUIType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainSectionController {



    //TODO FOR TEST DELETE AFTER
    @FXML
    private TextField scriptTF;
    @FXML
    private Button scriptBtn;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private DataSource dataSource;
    //FINISHTESTBLOCK


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
    @FXML
    private ToggleGroup injectorOrPump;

    @FXML
    private ListView<Manufacturer> manufacturerListView;
    @FXML
    private TextField searchModelTF;
    @FXML
    private ListView<Model> modelListView;

    @Autowired
    private ApplicationConfigHandler applicationConfigHandler;
    @Autowired
    private ApplicationAppearanceChanger applicationAppearanceChanger;

    @PostConstruct
    private void init() {
        versionComboBox.getItems().addAll(versions);

        versionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            switch (newValue) {
                case "UIS":
                    unselectAll();
                    changeToUIS();
                    break;
                case "CR":
                    unselectAll();
                    changeToCR();
                    break;
            }
        });

        injectorOrPump.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (versionComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("CR")) {
                if (injectorOrPump.getSelectedToggle() == injRB) {
                    GUIType.setCurrentType(GUIType.CR_Inj);
                    applicationConfigHandler.put("GUI_Type", "CR_Inj");
                    applicationAppearanceChanger.changeToCRInj();
                } else if (injectorOrPump.getSelectedToggle() == pumpRB) {
                    GUIType.setCurrentType(GUIType.CR_Pump);
                    applicationConfigHandler.put("GUI_Type", "CR_Pump");
                    applicationAppearanceChanger.changeToCRPump();
                }
            }
        });

        switch (GUIType.getByString(applicationConfigHandler.get("GUI_Type"))) {
            case UIS:
                versionComboBox.getSelectionModel().select("UIS");
                break;
            case CR_Inj:
                versionComboBox.getSelectionModel().select("CR");
                break;
            case CR_Pump:
                versionComboBox.getSelectionModel().select("CR");
        }


        manufacturerListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            System.err.println(newValue);
            ObservableList<Model> observableList = FXCollections.observableList(new ArrayList<>());
            switch (GUIType.getCurrentType()) {
                case CR_Inj:
                    System.err.println(newValue.getInjectorsCR());
                    observableList.setAll(newValue.getInjectorsCR());
                    modelListView.setItems(new FilteredList<>(observableList));
                    break;
                case CR_Pump:
                    break;
                case UIS:
                    break;
            }

        }));

    }

    private void changeToUIS() {
        GUIType.setCurrentType(GUIType.UIS);
        applicationConfigHandler.put("GUI_Type", "UIS");
        applicationAppearanceChanger.changeToUIS();
        injRB.setSelected(true);
        pumpRB.setDisable(true);
    }

    private void changeToCR() {
        injRB.setSelected(true);
        pumpRB.setDisable(false);
    }

    private void unselectAll() {
        injRB.setSelected(false);
        pumpRB.setSelected(false);
    }

    public ListView<Manufacturer> getManufacturerListView() {
        return manufacturerListView;
    }
}
