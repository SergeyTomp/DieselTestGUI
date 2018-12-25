package fi.stardex.sisu.ui.updaters;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.STAND_FM_4_CH;

@Module(value = {Device.MODBUS_FLOW, Device.MODBUS_STAND})
public class TestBenchSectionUpdater implements Updater {

    private Spinner<Integer> targetRPMSpinner;

    private Lcd currentRPMLcd;

    private ToggleButton leftDirectionRotationToggleButton;

    private ToggleButton rightDirectionRotationToggleButton;

    private ToggleButton testBenchStartToggleButton;

    private ToggleButton pumpControlToggleButton;

    private ToggleButton fanControlToggleButton;

    private ProgressBar tempProgressBar1;

    private ProgressBar tempProgressBar2;

    private ProgressBar pressProgressBar1;

    private ProgressBar tankOil;

    private Text tempText1;

    private Text tempText2;

    private Text pressText1;

    private Text tankOilText;

    private TestBenchSectionController testBenchSectionController;

    private FirmwareVersion<FlowVersions> flowFirmwareVersion;

    public TestBenchSectionUpdater(TestBenchSectionController testBenchSectionController,
                                   FirmwareVersion<FlowVersions> flowFirmwareVersion) {

        this.testBenchSectionController = testBenchSectionController;
        this.flowFirmwareVersion = flowFirmwareVersion;

        pumpControlToggleButton = testBenchSectionController.getPumpControlToggleButton();
        targetRPMSpinner = testBenchSectionController.getTargetRPMSpinner();
        leftDirectionRotationToggleButton = testBenchSectionController.getLeftDirectionRotationToggleButton();
        rightDirectionRotationToggleButton = testBenchSectionController.getRightDirectionRotationToggleButton();
        testBenchStartToggleButton = testBenchSectionController.getTestBenchStartToggleButton();
        fanControlToggleButton = testBenchSectionController.getFanControlToggleButton();
        currentRPMLcd = testBenchSectionController.getCurrentRPMLcd();
        tempProgressBar1 = testBenchSectionController.getTempProgressBar1();
        tempProgressBar2 = testBenchSectionController.getTempProgressBar2();
        pressProgressBar1 = testBenchSectionController.getPressProgressBar1();
        tempText1 = testBenchSectionController.getTempText1();
        tempText2 = testBenchSectionController.getTempText2();
        pressText1 = testBenchSectionController.getPressText1();
        tankOil = testBenchSectionController.getTankOil();
        tankOilText = testBenchSectionController.getTankOilText();

    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        boolean isStandFMVersion = (flowFirmwareVersion.getVersions() == STAND_FM) || (flowFirmwareVersion.getVersions() == STAND_FM_4_CH);

        runSyncWriteReadBooleanRegisters(isStandFMVersion ? RotationStandFM : Rotation, testBenchStartToggleButton);

        runSyncWriteReadBooleanRegisters(isStandFMVersion ? FanTurnOnStandFM : FanTurnOn, fanControlToggleButton);

        runPressureRegister(isStandFMVersion ? Pressure1StandFM : Pressure1, pressProgressBar1, pressText1);

        runTemperatureRegister(isStandFMVersion ? Temperature1StandFM : Temperature1, tempProgressBar1, tempText1);

        runTemperatureRegister(isStandFMVersion ? Temperature2StandFM : Temperature2, tempProgressBar2, tempText2);

        runTargetRPMRegister(isStandFMVersion ? TargetRPMStandFM : TargetRPM);

        runRotationDirectionRegister(isStandFMVersion ? RotationDirectionStandFM : RotationDirection);

        runPumpRegisters(isStandFMVersion ? PumpTurnOnStandFM : PumpTurnOn, isStandFMVersion ? PumpAutoModeStandFM : PumpAutoMode);

        runCurrentRPMRegister(isStandFMVersion ? CurrentRPMStandFM : CurrentRPM);

        runTankOilRegister(isStandFMVersion ? TankOilLevelStandFM : TankOilLevel);

    }

    private void runSyncWriteReadBooleanRegisters(ModbusMapStand register, ToggleButton toggleButton) {

        Boolean registerLastValue = (Boolean) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (registerLastValue != null)
            toggleButton.selectedProperty().setValue(registerLastValue);

    }

    private void runPressureRegister(ModbusMapStand register, ProgressBar progressBar, Text text) {

        Double registerLastValue = (Double) register.getLastValue();

        if (registerLastValue != null)
            setPressureProgress(3, 5, progressBar, text, registerLastValue);
    }

