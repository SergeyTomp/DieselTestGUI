package fi.stardex.sisu.ui.updaters;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.registers.stand.ModbusMapStand;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.util.VisualUtils;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import static fi.stardex.sisu.registers.stand.ModbusMapStand.*;

@Module(value = Device.MODBUS_STAND)
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

    private VisualUtils visualUtils;

    public TestBenchSectionUpdater(TestBenchSectionController testBenchSectionController, VisualUtils visualUtils) {

        this.testBenchSectionController = testBenchSectionController;
        this.visualUtils = visualUtils;

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

        runSyncWriteReadBooleanRegisters(Rotation, testBenchStartToggleButton);

        runSyncWriteReadBooleanRegisters(FanTurnOn, fanControlToggleButton);

        runPressureAndTemperatureRegisters(Pressure1, pressProgressBar1, pressText1);

        runPressureAndTemperatureRegisters(Temperature1, tempProgressBar1, tempText1);

        runPressureAndTemperatureRegisters(Temperature2, tempProgressBar2, tempText2);

        runTargetRPMRegister();

        runRotationDirectionRegister();

        runPumpRegisters();

        runCurrentRPMRegister();

        runTankOilRegister();

    }

    private void runSyncWriteReadBooleanRegisters(ModbusMapStand register, ToggleButton toggleButton) {

        Boolean registerLastValue = (Boolean) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (registerLastValue != null)
            toggleButton.selectedProperty().setValue(registerLastValue);

    }

    private void runPressureAndTemperatureRegisters(ModbusMapStand register, ProgressBar progressBar, Text text) {

        Double registerLastValue = (Double) register.getLastValue();

        if (registerLastValue != null)
            visualUtils.setPressureProgress(progressBar, text, registerLastValue);

    }

    private void runTargetRPMRegister() {

        Integer targetRPMLastValue = (Integer) TargetRPM.getLastValue();

        if (TargetRPM.isSyncWriteRead())
            TargetRPM.setSyncWriteRead(false);
        else if (targetRPMLastValue != null) {
            targetRPMSpinner.getValueFactory().setValue(targetRPMLastValue);
        }

    }

    private void runRotationDirectionRegister() {

        Boolean rotationDirectionLastValue = (Boolean) RotationDirection.getLastValue();

        if (RotationDirection.isSyncWriteRead())
            RotationDirection.setSyncWriteRead(false);
        else if (rotationDirectionLastValue != null) {
            boolean lastValue = rotationDirectionLastValue;
            if (lastValue)
                rightDirectionRotationToggleButton.selectedProperty().setValue(true);
            else
                leftDirectionRotationToggleButton.selectedProperty().setValue(true);
        }

    }

    private void runPumpRegisters() {

        Boolean pumpTurnOnLastValue = (Boolean) PumpTurnOn.getLastValue();
        Boolean pumpAutoModeLastValue = (Boolean) PumpAutoMode.getLastValue();

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

    private void runCurrentRPMRegister() {

        Integer currentRPMLastValue = (Integer) CurrentRPM.getLastValue();

        if (currentRPMLastValue != null)
            currentRPMLcd.setValue(currentRPMLastValue);

    }

    private void runTankOilRegister() {

        Integer tankOilLevelLastValue = (Integer) TankOilLevel.getLastValue();

        if (tankOilLevelLastValue != null) {
            if (tankOilLevelLastValue > 10)
                changeTankOil("green-oil-bar", "NORMAL");
            else if (tankOilLevelLastValue > 1)
                changeTankOil("yellow-oil-bar", "LOW");
            else
                changeTankOil("red-oil-bar", "VERY\nLOW");

            tankOil.setProgress(tankOilLevelLastValue / 100.0);
        }

    }

    private void changeTankOil(String style, String text) {

        tankOil.getStyleClass().clear();
        tankOil.getStyleClass().add(style);
        tankOilText.setText(text);

    }

}
