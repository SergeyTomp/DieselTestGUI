package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisBipModel;
import fi.stardex.sisu.model.uis.UisBipModel.UisBipResult;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class UisBipReportController {
    @FXML private TableView<UisBipResult> bipTableView;
    @FXML private TableColumn<UisBipResult, Test> testNameColumn;
    @FXML private TableColumn<UisBipResult, String>  bipNominalColumn;
    @FXML private TableColumn<UisBipResult, String>  bip1Column;
    @FXML private TableColumn<UisBipResult, String>  bip2Column;
    @FXML private TableColumn<UisBipResult, String>  bip3Column;
    @FXML private TableColumn<UisBipResult, String>  bip4Column;
    @FXML private TableColumn<UisBipResult, String>  bip5Column;
    @FXML private TableColumn<UisBipResult, String>  bip6Column;
    @FXML private TableColumn<UisBipResult, String>  bip7Column;
    @FXML private TableColumn<UisBipResult, String>  bip8Column;
    @FXML private TableColumn<UisBipResult, Boolean>  deleteColumn;

    private static final String CELL_COLOR_DEFAULT = "-fx-text-fill: #bf8248; -fx-alignment: CENTER";
    private static final String CELL_COLOR_ORANGE = "-fx-text-fill: orange; -fx-alignment: CENTER";
    private static final String CELL_COLOR_RED = "-fx-text-fill: red; -fx-alignment: CENTER";

    private MainSectionUisModel mainSectionUisModel;
    private UisBipModel uisBipModel;
    private I18N i18N;

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisBipModel(UisBipModel uisBipModel) {
        this.uisBipModel = uisBipModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init() {
        setupTableColumns();
        bindingI18N();
        setupResultChangeListener();
    }

    private void setupTableColumns() {

        testNameColumn.setCellValueFactory(c -> c.getValue().injectorTestProperty());
        bipNominalColumn.setCellValueFactory(c -> c.getValue().nominalBipProperty());
        bip1Column.setCellValueFactory(c -> c.getValue().value1Property());
        bip2Column.setCellValueFactory(c -> c.getValue().value2Property());
        bip3Column.setCellValueFactory(c -> c.getValue().value3Property());
        bip4Column.setCellValueFactory(c -> c.getValue().value4Property());
        bip5Column.setCellValueFactory(c -> c.getValue().value5Property());
        bip6Column.setCellValueFactory(c -> c.getValue().value6Property());
        bip7Column.setCellValueFactory(c -> c.getValue().value7Property());
        bip8Column.setCellValueFactory(c -> c.getValue().value8Property());
        deleteColumn.setCellValueFactory(c -> new SimpleBooleanProperty());

        setCellFactory(bip1Column);
        setCellFactory(bip2Column);
        setCellFactory(bip3Column);
        setCellFactory(bip4Column);
        setCellFactory(bip5Column);
        setCellFactory(bip6Column);
        setCellFactory(bip7Column);
        setCellFactory(bip8Column);
        deleteColumn.setCellFactory(tableColumn -> new ButtonCell());
    }

    private void setupResultChangeListener(){

        ObservableList<UisBipResult> flowResultsSource = FXCollections.observableArrayList();
        uisBipModel.resultMapChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                flowResultsSource.clear();
                flowResultsSource.addAll(uisBipModel.getResultObservableMap().values());
                bipTableView.setItems(flowResultsSource);
                bipTableView.refresh();
            }
        });
    }

    private void setCellFactory(TableColumn<UisBipResult, String> column){

        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<UisBipResult, String> call(TableColumn<UisBipResult, String> param) {
                return new TableCell<>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);

                            UisBipResult flowTestResult = bipTableView.getItems().get(getTableRow().getIndex());

                            double flowRangeLeft = flowTestResult.getRangeLeft();
                            double flowRangeRight = flowTestResult.getRangeRight();
                            double acceptableFlowRangeLeft = flowTestResult.getAcceptableRangeLeft();
                            double acceptableFlowRangeRight = flowTestResult.getAcceptableRangeRight();

                            if (item.equals("-"))
                                setStyle(CELL_COLOR_DEFAULT);
                            else
                                setStyle(getColorForCell(convertDataToDouble(item), flowRangeLeft, flowRangeRight, acceptableFlowRangeLeft, acceptableFlowRangeRight));
                        }
                    }
                };
            }
        });
    }

    private static String getColorForCell(Double cellValue, double flowRangeLeft, double flowRangeRight, double acceptableFlowRangeLeft, double acceptableFlowRangeRight) {

        class Range {

            private boolean inAcceptableRange() {
                return ((cellValue <= flowRangeLeft) && (cellValue >= acceptableFlowRangeLeft)) || ((cellValue >= flowRangeRight) && (cellValue <= acceptableFlowRangeRight));
            }

            private boolean beyondAcceptableRange() {
                return (cellValue < acceptableFlowRangeLeft) || (cellValue > acceptableFlowRangeRight);
            }

            private boolean inRange(){
                return (cellValue > flowRangeLeft && cellValue < flowRangeRight);
            }
        }

        Range range = new Range();

        if (cellValue == 0d || range.beyondAcceptableRange())
            return CELL_COLOR_RED;
        else if (range.inAcceptableRange())
            return CELL_COLOR_ORANGE;
        else if(range.inRange())
            return CELL_COLOR_DEFAULT;
        else throw new RuntimeException("Invalid cell value");
    }

    private class ButtonCell extends TableCell<UisBipResult, Boolean> {

        private final Button deleteButton;

        ButtonCell() {

            deleteButton = new Button("Delete");
            deleteButton.setStyle("-textFont-color: #1f1f2e");
            showNode(false, deleteButton);
            deleteButton.visibleProperty().bind(mainSectionUisModel.startButtonProperty().not());
            deleteButton.setOnAction(event -> uisBipModel.deleteResult(getTableRow().getTableView().getItems().get(getTableRow().getIndex()).injectorTestProperty().get()));
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {

            super.updateItem(item, empty);

            if (item == null || empty)
                setGraphic(null);
            else
                setGraphic(deleteButton);
        }
    }

    private void showNode(boolean show, Node... nodes) {
        for (Node node : nodes)
            node.setVisible(show);

    }

    private void bindingI18N() {

        testNameColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.testName"));
        bipNominalColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.nominal"));
    }
}
