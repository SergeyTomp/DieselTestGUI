package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.persistence.orm.bosch_info.Applications;
import fi.stardex.sisu.persistence.orm.bosch_info.Nozzles;
import fi.stardex.sisu.persistence.orm.bosch_info.Parameters;
import fi.stardex.sisu.persistence.orm.bosch_info.Valves;
import fi.stardex.sisu.persistence.repos.cr.BoschRepository;
import fi.stardex.sisu.util.enums.BoschImg;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static fi.stardex.sisu.util.VisualUtils.copyToClipBoard;
import static fi.stardex.sisu.util.enums.BoschImg.*;


public class BoschController {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private GridPane infoGridPane;
    @FXML
    private Button infoButton;
    @FXML
    private TableView<InfoTableLine> infoTableView;
    @FXML
    private TableColumn<InfoTableLine, String> parameterColumn;
    @FXML
    private TableColumn<InfoTableLine, String> dataColumn;
    private String injectorCode;
    private Stage addInfoImageStage;
    private ImageView addInfoImageView;
    private MenuItem item;
    private ContextMenu menu;
    private KeyCodeCombination copyKeyCodeCombination;
    private BoschRepository boschRepository;
    private ObservableList<InfoTableLine> infoSource;
    private String SERIAL_NUMBER = "Serial Number :";
    private String ADDITIONAL_SERIAL_NUMBER = "Additional Serial Number :";
    private String VALVE = "Valve :";
    private String NOZZLE = "Nozzle :";
    private String APPLICATION = "Application :";
    private String ARMATURE_STROKE_ELECTRICAL = "Armature stroke, electrical :";
    private String ARMATURE_STROKE = "Armature stroke :";
    private String NEEDLE_LIFT = "Needle lift :";
    private String RERIDUAL_AIR_GAP = "Residual air gap :";
    private String EXCESS_LIFT ="Excess lift :";
    private StringBuilder sb;
    private Logger logger = LoggerFactory.getLogger(BoschController.class);

    public void setBoschRepository(BoschRepository boschRepository) {
        this.boschRepository = boschRepository;
    }

    @PostConstruct
    private void init(){
        infoSource = FXCollections.observableArrayList();
        sb = new StringBuilder();
        infoTableView.getSelectionModel().setCellSelectionEnabled(true);
        infoTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        addInfoImageView = new ImageView();
        infoButton.setVisible(false);
        infoButton.setOnAction(this::addInfoButtonEvent);
        parameterColumn.setCellValueFactory(c-> c.getValue().parameter);
        dataColumn.setCellValueFactory(c-> c.getValue().data);
        item = new MenuItem("Copy");
        menu = new ContextMenu();
        menu.getItems().add(item);
        infoTableView.setContextMenu(menu);
        copyKeyCodeCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);


        item.setOnAction(event -> copyToClipBoard(infoTableView));
        infoTableView.setOnKeyPressed(event -> {
            if (copyKeyCodeCombination.match(event) && event.getSource() instanceof TableView) {
                copyToClipBoard(infoTableView);

            }
        });
    }

    void switchTo(){
        infoSource.clear();
        infoButton.setVisible(false);
        injectorCode = CurrentInjectorObtainer.getInjector().getInjectorCode();

        boschRepository.findById(injectorCode).ifPresent(parameters -> fillInformationTab(parameters));

        logger.info("Change to Bosch Label");
    }

    private class InfoTableLine{
        StringProperty parameter;
        StringProperty data;

        public InfoTableLine(String parameter, String data) {
            this.parameter = new SimpleStringProperty(parameter);
            this.data = new SimpleStringProperty(data);
        }
    }

    private void addInfoButtonEvent(ActionEvent event) {
        if (addInfoImageStage == null) {
            addInfoImageStage = new Stage();
            addInfoImageStage.setTitle("Injector info image");
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(addInfoImageView);
            addInfoImageStage.setScene(new Scene(stackPane, addInfoImageView.getImage().getWidth(), addInfoImageView.getImage().getHeight()));
            addInfoImageStage.setResizable(false);
            addInfoImageStage.initModality(Modality.WINDOW_MODAL);
            addInfoImageStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            addInfoImageStage.show();
        } else {
            addInfoImageStage.show();
        }
    }

    private void fillInformationTab(Parameters parameters){
        List<Valves> valvesList = parameters.getValves();
        List<Nozzles> nosslesList = parameters.getNossles();
        List<Applications> applicationsList = parameters.getApplications();

        BoschImg image = parameters.getImage();
        if (image != null) {
            switch (image) {
                case CRI10:
                    addInfoImageView.setImage(new Image(CRI10.getUrl()));
                    infoButton.setVisible(true);
                    break;
                case CRI20:
                    addInfoImageView.setImage(new Image(CRI20.getUrl()));
                    infoButton.setVisible(true);
                    break;
                case CRI21:
                    addInfoImageView.setImage(new Image(CRI21.getUrl()));
                    infoButton.setVisible(true);
                    break;
                case CRI22:
                    addInfoImageView.setImage(new Image(CRI22.getUrl()));
                    infoButton.setVisible(true);
                    break;
                case CRIN1:
                    addInfoImageView.setImage(new Image(CRIN1.getUrl()));
                    infoButton.setVisible(true);
                    break;
                case CRIN2:
                case CRIN2A:
                    addInfoImageView.setImage(new Image(CRIN2.getUrl()));
                    infoButton.setVisible(true);
                    break;
                case CRIN3:
                case CRIN3L:
                    addInfoImageView.setImage(new Image(CRIN3.getUrl()));
                    infoButton.setDisable(false);
                    break;
                default:
                    break;
            }
        }
        infoSource.add(new InfoTableLine(SERIAL_NUMBER, injectorCode));
        infoSource.add(new InfoTableLine(ADDITIONAL_SERIAL_NUMBER, parameters.getAddidionalSerialNumber()));

        sb.setLength(0);
        Iterator<Valves> vlit = valvesList.iterator();
        if (vlit.hasNext()) {
            sb.append(vlit.next().getValve());
            while (vlit.hasNext()) {
                sb.append("\n").append(vlit.next().getValve());
            }
        } else sb.append("-");
        infoSource.add(new InfoTableLine(VALVE, sb.toString()));

        sb.setLength(0);
        Iterator<Nozzles> nlit = nosslesList.iterator();
        if (nlit.hasNext()) {
            sb.append(nlit.next().getNozzle());
            while (nlit.hasNext()) {
                sb.append("\n").append(nlit.next().getNozzle());
            }
        } else sb.append("-");
        infoSource.add(new InfoTableLine(NOZZLE, sb.toString()));

        sb.setLength(0);
        Iterator<Applications> alit = applicationsList.iterator();
        if (alit.hasNext()) {
            sb.append(alit.next().getApplication());
            while (alit.hasNext()) {
                sb.append("\n").append(alit.next().getApplication());
            }
        } else sb.append("-");
        infoSource.add(new InfoTableLine(APPLICATION, sb.toString()));

        infoSource.add(new InfoTableLine(ARMATURE_STROKE_ELECTRICAL, parameters.getAHE()));
        infoSource.add(new InfoTableLine(ARMATURE_STROKE, parameters.getAH()));
        infoSource.add(new InfoTableLine(NEEDLE_LIFT, parameters.getDNH()));
        infoSource.add(new InfoTableLine(RERIDUAL_AIR_GAP, parameters.getRLS()));
        infoSource.add(new InfoTableLine(EXCESS_LIFT, parameters.getUEH()));

        infoTableView.setItems(infoSource);

    }

}