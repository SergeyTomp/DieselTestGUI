package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.dialogs.ManufacturerMenuDialogController;
import fi.stardex.sisu.ui.controllers.dialogs.NewEditInjectorDialogController;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.view.GUIType;
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

    @FXML
    private ToggleGroup baseType;
    @FXML
    private RadioButton defaultRB;
    @FXML
    private RadioButton customRB;

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
    @Autowired
    private ViewHolder newEditInjectorDialog;

    @Autowired
    private InjectorsRepository injectorsRepository;

    private Stage manufacturerDialogStage;
    private Stage modelDialogStage;

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

        initManufacturerContextMenu();
        initModelContextMenu();

        manufacturerListView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            currentManufacturerObtainer.setCurrentManufacturer(newValue);
            switch (GUIType.getCurrentType()) {
                case CR_Inj:
                    modelListView.getItems().setAll(injectorsRepository.findByManufacturerAndIsCustom(newValue, !defaultRB.isSelected()));
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

        baseType.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            modelListView.getItems().setAll(injectorsRepository.findByManufacturerAndIsCustom(currentManufacturerObtainer.getCurrentManufacturer(),
                    !newValue.equals(defaultRB)));
        }));

    }

    public ListView<Manufacturer> getManufacturerListView() {
        return manufacturerListView;
    }

    private void initManufacturerContextMenu() {
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
    }

    private void initModelContextMenu() {
        ContextMenu modelMenu = new ContextMenu();
        MenuItem newModel = new MenuItem("New");
        newModel.setOnAction(new ModelMenuEventHandler("New injector", NewEditInjectorDialogController::setNew));
        MenuItem copyModel = new MenuItem("Copy");

        //TODO
        modelListView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.SECONDARY) {
                modelMenu.getItems().clear();
                if(defaultRB.isSelected()) {
                    modelMenu.getItems().add(copyModel);
                } else {
                    modelMenu.getItems().add(newModel);
                    if(modelListView.getSelectionModel().getSelectedItem() != null)
                        modelMenu.getItems().addAll(copyModel);
                }
                modelMenu.show(modelListView, event.getScreenX(), event.getScreenY());
            } else
                modelMenu.hide();
        });
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
            manufacturerDialogStage.setTitle(title);
            dialogType.accept((ManufacturerMenuDialogController) manufacturerMenuDialog.getController());
            manufacturerDialogStage.show();
        }
    }

    private class ModelMenuEventHandler implements EventHandler<ActionEvent> {
        private String title;
        private Consumer<NewEditInjectorDialogController> dialogType;

        public ModelMenuEventHandler(String title, Consumer<NewEditInjectorDialogController> dialogType) {
            this.title = title;
            this.dialogType = dialogType;
        }

        @Override
        public void handle(ActionEvent event) {
            if(modelDialogStage == null) {
                modelDialogStage = new Stage();
                modelDialogStage.setScene(new Scene(newEditInjectorDialog.getView(), 600, 400));
                modelDialogStage.setResizable(false);
                modelDialogStage.initModality(Modality.APPLICATION_MODAL);
//                manufacturerDialogStage.initStyle(StageStyle.UNDECORATED);
                ((NewEditInjectorDialogController) newEditInjectorDialog.getController()).setStage(modelDialogStage);
            }
            modelDialogStage.setTitle(title);
            dialogType.accept((NewEditInjectorDialogController) newEditInjectorDialog.getController());
            modelDialogStage.show();
        }
    }
}
