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

    private ToggleButton leftDirectionRotationToggleButton;

    private ToggleButton rightDirectionRotationToggleButton;

    private ToggleButton testBenchStartToggleButton;

    private ToggleButton pumpControlToggleButton;

    private ToggleButton fanControlToggleButton;

    private Lcd currentRPMlcd;

    private ProgressBar tempProgressBar1;

    private ProgressBar tempProgressBar2;

    private ProgressBar pressProgressBar1;

    private Text tempText1;

    private Text tempText2;

    private Text pressText1;

    private ProgressBar tankOil;

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
        currentRPMlcd = testBenchSectionController.getCurrentRPMlcd();
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

        Object targetRPMLastValue = TargetRPM.getLastValue();

        if (TargetRPM.isSyncWriteRead())
            TargetRPM.setSyncWriteRead(false);
        else if (targetRPMLastValue != null) {
            targetRPMSpinner.getValueFactory().setValue(Integer.valueOf(targetRPMLastValue.toString()));
        }

        Object rotationDirectionLastValue = RotationDirection.getLastValue();

        if (RotationDirection.isSyncWriteRead())
            RotationDirection.setSyncWriteRead(false);
        else if (rotationDirectionLastValue != null) {
            boolean lastValue = (Boolean) rotationDirectionLastValue;
            if (lastValue)
                rightDirectionRotationToggleButton.selectedProperty().setValue(true);
            else
                leftDirectionRotationToggleButton.selectedProperty().setValue(true);
        }

        runSyncWriteReadableBooleanRegisters(Rotation, testBenchStartToggleButton);

        Object rotationLastValue = Rotation.getLastValue();

        if (Rotation.isSyncWriteRead())
            Rotation.setSyncWriteRead(false);
        else if (rotationLastValue != null)
            testBenchStartToggleButton.selectedProperty().setValue((Boolean) rotationLastValue);


        Object pumpTurnOnLastValue = PumpTurnOn.getLastValue();
        Object pumpAutoModeLastValue = PumpAutoMode.getLastValue();

        if (pumpAutoModeLastValue != null) {
            if ((Boolean)pumpAutoModeLastValue) {
                testBenchSectionController.setPumpState(testBenchStartToggleButton.isSelected() ?
                        TestBenchSectionController.StatePump.AUTO_ON : TestBenchSectionController.StatePump.AUTO_OFF);
            } else {
                if (pumpTurnOnLastValue != null) {
                    if ((Boolean) pumpTurnOnLastValue)
                        testBenchSectionController.setPumpState(TestBenchSectionController.StatePump.ON);
                    else
                        testBenchSectionController.setPumpState(TestBenchSectionController.StatePump.OFF);
                }
            }
            TestBenchSectionController.StatePump currentStatePump = testBenchSectionController.getPumpState();

            pumpControlToggleButton.getStyleClass().set(1, currentStatePump.getStyle());
            pumpControlToggleButton.setText(currentStatePump.getText());
        }

        Object fanTurnOnLastValue = FanTurnOn.getLastValue();

        if (FanTurnOn.isSyncWriteRead())
            FanTurnOn.setSyncWriteRead(false);
        else if (fanTurnOnLastValue != null)
            fanControlToggleButton.selectedProperty().setValue((Boolean) fanTurnOnLastValue);

        Object currentRPMLastValue = CurrentRPM.getLastValue();

        if (currentRPMLastValue != null)
            currentRPMlcd.setValue((Integer)currentRPMLastValue);

        Object pressure1LastValue = Pressure1.getLastValue();

        if (pressure1LastValue != null)
            visualUtils.setPressureProgress(pressProgressBar1, pressText1, (Double) pressure1LastValue);

        Object temperature1LastValue = Temperature1.getLastValue();

        if (temperature1LastValue != null)
            visualUtils.setTemperatureProgress(tempProgressBar1, tempText1, (Double) temperature1LastValue);

        Object temperature2LastValue = Temperature2.getLastValue();

        if (temperature2LastValue != null)
            visualUtils.setTemperatureProgress(tempProgressBar2, tempText2, (Double) temperature2LastValue);

        Integer tankOilLevelLastValue = (Integer) TankOilLevel.getLastValue();

        if (tankOilLevelLastValue != null) {
            if(tankOilLevelLastValue > 10) {
                tankOil.getStyleClass().clear();
                tankOil.getStyleClass().add("green-oil-bar");
                tankOilText.setText("NORMAL");
            } else if(tankOilLevelLastValue > 1) {
                tankOil.getStyleClass().clear();
                tankOil.getStyleClass().add("yellow-oil-bar");
                tankOilText.setText("LOW");
            } else {
                tankOil.getStyleClass().clear();
                tankOil.getStyleClass().add("red-oil-bar");
                tankOilText.setText("VERY\nLOW");
            }

            tankOil.setProgress(tankOilLevelLastValue / 100.0);
        }

    }

    private void runSyncWriteReadableBooleanRegisters(ModbusMapStand register, ToggleButton targetToggleButton) {

        Boolean registerLastValue = (Boolean) register.getLastValue();

        if (register.isSyncWriteRead())
            register.setSyncWriteRead(false);
        else if (registerLastValue != null)
            targetToggleButton.selectedProperty().setValue((Boolean) registerLastValue);

    }
}
