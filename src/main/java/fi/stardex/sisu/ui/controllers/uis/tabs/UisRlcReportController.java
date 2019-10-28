package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.UisRlcModel;
import fi.stardex.sisu.model.uis.UisRlcModel.UisRlcResult;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;

public class UisRlcReportController {

    @FXML private TableView<UisRlcResult> rlcTableView;
    @FXML private TableColumn<UisRlcResult, String> parameterColumn;
    @FXML private TableColumn<UisRlcResult, String> unitsColumn;
    @FXML private TableColumn<UisRlcResult, String> channel1Column;
    @FXML private TableColumn<UisRlcResult, String> channel2Column;
    @FXML private TableColumn<UisRlcResult, String> channel3Column;
    @FXML private TableColumn<UisRlcResult, String> channel4Column;
    @FXML private TableColumn<UisRlcResult, String> channel5Column;
    @FXML private TableColumn<UisRlcResult, String> channel6Column;
    @FXML private TableColumn<UisRlcResult, String> channel7Column;
    @FXML private TableColumn<UisRlcResult, String> channel8Column;

    private ObservableList<UisRlcResult> RlcResultSource;
    private UisRlcModel uisRlcModel;
    private I18N i18N;

    public void setUisRlcModel(UisRlcModel uisRlcModel) {
        this.uisRlcModel = uisRlcModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {
        RlcResultSource = FXCollections.observableArrayList();
        parameterColumn.setCellValueFactory(c->c.getValue().parameterProperty());
        unitsColumn.setCellValueFactory(c->c.getValue().unitsProperty());
        channel1Column.setCellValueFactory(c -> c.getValue().channel_1Property());
        channel2Column.setCellValueFactory(c->c.getValue().channel_2Property());
        channel3Column.setCellValueFactory(c->c.getValue().channel_3Property());
        channel4Column.setCellValueFactory(c->c.getValue().channel_4Property());
        channel5Column.setCellValueFactory(c->c.getValue().channel_5Property());
        channel6Column.setCellValueFactory(c->c.getValue().channel_6Property());
        channel7Column.setCellValueFactory(c->c.getValue().channel_7Property());
        channel8Column.setCellValueFactory(c->c.getValue().channel_8Property());
        rlcTableView.getSelectionModel().setCellSelectionEnabled(true);

        bindingI18N();

        uisRlcModel.newRlcAddedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                RlcResultSource.clear();
                RlcResultSource.addAll(uisRlcModel.getResultObservableMap().values());
                rlcTableView.setItems(RlcResultSource);
                rlcTableView.refresh();
            }
        });
    }


    private void bindingI18N() {
        parameterColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.parameterName"));
        unitsColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.units"));
    }
}
