package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    private ListView<Manufacturer> manufacturerList;

    private ManufacturerRepository manufacturerRepository;

    private Stage stage;

    public void setManufacturerList(ListView<Manufacturer> manufacturerList) {
        this.manufacturerList = manufacturerList;
    }

    public void setManufacturerRepository(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    @PostConstruct
    private void init() {
        applyBtn.setOnMouseClicked(event -> {
            switch (currentState) {
                case NEW:
                    create();
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
        newManufacturer.setManufacturerName(nameTF.getText());
        newManufacturer.setCustom(true);
        if (manufacturerList.getItems().contains(newManufacturer)) {
            notUniqueLabel.setVisible(true);
        } else {
            notUniqueLabel.setVisible(false);
            manufacturerRepository.save(newManufacturer);
            manufacturerList.getItems().add(newManufacturer);
            manufacturerList.getSelectionModel().select(newManufacturer);
            stage.close();
        }
    }

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


    public void setDelete() {
        nameTF.setDisable(true);
        nameLabel.setVisible(false);
        nameTF.setText(manufacturerList.getSelectionModel().getSelectedItem().getManufacturerName());
        currentState = State.DELETE;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private enum State {
        NEW, DELETE
    }
}
