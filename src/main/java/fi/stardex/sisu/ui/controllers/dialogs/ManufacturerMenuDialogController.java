package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class ManufacturerMenuDialogController {

    private State currentState;

    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTF;
    @FXML
    private Button applyBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label notUniqueLabel;

    @Autowired
    private ListView<Manufacturer> manufacturerList;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    private Stage stage;

    @PostConstruct
    private void init() {
        applyBtn.setOnMouseClicked(event -> {
            switch (currentState) {
                case NEW:
                    create();
                    break;
                case EDIT:
                    edit();
                    break;
                case DELETE:
                    delete();
                    break;
            }
        });
        cancelBtn.setOnMouseClicked(event -> stage.close());
    }

    private void create() {
        Manufacturer newManufacturer = new Manufacturer();
        newManufacturer.setManufacturer(nameTF.getText());
        newManufacturer.setCustom(true);
        if (manufacturerList.getItems().contains(newManufacturer)) {
            notUniqueLabel.setVisible(true);
        } else {
            notUniqueLabel.setVisible(false);
            manufacturerRepository.save(newManufacturer);
            manufacturerList.getItems().add(newManufacturer);
            stage.close();
        }
    }

    //TODO Edit manufacturer and all children injectors
    private void edit() {
        stage.close();
    }

    //TODO Delete manufacturer and children injectors
    private void delete() {
        Manufacturer manufacturer = manufacturerList.getSelectionModel().getSelectedItem();
        manufacturerList.getItems().remove(manufacturer);
        manufacturerRepository.delete(manufacturer);
        stage.close();
    }

    public void setNew() {
        nameTF.setDisable(false);
        nameLabel.setVisible(true);
        nameTF.setText("");
        currentState = State.NEW;
    }

    public void setEdit() {
        nameTF.setDisable(false);
        nameLabel.setVisible(true);
        nameTF.setText("");
        currentState = State.EDIT;
    }

    public void setDelete() {
        nameTF.setDisable(true);
        nameLabel.setVisible(false);
        nameTF.setText(manufacturerList.getSelectionModel().getSelectedItem().getManufacturer());
        currentState = State.DELETE;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private enum State {
        NEW, EDIT, DELETE
    }
}
