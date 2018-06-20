package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import javafx.scene.control.CheckBox;

import javax.annotation.PostConstruct;

@Module(value = Device.MODBUS_FLOW)
public class FlowStreamUpdater extends FlowUpdater implements Updater{

    public FlowStreamUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                       CheckBox checkBoxFlowVisible, FirmwareDataConverter firmwareDataConverter) {
        super(flowController, injectorSectionController, checkBoxFlowVisible, firmwareDataConverter);
    }

    @PostConstruct
    private void init() {

    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        System.err.println("Stream");

        if ((!checkBoxFlowVisible.isSelected()) && (!injectorSectionPowerSwitch.isSelected()))
            return;

        String value;

//        if ((value = ModbusMapFlow.Channel1Level.getLastValue().toString()) != null)

    }
}
