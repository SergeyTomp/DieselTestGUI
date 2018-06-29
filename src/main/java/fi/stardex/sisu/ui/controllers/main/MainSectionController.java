package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.view.GUIType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MainSectionController {
    private List<String> versions = new LinkedList<>();

    {
        versions.add("CR Injectors");
        versions.add("CR Pumps");
        versions.add("UIS");
    }


    @FXML
    private ComboBox<String> versionComboBox;

    @FXML
    private ListView<Manufacturer> manufacturerListView;
    @FXML
    private TextField searchModelTF;
    @FXML
    private ListView<Model> modelListView;

    @Autowired
    private CurrentManufacturerObtainer currentManufacturerObtainer;
    @Autowired
    private ApplicationConfigHandler applicationConfigHandler;
    @Autowired
    private ApplicationAppearanceChanger applicationAppearanceChanger;
    @Autowired
    private ViewHolder manufacturerMenuDialog;

    private Stage manufacturerDialogStage;

    @PostConstruct
    private void init() {
        versionComboBox.getItems().addAll(versions);

        versionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            switch (newValue) {
                case "UIS":
                    applicationAppearanceChanger.changeToUIS();
                    break;
                case "CR Injectors":
                    applicationAppearanceChanger.changeToCRInj();
                    break;
                case "CR Pumps":
                    applicationAppearanceChanger.changeToCRPump();
                    break;
            }
        });


        switch (GUIType.getByString(applicationConfigHandler.get("GUI_Type"))) {
            case UIS:
                versionComboBox.getSelectionModel().select("UIS");
                break;
            case CR_Inj:
                versionComboBox.getSelectionModel().select("CR Injectors");
                break;
            case CR_Pump:
                versionComboBox.getSelectionModel().select("CR Pumps");
        }

        ContextMenu manufacturerMenu = new ContextMenu();
        MenuItem newManufacturer = new MenuItem("New");
        newManufacturer.setOnAction(new ManufacturerMenuEventHandler("New manufacturer", ManufacturerMenuDialogController::setNew));
        MenuItem editManufacturer = new MenuItem("Edit");
        editManufacturer.setOnAction(new ManufacturerMenuEventHandler("Edit manufacturer.", ManufacturerMenuDialogController::setEdit));
        MenuItem deleteManufacturer = new MenuItem("Delete");
        deleteManufacturer.setOnAction(new ManufacturerMenuEventHandler("Delete manufacturer", ManufacturerMenuDialogController::setDelete));

        manufacturerListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                manufacturerMenu.getItems().clear();
                manufacturerMenu.getItems().add(newManufacturer);
                if(currentManufacturerObtainer.getCurrentManufacturer().isCustom())
                    manufacturerMenu.getItems().addAll(editManufacturer, deleteManufacturer);
                manufacturerMenu.show(manufacturerListView, event.getScreenX(), event.getScreenY());
            } else
                manufacturerMenu.hide();
        });

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            ObservableList<Model> observableList = FXCollections.observableList(new ArrayList<>());
            currentManufacturerObtainer.setDefaultManufacturer(newValue);
            switch (GUIType.getCurrentType()) {
                case CR_Inj:
                    //TODO
                    System.err.println("IN DEVELOPMENT");
                    break;
                case CR_Pump:
                    //TODO
                    System.err.println("IN DEVELOPMENT");
                    break;
                case UIS:
                    //TODO
                    System.err.println("IN DEVELOPMENT");
                    break;
            }

        }));

    }

    public ListView<Manufacturer> getManufacturerListView() {
        return manufacturerListView;
    }

    private class ManufacturerMenuEventHandler implements EventHandler<ActionEvent> {
        private String title;
        private Consumer<ManufacturerMenuDialogController> dialogType;

        public ManufacturerMenuEventHandler(String title, Consumer<ManufacturerMenuDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            if(manufacturerDialogStage == null) {
                manufacturerDialogStage = new Stage();
                manufacturerDialogStage.setScene(new Scene(manufacturerMenuDialog.getView(), 200, 130));
                manufacturerDialogStage.setResizable(false);
                manufacturerDialogStage.initModality(Modality.APPLICATION_MODAL);
//                manufacturerDialogStage.initStyle(StageStyle.UNDECORATED);
                ((ManufacturerMenuDialogController) manufacturerMenuDialog.getController()).setStage(manufacturerDialogStage);
            }
            System.err.println(title);
            manufacturerDialogStage.setTitle(title);
            dialogType.accept((ManufacturerMenuDialogController) manufacturerMenuDialog.getController());
            manufacturerDialogStage.show();
        }
    }
}
