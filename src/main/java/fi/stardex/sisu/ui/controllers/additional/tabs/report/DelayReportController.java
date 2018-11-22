package fi.stardex.sisu.ui.controllers.additional.tabs.report;

import fi.stardex.sisu.model.DelayReportModel;
import fi.stardex.sisu.model.DelayReportModel.DelayResult;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;
import java.util.*;

public class DelayReportController {

    @FXML
    private TableColumn deleteColumn;
    @FXML
    private TableView<DelayResult> delayTableView;
    @FXML
    private TableColumn<DelayResult, String> parameterColumn;
    @FXML
    private TableColumn<DelayResult,String> unitsColumn;
    @FXML
    private TableColumn<DelayResult,String> channel1Column;
    @FXML
    private TableColumn<DelayResult,String> channel2Column;
    @FXML
    private TableColumn<DelayResult,String> channel3Column;
    @FXML
    private TableColumn<DelayResult,String> channel4Column;

    private ObservableList<DelayResult> delayResultSource;

    private BooleanProperty newResultFlag;

    private DelayReportModel delayReportModel;

    private I18N i18N;

    public void setDelayReportModel(DelayReportModel delayReportModel) {
        this.delayReportModel = delayReportModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init(){
        delayResultSource = FXCollections.observableArrayList();
        parameterColumn.setCellValueFactory(c -> c.getValue().testProperty());
        unitsColumn.setCellValueFactory(c->c.getValue().unitsProperty());
        channel1Column.setCellValueFactory(c->c.getValue().channel_1Property());
        channel2Column.setCellValueFactory(c->c.getValue().channel_2Property());
        channel3Column.setCellValueFactory(c->c.getValue().channel_3Property());
        channel4Column.setCellValueFactory(c->c.getValue().channel_4Property());
        delayTableView.getSelectionModel().setCellSelectionEnabled(true);
        newResultFlag = delayReportModel.newResultAddedProperty();
        bindingI18N();

        newResultFlag.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                newResultFlag.setValue(false);
                delayResultSource.clear();
                delayResultSource.addAll(delayReportModel.getResultObservableMap().values());
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