    private void runTemperatureRegister(ModbusMapStand register, ProgressBar progressBar, Text text){

        Double registerLastValue = (Double) register.getLastValue();

        if (registerLastValue != null)
            setTemperatureProgress(37, 41, progressBar, text, registerLastValue);
    }

    private void setPressureProgress(int left, int right, ProgressBar progressBar, Text text, double pressureValue) {

        System.err.println(pressureValue);

        if (pressureValue <= left) {
            progressBarStyle(progressBar, "green-progress-bar");
        }
        if (pressureValue > left && pressureValue < right) {
            progressBarStyle(progressBar, "orange-progress-bar");
        }
        if (pressureValue >= right) {
            progressBarStyle(progressBar, "red-progress-bar");
        }
        fillProgressBar(pressureValue, text, progressBar);
    }

    private void setTemperatureProgress(int left, int right, ProgressBar progressBar, Text text, double temperatureValue){

        if (temperatureValue <= left && temperatureValue >= right) {
            progressBarStyle(progressBar, "red-progress-bar");
        }
        else {
            progressBarStyle(progressBar, "green-progress-bar");
        }
        fillProgressBar(temperatureValue, text, progressBar);
    }

    private void fillProgressBar(double value, Text text, ProgressBar progressBar){
        text.setText(String.format("%.1f", value));
        progressBar.setProgress(value < 1 ? 1.0 : value);
    }

    private static void progressBarStyle(ProgressBar progressBar, String style) {

        progressBar.getStyleClass().clear();
        progressBar.getStyleClass().add("progress-bar");
        progressBar.getStyleClass().add(style);
    }

    private void runTargetRPMRegister(ModbusMapStand register) {

        Integer targetRPMLastValue = (Integer) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (targetRPMLastValue != null)
            targetRPMSpinner.getValueFactory().setValue(targetRPMLastValue);
    }

    private void runRotationDirectionRegister(ModbusMapStand register) {

        Boolean rotationDirectionLastValue = (Boolean) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (rotationDirectionLastValue != null) {
            boolean lastValue = rotationDirectionLastValue;
            if (lastValue)
                rightDirectionRotationToggleButton.selectedProperty().setValue(true);
            else
                leftDirectionRotationToggleButton.selectedProperty().setValue(true);
        }

    }

    private void runPumpRegisters(ModbusMapStand pumpTurnOnRegister, ModbusMapStand pumpAutoModeRegister) {

        Boolean pumpTurnOnLastValue = (Boolean) pumpTurnOnRegister.getLastValue();
        Boolean pumpAutoModeLastValue = (Boolean) pumpAutoModeRegister.getLastValue();

        if (pumpAutoModeLastValue != null) {
            if (pumpAutoModeLastValue) {
                testBenchSectionController.setPumpState(testBenchStartToggleButton.isSelected() ?
                        TestBenchSectionController.StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);
            } else {
                if (pumpTurnOnLastValue != null) {
                    if (pumpTurnOnLastValue)
                        testBenchSectionController.setPumpState(TestBenchSectionController.StatePump.ON);
                    else
                        testBenchSectionController.setPumpState(TestBenchSectionController.StatePump.OFF);
                }
            }
            TestBenchSectionController.StatePump currentStatePump = testBenchSectionController.getPumpState();

            pumpControlToggleButton.getStyleClass().set(1, currentStatePump.getStyle());
            pumpControlToggleButton.setText(currentStatePump.getText());
        }

    }

    private void runCurrentRPMRegister(ModbusMapStand register) {

        Integer currentRPMLastValue = (Integer) register.getLastValue();

        if (currentRPMLastValue != null)
            currentRPMLcd.setValue(currentRPMLastValue);

    }

    private void runTankOilRegister(ModbusMapStand register) {

        Integer tankOilLevelLastValue = (Integer) register.getLastValue();

        if (tankOilLevelLastValue != null) {
            if (tankOilLevelLastValue > 10)
                changeTankOil("green-oil-bar", "NORMAL");
            else if (tankOilLevelLastValue > 1)
                changeTankOil("yellow-oil-bar", "LOW");
            else
                changeTankOil("red-oil-bar", "VERY\nLOW");

            tankOil.setProgress(tankOilLevelLastValue / 110.0);
        }

    }

    private void changeTankOil(String style, String text) {

        tankOil.getStyleClass().clear();
        tankOil.getStyleClass().add(style);
        tankOilText.setText(text);

    }

}
