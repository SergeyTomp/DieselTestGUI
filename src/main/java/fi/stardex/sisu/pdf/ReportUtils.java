package fi.stardex.sisu.pdf;

import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.data.TestResult;
import fi.stardex.sisu.util.FlowUnitObtainer;
import fi.stardex.sisu.util.converters.DataConverter;
import org.codehaus.groovy.runtime.StringGroovyMethods;

public class ReportUtils {

    AdditionalSectionController additionalSectionController;

    public AdditionalSectionController getAdditionalSectionController() {
        return additionalSectionController;
    }

    public void setAdditionalSectionController(AdditionalSectionController additionalSectionController) {
        this.additionalSectionController = additionalSectionController;
    }

    public String getFormulaNominalFlowRangeForInjectors(TestResult testResult) {
        String resultSpread = "";
        if (testResult.getDeliveryRecovery().equals("Delivery")) {
            double coefficient = FlowUnitObtainer.getDeliveryCoefficient();
            String units = additionalSectionController.getFlowController().getDeliveryFlowComboBox().getSelectionModel().getSelectedItem();
//            resultSpread = getNominalSpreadFormula(testResult.getSpreadNominalFlow(), additionalSectionController.getFlowController().getDeliveryFlowComboBox().getSelectionModel().getSelectedItem());
            resultSpread = getNominalSpreadFormula(testResult.getSpreadNominalFlow(), coefficient, units);
        }

        if (testResult.getDeliveryRecovery().equals("Back Flow")) {
            double coefficient = FlowUnitObtainer.getBackFlowCoefficient();
            String units = additionalSectionController.getFlowController().getBackFlowComboBox().getSelectionModel().getSelectedItem();
//            resultSpread = getNominalSpreadFormula(testResult.getSpreadNominalFlow(), additionalSectionController.getFlowController().getBackFlowComboBox().getSelectionModel().getSelectedItem());
            resultSpread = getNominalSpreadFormula(testResult.getSpreadNominalFlow(), coefficient, units);

        }

        return resultSpread;
    }

    public String getFormulaNominalFlowRangeForPumps(TestResult testResult) {
        String resultSpread = "";
//        if (testResult.getDeliveryRecovery().equals("Delivery")) {
//            resultSpread = getNominalSpreadFormula(testResult.getSpreadNominalFlow().toString(), additionalSectionController.getDeliveryPumpComboBox().getSelectionModel().getSelectedItem());
//        }
//
//        if (testResult.getDeliveryRecovery().equals("Back Flow")) {
//            resultSpread = getValueResultForPumps(testResult.getNominalValue().toString(), testResult);
//            resultSpread += " " + additionalSectionController.getBackFlowPumpComboBox().getSelectionModel().getSelectedItem().toString();
//        }
//
        return resultSpread;
    }

    public String getValueResultForInjectors(String value, TestResult testResult) {
        String resultValue = "";
        if (StringGroovyMethods.toDouble(value) < 0) {
            resultValue = "-";
        } else {
            if (testResult.getDeliveryRecovery().equals("Delivery")) {
//                resultValue = Formula.round(additionalSectionController.getFlowController().getDeliveryFlowComboBox().getSelectionModel().getSelectedItem().calculate(Double.valueOf(value)));
                resultValue = Double.toString(DataConverter.round(Double.valueOf(value)));

            }

            if (testResult.getDeliveryRecovery().equals("Back Flow")) {
//                resultValue = Formula.round(additionalSectionController.getFlowController().getDeliveryFlowComboBox().getSelectionModel().getSelectedItem().calculate(Double.valueOf(value)));
                resultValue = Double.toString(DataConverter.round(Double.valueOf(value)));
            }

        }

        return resultValue;
    }

    public String getValueResultForPumps(String value, TestResult testResult) {
        String resultValue = "";
//        if (StringGroovyMethods.toDouble(value) < 0) {
//            resultValue = "-";
//        } else {
//            if (testResult.getDeliveryRecovery().equals("Delivery")) {
//                resultValue = Formula.round(additionalSectionController.getDeliveryPumpComboBox().getSelectionModel().getSelectedItem().calculate(Double.valueOf(value)));
//            }
//
//            if (testResult.getDeliveryRecovery().equals("Back Flow")) {
//                resultValue = Formula.round(additionalSectionController.getBackFlowPumpComboBox().getSelectionModel().getSelectedItem().calculate(Double.valueOf(value)));
//            }
//
//        }
//
        return resultValue;
    }
//    private static String getNominalSpreadFormula(String invariantRange, Formula formula) {
    private static String getNominalSpreadFormula(String invariantRange, double coefficient, String units) {

        String range = invariantRange.replace(",", ".");//todo: think about other way of i18n , .
        String[] partsItem = range.split(":");

//        double leftPart = (Double) formula.calculate(Double.valueOf(partsItem[0]));
//        double rightPart = (Double)formula.calculate(Double.valueOf(partsItem[1]));

        //TODO : проверить участие motorSpeed в расчёте, раньше было
        double leftPart = (Double.valueOf(partsItem[0])) * coefficient;
        double rightPart = (Double.valueOf(partsItem[1])) * coefficient;

        return String.format("%.1f", leftPart) + "-" + String.format("%1.1f", rightPart) + " " + units;
    }



}
