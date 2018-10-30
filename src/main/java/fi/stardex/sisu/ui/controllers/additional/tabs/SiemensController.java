package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.persistence.orm.siemens_info.Cars;
import fi.stardex.sisu.persistence.orm.siemens_info.Engines;
import fi.stardex.sisu.persistence.orm.siemens_info.Reference;
import fi.stardex.sisu.persistence.orm.siemens_info.Spares;
import fi.stardex.sisu.persistence.repos.cr.SiemensReferenceRepository;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static fi.stardex.sisu.util.VisualUtils.copyToClipBoard;

public class SiemensController {
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private TableView<InfoTableLine> infoTableView;
    @FXML
    private TableView<SparesTableLine> sparesTableView;
    @FXML
    private TableColumn <InfoTableLine, String> parameterColumn;
    @FXML
    private TableColumn <InfoTableLine, String> dataColumn;
    @FXML
    private TableColumn<SparesTableLine, String> categoryColumn;
    @FXML
    private TableColumn<SparesTableLine, String> orderNumberColumn;
    @FXML
    private TableColumn<SparesTableLine, String> descriptionColumn;

    private SiemensReferenceRepository siemensReferenceRepository;
    private ObservableList<InfoTableLine> infoSource;
    private ObservableList<SparesTableLine> sparesSource;
    private StringBuilder sb;
    private ContextMenu contextMenu;
    private MenuItem menuItem;
    private ObservableList<TablePosition> positionList;
    private MenuItem infoContextMenuItem;
    private MenuItem sparesContextMenuItem;
    private ContextMenu infoContextMenu;
    private ContextMenu sparesContextMenu;
    private String selectedText;
    private ObservableList<TablePosition> selectedCells;
    private KeyCodeCombination copyKeyCodeCombination;
    private String orderNumber;
    private Engines engine;
    private String ENGINE = "Engine";
    private String ENGINE_TYPE = "Engine type";
    private String USED_IN = "Used in";
    private String ORDER_NUMBER = "Order Number";
    private String DESCRIPTION = "Description";
    private String CROSS_REFERENCE = "Cross Reference";
    private String REMARKS = "Remarks";
    private String NO_INFORMATION = "No information for specified injector.";

    private Logger logger = LoggerFactory.getLogger(BoschController.class);

    public AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    public void setSiemensReferenceRepository(SiemensReferenceRepository siemensReferenceRepository) {
        this.siemensReferenceRepository = siemensReferenceRepository;
    }

    @PostConstruct
    private void init(){
        infoSource = FXCollections.observableArrayList();
        sparesSource = FXCollections.observableArrayList();
        sb = new StringBuilder();
        copyKeyCodeCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);

