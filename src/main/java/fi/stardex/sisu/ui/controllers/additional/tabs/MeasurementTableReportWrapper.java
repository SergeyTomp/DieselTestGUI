package fi.stardex.sisu.ui.controllers.additional.tabs;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class MeasurementTableReportWrapper {

    private final Gauge parameter1Gauge;
    private final Gauge parameter2Gauge;
    private final Gauge parameter3Gauge;
    private final Gauge parameter4Gauge;

    private final TableView<MeasurementResult> measurementResultTableView;
    private final MeasurementResultsStorage measurementResultsStorage;
    private final CurrentInjectorObtainer currentInjectorObtainer;

    private final static Logger logger = LoggerFactory.getLogger(MeasurementTableReportWrapper.class);

    @Autowired
    public MeasurementTableReportWrapper(AdditionalSectionController additionalSectionController, MeasurementResultsStorage measurementResultsStorage,
                                         CurrentInjectorObtainer currentInjectorObtainer, LedControllerWrapper ledControllerWrapper) {
        this.measurementResultsStorage = measurementResultsStorage;
        this.currentInjectorObtainer = currentInjectorObtainer;
        this.ledControllerWrapper = ledControllerWrapper;

        additionalSectionController.getParameterNameColumn().setCellValueFactory(new PropertyValueFactory<>("parameterName"));
        additionalSectionController.getUnitsColumn().setCellValueFactory(new PropertyValueFactory<>("units"));
        additionalSectionController.getMeasureColumn1().setCellValueFactory(new PropertyValueFactory<>("parameterValue1"));
        additionalSectionController.getMeasureColumn2().setCellValueFactory(new PropertyValueFactory<>("parameterValue2"));
        additionalSectionController.getMeasureColumn3().setCellValueFactory(new PropertyValueFactory<>("parameterValue3"));
        additionalSectionController.getMeasureColumn4().setCellValueFactory(new PropertyValueFactory<>("parameterValue4"));

        parameter1Gauge = additionalSectionController.getMeasurementController().getParameter1Gauge();
        parameter2Gauge = additionalSectionController.getMeasurementController().getParameter2Gauge();
        parameter3Gauge = additionalSectionController.getMeasurementController().getParameter3Gauge();
        parameter4Gauge = additionalSectionController.getMeasurementController().getParameter4Gauge();

        measurementResultTableView = additionalSectionController.getMeasurementTableView();
    }

    public void setMeasureResults() {
        measurementResultTableView.getItems().clear();
        final Injector injector = currentInjectorObtainer.getInjector();
        List<MeasurementResult> measurementResults = measurementResultsStorage.getMeasurementResults(injector);
        if (measurementResults != null) {
            for (MeasurementResult measurementResult : measurementResults) {
                measurementResultTableView.getItems().add(measurementResult);
            }
        }
    }

    public void updateMeasurementGauges() {
        final Injector injector = currentInjectorObtainer.getInjector();
        List<MeasurementResult> measurementResults = measurementResultsStorage.getMeasurementResults(injector);
        if (measurementResults != null) {
            MeasurementResult measurementResult = measurementResults.get(0);
            setParameters(parameter1Gauge, measurementResult);
            measurementResult = measurementResults.get(1);
            setParameters(parameter2Gauge, measurementResult);
        } else {
            parameter1Gauge.setValue(0d);
            parameter2Gauge.setValue(0d);
            parameter3Gauge.setValue(0d);
            parameter4Gauge.setValue(0d);
        }
    }

    private void setParameters(Gauge gauge, MeasurementResult measurementResult) {
        LedController ledController = ledControllerWrapper.singleSelected();

        if (null == ledController) {
            return;
        }

        try {
            switch (ledController.getNumber()) {
                case 1:
                    gauge.setValue(measurementResult.getParameterValue1().equals("-")
                            ? 0d : parseToDouble(measurementResult.getParameterValue1()));
                    break;
                case 2:
                    gauge.setValue(measurementResult.getParameterValue2().equals("-")
                            ? 0d : parseToDouble(measurementResult.getParameterValue2()));
                    break;
                case 3:
                    gauge.setValue(measurementResult.getParameterValue3().equals("-")
                            ? 0d : parseToDouble(measurementResult.getParameterValue3()));
                    break;
                case 4:
                    gauge.setValue(measurementResult.getParameterValue4().equals("-")
                            ? 0d : parseToDouble(measurementResult.getParameterValue4()));
                    break;
                default:
                    break;
            }
        }catch (ParseException e){
            e.printStackTrace();
            logger.warn("Cannot String convert to Double");
        }
    }

    private double parseToDouble(String value) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.parse(value).doubleValue();
    }

}
