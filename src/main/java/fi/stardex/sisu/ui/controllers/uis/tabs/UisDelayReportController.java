package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.UisDelayModel;
import fi.stardex.sisu.model.uis.UisDelayModel.UisDelayResult;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;

public class UisDelayReportController {

    @FXML private TableView<UisDelayResult> delayTableView;
    @FXML private TableColumn<UisDelayResult, String> parameterColumn;
    @FXML private TableColumn<UisDelayResult, String> unitsColumn;
    @FXML private TableColumn<UisDelayResult, String> channel1Column;
    @FXML private TableColumn<UisDelayResult, String> channel2Column;
    @FXML private TableColumn<UisDelayResult, String> channel3Column;
    @FXML private TableColumn<UisDelayResult, String> channel4Column;
    @FXML private TableColumn<UisDelayResult, String> channel5Column;
    @FXML private TableColumn<UisDelayResult, String> channel6Column;
    @FXML private TableColumn<UisDelayResult, String> channel7Column;
    @FXML private TableColumn<UisDelayResult, String> channel8Column;

    private ObservableList<UisDelayResult> delayResultSource;
    private UisDelayModel uisDelayModel;
    private I18N i18N;

    public void setUisDelayModel(UisDelayModel uisDelayModel) {
        this.uisDelayModel = uisDelayModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init(){

        delayResultSource = FXCollections.observableArrayList();
        parameterColumn.setCellValueFactory(c -> c.getValue().testProperty());
        unitsColumn.setCellValueFactory(c->c.getValue().unitsProperty());
        channel1Column.setCellValueFactory(c -> c.getValue().channel_1Property());
        channel2Column.setCellValueFactory(c -> c.getValue().channel_2Property());
        channel3Column.setCellValueFactory(c->c.getValue().channel_3Property());
        channel4Column.setCellValueFactory(c->c.getValue().channel_4Property());
        channel5Column.setCellValueFactory(c->c.getValue().channel_5Property());
        channel6Column.setCellValueFactory(c->c.getValue().channel_6Property());
        channel7Column.setCellValueFactory(c->c.getValue().channel_7Property());
        channel8Column.setCellValueFactory(c->c.getValue().channel_8Property());
        delayTableView.getSelectionModel().setCellSelectionEnabled(true);

        bindingI18N();

        uisDelayModel.newDelayAddedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                delayResultSource.clear();
                delayResultSource.addAll(uisDelayModel.getResultObservableMap().values());
                delayTableView.setItems(delayResultSource);
                delayTableView.refresh();
            }
        });
    }

    private void bindingI18N() {
        parameterColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.testName"));
        unitsColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.units"));
    }
}
