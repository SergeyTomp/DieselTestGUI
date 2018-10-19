package fi.stardex.sisu.ui.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResult {

    private static final Logger logger = LoggerFactory.getLogger(TestResult.class);

    private String testName = "";
    private String deliveryRecovery = null;
    private Double nominalValue = null;
    private Double rangePercent = null;
    private Double beakerValue1 = null;
    private Double beakerValue2 = null;
    private Double beakerValue3 = null;
    private Double beakerValue4 = null;
    private String stringBeakerValue1 = null;
    private String stringBeakerValue2 = null;
    private String stringBeakerValue3 = null;
    private String stringBeakerValue4 = null;

    private String spreadNominalFlow = "";

    private Double value = null;
    private String stringValue = null;
    private Double motorSpeed = null;
    private String stringMotorSpeed = null;
    private Double pressure = null;
    private String stringPressure = null;
    private Double deltaPlus = null;
    private Double deltaMinus = null;

    private static final double PERCENT_EXCEPTION_RANGE = 0.03;
    private double nominalRight;
    private double nominalLeft;
    private double nominalExceptRight;
    private double nominalExceptLeft;

    protected TestResult() {
    }

    public TestResult(String testName, String deliveryRecovery, Double nominalValue, Double rangePercent,
                      Double beakerValue1, Double beakerValue2, Double beakerValue3,
                      Double beakerValue4) {

        this.testName = testName;
        this.deliveryRecovery = deliveryRecovery;
        this.nominalValue = nominalValue;
        this.rangePercent = rangePercent;
        this.beakerValue1 = beakerValue1;
        this.beakerValue2 = beakerValue2;
        this.beakerValue3 = beakerValue3;
        this.beakerValue4 = beakerValue4;
        stringBeakerValue1 = String.valueOf(beakerValue1);
        stringBeakerValue2 = String.valueOf(beakerValue2);
        stringBeakerValue3 = String.valueOf(beakerValue3);
        stringBeakerValue4 = String.valueOf(beakerValue4);

        this.deltaPlus = rangePercent;
        this.deltaMinus = rangePercent;
        nominalRight = nominalValue + nominalValue * rangePercent * 0.01;
        nominalLeft = nominalValue - nominalValue * rangePercent * 0.01;
        /**
         * make exception range of nominal value
         * **/
        nominalExceptRight = nominalRight + nominalRight * PERCENT_EXCEPTION_RANGE;
        nominalExceptLeft = nominalLeft - nominalLeft * PERCENT_EXCEPTION_RANGE;
        spreadNominalFlow = String.format("%.1f", nominalLeft) + ":" + String.format("%.1f", nominalRight);
    }

    public TestResult(String testName, String deliveryRecovery, Double nominalValue, Double value, Double deltaMinus, Double deltaPlus,
                      Double motorSpeed, Double pressure, String pump) {

        this.testName = testName;
        this.deliveryRecovery = deliveryRecovery;
        this.nominalValue = nominalValue;
        this.deltaPlus = deltaPlus;
        this.deltaMinus = deltaMinus;
        this.value = value;
        stringValue = String.valueOf(value);
        this.motorSpeed = motorSpeed;
        stringMotorSpeed = String.valueOf(motorSpeed);
        this.pressure = pressure;
        stringPressure = String.valueOf(pressure);
        nominalRight = nominalValue + nominalValue * deltaPlus * 0.01;
        nominalLeft = nominalValue - nominalValue * deltaMinus * 0.01;

        logger.trace("deltaPlus = " + deltaPlus + " deltaMinus = " + deltaMinus + " nominalValue = " + nominalValue);


        /**
         * make exception range of nominal value
         * **/
        nominalExceptRight = nominalRight + nominalRight * PERCENT_EXCEPTION_RANGE;
        nominalExceptLeft = nominalLeft - nominalLeft * PERCENT_EXCEPTION_RANGE;
        spreadNominalFlow = String.format("%.1f", nominalLeft) + ":" + String.format("%.1f", nominalRight);

        logger.trace("nominalRight = " + nominalRight + " nominalLeft = " + nominalLeft + " spreadNominalFlow = " + spreadNominalFlow);
    }

    public static TestResult makeTestResultsForInjectors(String testName, String deliveryRecovery, Double nominalValue, Double rangePercent,
                                                         Double beakerValue1, Double beakerValue2, Double beakerValue3,
                                                         Double beakerValue4) {

        return new TestResult(testName, deliveryRecovery, nominalValue, rangePercent,
                beakerValue1, beakerValue2, beakerValue3,
                beakerValue4);
    }

    public static TestResult makeTestResultsForPumps(String testName, String deliveryRecovery, Double nominalValue, Double value, Double deltaMinus,
                                                     Double deltaPlus, Double motorSpeed, Double pressure) {

        return new TestResult("Efficiency " + testName, deliveryRecovery, nominalValue, value, deltaMinus, deltaPlus, motorSpeed, pressure, null);
    }

    public Double getNominalValue() {
        return nominalValue;
    }

    public Double getBeakerValue1() {
        return beakerValue1;
    }

    public Double getBeakerValue2() {
        return beakerValue2;
    }

    public Double getBeakerValue3() {
        return beakerValue3;
    }

    public Double getBeakerValue4() {
        return beakerValue4;
    }

    public String getTestName() {
        return testName;
    }

    public Double getRangePercent() {
        return rangePercent;
    }

    public String getSpreadNominalFlow() {
        return spreadNominalFlow;
    }

    public Double getValue() {
        return value;
    }

    public String getDeliveryRecovery() {
        return deliveryRecovery;
    }

    public Double getMotorSpeed() {
        return motorSpeed;
    }

    public Double getPressure() {
        return pressure;
    }

    public Double getDeltaMinus() {
        return deltaMinus;
    }

    public Double getDeltaPlus() {
        return deltaPlus;
    }

    public String getStringBeakerValue1() {
        return stringBeakerValue1;
    }

    public String getStringBeakerValue2() {
        return stringBeakerValue2;
    }

    public String getStringBeakerValue3() {
        return stringBeakerValue3;
    }

    public String getStringBeakerValue4() {
        return stringBeakerValue4;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getStringMotorSpeed() {
        return stringMotorSpeed;
    }

    public String getStringPressure() {
        return stringPressure;
    }

    public double getNominalRight() {
        return nominalRight;
    }

    public double getNominalLeft() {
        return nominalLeft;
    }

    public double getNominalExceptRight() {
        return nominalExceptRight;
    }

    public double getNominalExceptLeft() {
        return nominalExceptLeft;
    }
}
