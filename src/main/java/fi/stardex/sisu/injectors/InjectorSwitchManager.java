package fi.stardex.sisu.injectors;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.wrappers.LedControllerWrapper;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class InjectorSwitchManager {

    private static final int OFF_COMMAND_NUMBER = 255;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private List<ModbusMapUltima> slotNumbersList = ModbusMapUltima.getSlotNumbersList();

    private List<ModbusMapUltima> slotPulsesList = ModbusMapUltima.getSlotPulsesList();

    private LedControllerWrapper ledControllerWrapper;

    private InjectorSectionController injectorSectionController;

    private SettingsController settingsController;

    private RadioButton coilRadioButton;

    private RadioButton piezoRadioButton;

    private RadioButton piezoDelphiRadioButton;

    public InjectorSwitchManager(ModbusRegisterProcessor ultimaModbusWriter, LedControllerWrapper ledControllerWrapper,
                                 InjectorSectionController injectorSectionController, SettingsController settingsController) {
        this.ultimaModbusWriter = ultimaModbusWriter;
        this.ledControllerWrapper = ledControllerWrapper;
        this.injectorSectionController = injectorSectionController;
        this.settingsController = settingsController;
        coilRadioButton = injectorSectionController.getCoilRadioButton();
        piezoRadioButton = injectorSectionController.getPiezoRadioButton();
        piezoDelphiRadioButton = injectorSectionController.getPiezoDelphiRadioButton();
    }

    public void sendRefreshedLeds() {
        switchOffAll();

        writeInjectorTypeRegister();

        List<LedController> activeControllers = ledControllerWrapper.activeControllers();
        Iterator<LedController> activeControllersIterator = activeControllers.iterator();
        int activeLeds = activeControllers.size();
        double frequency = injectorSectionController.getFreqCurrentSignal().getValue();
        ultimaModbusWriter.add(ModbusMapUltima.GImpulsesPeriod, 1000 / frequency);
        if (activeLeds == 0) {
            return;
        }
        int step = (int) Math.round(1000 / (frequency * activeLeds));
        int impulseTime = 0;
        while (activeControllersIterator.hasNext()) {
            int selectedChannel = activeControllersIterator.next().getNumber();
            int injectorChannel = settingsController.getComboInjectorConfig().getSelectionModel().getSelectedItem() ==
                    InjectorChannel.SINGLE_CHANNEL ? 1 : selectedChannel;
            ultimaModbusWriter.add(slotNumbersList.get(selectedChannel - 1), injectorChannel);
            ultimaModbusWriter.add(slotPulsesList.get(selectedChannel - 1), impulseTime);
            impulseTime += step;
        }
    }

    private void switchOffAll() {
        slotNumbersList.forEach((s) -> ultimaModbusWriter.add(s, OFF_COMMAND_NUMBER));
        slotPulsesList.forEach((s) -> ultimaModbusWriter.add(s, 0));
    }

    private void writeInjectorTypeRegister() {
        Toggle newValue = injectorSectionController.getPiezoCoilToggleGroup().getSelectedToggle();

        if (Objects.equals(coilRadioButton, newValue))
            ultimaModbusWriter.add(ModbusMapUltima.Injector_type, 0);
        else if (Objects.equals(piezoRadioButton, newValue))
            ultimaModbusWriter.add(ModbusMapUltima.Injector_type, 1);
        else if (Objects.equals(piezoDelphiRadioButton, newValue))
            ultimaModbusWriter.add(ModbusMapUltima.Injector_type, 2);
        else
            throw new AssertionError("Coil or piezo buttons has not been set.");
    }
}