        dataColumn.setCellFactory(new Callback<TableColumn<InfoTableLine, String>, TableCell<InfoTableLine, String>>() {

            @Override public TableCell<InfoTableLine, String> call(TableColumn<InfoTableLine, String> param) {
                TableCell<InfoTableLine, String> tableCell = new TableCell<InfoTableLine, String>() {
                    @Override protected void updateItem(String item, boolean empty) {
                        if (item == getItem()) return;

                        super.updateItem(item, empty);

                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(null);
                            Label l = new Label(item);
                            l.setWrapText(true);
                            VBox box = new VBox(l);
                            l.heightProperty().addListener((observable,oldValue,newValue)-> {
                                box.setPrefHeight(newValue.doubleValue()+7);
                                Platform.runLater(()->this.getTableRow().requestLayout());
                            });
                            super.setGraphic(box);
                        }
                    }
                };
                return tableCell;
            }
        });

        infoTableView.getSelectionModel().setCellSelectionEnabled(true);
        sparesTableView.getSelectionModel().setCellSelectionEnabled(true);

        infoTableView.setPlaceholder(new Label(NO_INFORMATION));
        sparesTableView.setPlaceholder(new Label(NO_INFORMATION));

        parameterColumn.setCellValueFactory(c->c.getValue().parameter);
        dataColumn.setCellValueFactory(c->c.getValue().data);
        categoryColumn.setCellValueFactory(c -> c.getValue().category);
        orderNumberColumn.setCellValueFactory(c->c.getValue().orderNumber);
        descriptionColumn.setCellValueFactory(c->c.getValue().description);

        infoContextMenuItem = new MenuItem("Copy");
        sparesContextMenuItem = new MenuItem("Copy");

        infoContextMenu = new ContextMenu();
        sparesContextMenu = new ContextMenu();

        infoContextMenu.getItems().add(infoContextMenuItem);
        sparesContextMenu.getItems().add(sparesContextMenuItem);

        infoTableView.setContextMenu(infoContextMenu);
        sparesTableView.setContextMenu(sparesContextMenu);

        infoContextMenu.setOnAction(event -> {
            copyToClipBoard(infoTableView);
        });

        sparesContextMenu.setOnAction(event -> {
            copyToClipBoard(sparesTableView);
        });

        infoTableView.setOnKeyPressed(event -> {
            if (copyKeyCodeCombination.match(event) && event.getSource() instanceof TableView) {
                copyToClipBoard(infoTableView);
            }
        });
        sparesTableView.setOnKeyPressed(event -> {
            if (copyKeyCodeCombination.match(event) && event.getSource() instanceof TableView) {
                copyToClipBoard(sparesTableView);
            }
        });
    }

    void switchTo(){
        infoSource.clear();
        sparesSource.clear();
        sb.setLength(0);
        String injectorCode = CurrentInjectorObtainer.getInjector().getInjectorCode()
                .replaceAll("-", "").replaceAll(" ", "").trim();

        Reference refByInjector = siemensReferenceRepository.findByInjector(injectorCode);
        if(refByInjector != null){

            engine = refByInjector.getEngine();

            if(engine != null){
                List<Cars> carsList = engine.getCarsList();
                List<Spares> sparesList = engine.getSparesList();
                infoSource.add(new InfoTableLine(ENGINE, engine.getEngine()));
                infoSource.add(new InfoTableLine(ENGINE_TYPE, engine.getEngineType()));

                Iterator<Cars> clit = carsList.iterator();
                if(clit.hasNext()){
                    sb.append(clit.next().getCar());
                    while (clit.hasNext()){
                        sb.append(", ").append(clit.next().getCar());
                    }
                }
                infoSource.add(new InfoTableLine(USED_IN, sb.toString()));
                sb.setLength(0);
                sparesList.forEach(sp->sparesSource.add(new SparesTableLine(sp.getCategory(), sp.getOrderNumber(), sp.getDescription())));
            }

            orderNumber = refByInjector.getOrderNumber();

            if(orderNumber != null){
                List<Reference> refListByOrderNumber = siemensReferenceRepository.findByOrderNumber(orderNumber);
                Iterator<Reference> rlit = refListByOrderNumber.iterator();
                if(rlit.hasNext()){
                    sb.append(rlit.next().getInjector());
                    while (rlit.hasNext()){
                        sb.append("\n").append(rlit.next().getInjector());
                    }
                }
                infoSource.add(new InfoTableLine(CROSS_REFERENCE, sb.toString()));
                sb.setLength(0);

                infoSource.add(new InfoTableLine(ORDER_NUMBER, refByInjector.getOrderNumber()));
                infoSource.add(new InfoTableLine(DESCRIPTION, refByInjector.getDescription()));

                infoSource.add(new InfoTableLine(REMARKS, refByInjector.getRemarks()));

                infoTableView.setItems(infoSource);
                sparesTableView.setItems(sparesSource);

                logger.info("Change to Siemens Label");
            }
        }
    }

    private class InfoTableLine{
        StringProperty parameter;
        StringProperty data;

        public InfoTableLine(String parameter, String data) {
            this.parameter = new SimpleStringProperty(parameter);
            this.data = new SimpleStringProperty(data);
        }
    }

    private class SparesTableLine{
        StringProperty category;
        StringProperty orderNumber;
        StringProperty description;

        public SparesTableLine(String category, String orderNumber, String description) {
            this.category = new SimpleStringProperty(category);
            this.orderNumber = new SimpleStringProperty(orderNumber);
            this.description = new SimpleStringProperty(description);
        }
    }
}
