package fi.stardex.sisu.ui.controllers.additional.tabs.report;

import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.PostConstruct;
import java.util.*;

public class RLC_ReportController {
    @FXML
    private TableView<RLCreportTableLine> rlcTableView;
    @FXML
    private TableColumn<RLCreportTableLine, String> parameterColumn;
    @FXML
    private TableColumn<RLCreportTableLine,String> unitsColumn;
    @FXML
    private TableColumn<RLCreportTableLine,String> channel1Column;
    @FXML
    private TableColumn<RLCreportTableLine,String> channel2Column;
    @FXML
    private TableColumn<RLCreportTableLine,String> channel3Column;
    @FXML
    private TableColumn<RLCreportTableLine,String> channel4Column;

    private ObservableList<RLCreportTableLine> RLCresultSource;

    private I18N i18N;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }



    @PostConstruct
    private void init(){
        RLCresultSource = FXCollections.observableArrayList();
        parameterColumn.setCellValueFactory(c->c.getValue().parameter);
        unitsColumn.setCellValueFactory(c->c.getValue().units);
        channel1Column.setCellValueFactory(c->c.getValue().channel_1);
        channel2Column.setCellValueFactory(c->c.getValue().channel_2);
        channel3Column.setCellValueFactory(c->c.getValue().channel_3);
        channel4Column.setCellValueFactory(c->c.getValue().channel_4);
        rlcTableView.getSelectionModel().setCellSelectionEnabled(true);
        bindingI18N();

    }

    public void showResults(Collection<RLCreportTableLine> setOfTableLines){
        RLCresultSource.clear();
        RLCresultSource.addAll(setOfTableLines);
        rlcTableView.setItems(RLCresultSource);

    }

    public void clearTable(){
        RLCresultSource.clear();
        rlcTableView.refresh();
    }

    public static class RLCreportTableLine implements Result {
        StringProperty parameter;
        StringProperty units;
        StringProperty channel_1;
        StringProperty channel_2;
        StringProperty channel_3;
        StringProperty channel_4;

        private List<String> parameterValues;

        public RLCreportTableLine(String parameter,
                                  String units){
            this.parameter = new SimpleStringProperty(parameter);
            this.units = new SimpleStringProperty(units);
            parameterValues = new ArrayList<>(Arrays.asList("-", "-", "-", "-"));
            setParameterValues();

        }

        public void setParameterValue(int number, String value) {
            parameterValues.set(number - 1, value);
            setParameterValues();
        }

        private void setParameterValues() {
            channel_1 = new SimpleStringProperty(parameterValues.get(0));
            channel_2 = new SimpleStringProperty(parameterValues.get(1));
            channel_3 = new SimpleStringProperty(parameterValues.get(2));
            channel_4 = new SimpleStringProperty(parameterValues.get(3));
        }

        @Override
        public String getMainColumn() {
            return parameter.getValue();
        }

        @Override
        public String getSubColumn1() {
            return units.getValue();
        }

        @Override
        public String getSubColumn2() {
            return null;
        }

        @Override
        public List<String> getValueColumns() {
            return parameterValues;
        }
    }
    private void bindingI18N() {
        parameterColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.parameterName"));
        unitsColumn.textProperty().bind(i18N.createStringBinding("h4.report.measure.label.units"));
    }
}
