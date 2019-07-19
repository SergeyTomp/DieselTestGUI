package fi.stardex.sisu.ui.controllers.pumps;

import fi.stardex.sisu.ui.controllers.cr.tabs.info.DensoController;
import fi.stardex.sisu.util.obtainers.CurrentPumpObtainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.CopyUtils.copyToClipBoard;

public class PumpInfoController {

    @FXML private TableView infoTableView;
    @FXML private TableColumn<InfoTableLine, String> parameterColumn;
    @FXML private TableColumn<InfoTableLine, String> dataColumn;

    private MenuItem item;
    private ContextMenu menu;
    private KeyCodeCombination copyKeyCodeCombination;
    private StringBuilder sb;
    private ObservableList<InfoTableLine> infoSource;
    private String pumpCode;
    private boolean notEmpty;
    private Logger logger = LoggerFactory.getLogger(DensoController.class);

    @PostConstruct
    private void init(){
        infoSource = FXCollections.observableArrayList();
        sb = new StringBuilder();
        infoTableView.getSelectionModel().setCellSelectionEnabled(true);
        infoTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        parameterColumn.setCellValueFactory(c -> c.getValue().parameter);
        dataColumn.setCellValueFactory(c -> c.getValue().parameter);

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

    private void fillTableView(){
        infoSource.clear();
        String pumpCode = CurrentPumpObtainer.getPump().getPumpCode();
        notEmpty = false;
    }

    void switchTo(){
        infoSource.clear();
        pumpCode = CurrentPumpObtainer.getPump().getPumpCode();

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

}
