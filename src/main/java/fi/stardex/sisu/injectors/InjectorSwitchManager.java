package fi.stardex.sisu.injectors;

import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.ui.controllers.additional.LedController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.wrappers.LedControllerWrapper;

import java.util.Iterator;
import java.util.List;

public class InjectorSwitchManager {

    private static final int OFF_COMMAND_NUMBER = 255;

    private ModbusRegisterProcessor ultimaModbusWriter;

    private List<ModbusMapUltima> slotNumbersList = ModbusMapUltima.getSlotNumbersList();

    private LedControllerWrapper ledControllerWrapper;

    private InjectorSectionController injectorSectionController;

    public InjectorSwitchManager(ModbusRegisterProcessor ultimaModbusWriter, LedControllerWrapper ledControllerWrapper,
                                 InjectorSectionController injectorSectionController) {
        this.ultimaModbusWriter = ultimaModbusWriter;
        this.ledControllerWrapper = ledControllerWrapper;
        this.injectorSectionController = injectorSectionController;
    }

    public void sendRefreshedLeds() {
        switchOffAll();
        Iterator<LedController> activeControllersIterator = ledControllerWrapper.activeControllers().iterator();
        int activeLeds = ledControllerWrapper.activeControllers().size();
        double frequency = injectorSectionController.getFreqCurrentSignal().getValue();
        if ((int) frequency == 0 || activeLeds == 0) {
            return;
        }
        int step = (int) (1000 / (frequency * activeLeds));
        int impulseTime = 0;
        int slotPulse = 1;
        while (activeControllersIterator.hasNext()) {
            int selectedChannel = activeControllersIterator.next().getNumber();
        }
    }

    private void switchOffAll() {
        slotNumbersList.forEach((s) -> ultimaModbusWriter.add(s, OFF_COMMAND_NUMBER));
    }
}
