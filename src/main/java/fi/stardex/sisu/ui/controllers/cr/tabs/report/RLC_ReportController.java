package fi.stardex.sisu.ui.controllers.cr.tabs.report;

import fi.stardex.sisu.model.cr.RLC_ReportModel;
import fi.stardex.sisu.model.cr.RLC_ReportModel.RlcResult;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;

public class RLC_ReportController {
    @FXML
    private TableView<RlcResult> rlcTableView;
    @FXML
    private TableColumn<RlcResult, String> parameterColumn;
    @FXML
    private TableColumn<RlcResult,String> unitsColumn;
    @FXML
    private TableColumn<RlcResult,String> channel1Column;
    @FXML
    private TableColumn<RlcResult,String> channel2Column;
    @FXML
    private TableColumn<RlcResult,String> channel3Column;
    @FXML
    private TableColumn<RlcResult,String> channel4Column;

    private BooleanProperty newResultFlag;

    private RLC_ReportModel rlc_reportModel;

    private ObservableList<RlcResult> RLCresultSource;

    private I18N i18N;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRlc_reportModel(RLC_ReportModel rlc_reportModel) {
        this.rlc_reportModel = rlc_reportModel;
    }

    @PostConstruct
    private void init(){
        RLCresultSource = FXCollections.observableArrayList();
        newResultFlag = rlc_reportModel.newRlcAddedProperty();
        parameterColumn.setCellValueFactory(c->c.getValue().parameterProperty());
        unitsColumn.setCellValueFactory(c->c.getValue().unitsProperty());
        channel1Column.setCellValueFactory(c -> c.getValue().channel_1Property());
        channel2Column.setCellValueFactory(c->c.getValue().channel_2Property());
        channel3Column.setCellValueFactory(c->c.getValue().channel_3Property());
        channel4Column.setCellValueFactory(c->c.getValue().channel_4Property());
        rlcTableView.getSelectionModel().setCellSelectionEnabled(true);
//        mapOfTableLines = new HashMap<>();
        bindingI18N();
        newResultFlag.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                newResultFlag.setValue(false);
                RLCresultSource.clear();
                RLCresultSource.addAll(rlc_reportModel.getResultObservableMap().values());
                rlcTableView.setItems(RLCresultSource);
                rlcTableView.refresh();
            }
        });
    }

    private void bindingI18N() {
        parameterColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.parameterName"));
        unitsColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.units"));
    }
}
