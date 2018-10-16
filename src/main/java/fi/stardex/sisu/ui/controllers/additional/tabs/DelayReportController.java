package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DelayReportController {

    @FXML
    private TableView<DelayReportTableLine> delayTableView;
    @FXML
    private TableColumn<DelayReportTableLine, String> parameterColumn;
    @FXML
    private TableColumn<DelayReportTableLine,String> unitsColumn;
    @FXML
    private TableColumn<DelayReportTableLine,String> channel1Column;
    @FXML
    private TableColumn<DelayReportTableLine,String> channel2Column;
    @FXML
    private TableColumn<DelayReportTableLine,String> channel3Column;
    @FXML
    private TableColumn<DelayReportTableLine,String> channel4Column;

    private ObservableList<DelayReportTableLine> delayResultSource;

    private I18N i18N;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init(){
        delayResultSource = FXCollections.observableArrayList();
        parameterColumn.setCellValueFactory(c->c.getValue().test);
        unitsColumn.setCellValueFactory(c->c.getValue().units);
        channel1Column.setCellValueFactory(c->c.getValue().channel_1);
        channel2Column.setCellValueFactory(c->c.getValue().channel_2);
        channel3Column.setCellValueFactory(c->c.getValue().channel_3);
        channel4Column.setCellValueFactory(c->c.getValue().channel_4);
        delayTableView.getSelectionModel().setCellSelectionEnabled(true);
        bindingI18N();

    }

    public void showResults(Collection<DelayReportTableLine> setOfTableLines){
        delayResultSource.clear();
        delayResultSource.addAll(setOfTableLines);
        delayTableView.setItems(delayResultSource);
    }

    public void clearTable(){
        delayResultSource.clear();
        delayTableView.refresh();
    }



    public static class DelayReportTableLine{

        StringProperty test;
        StringProperty units;
        StringProperty channel_1;
        StringProperty channel_2;
        StringProperty channel_3;
        StringProperty channel_4;

        private List<String> parameterValues;

        public DelayReportTableLine(String test){
            this.test = new SimpleStringProperty(test);
            this.units = new SimpleStringProperty("\u03BCs");
            parameterValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-"));
            setParameterValues();
        }

        public void setParameterValue(int number, String value) {
            if(value.equals("")){
                value = "Incorrect result!";
            }
            parameterValues.set(number - 1, value);
            setParameterValues();
        }

        private void setParameterValues() {
            channel_1 = new SimpleStringProperty(parameterValues.get(0));
            channel_2 = new SimpleStringProperty(parameterValues.get(1));
            channel_3 = new SimpleStringProperty(parameterValues.get(2));
            channel_4 = new SimpleStringProperty(parameterValues.get(3));
        }
    }
    private void bindingI18N() {
        parameterColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.testName"));
        unitsColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.units"));
    }
}
