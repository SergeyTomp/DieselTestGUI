package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.FirmwareDataConverter;
import javafx.scene.control.CheckBox;

@Module(value = Device.MODBUS_FLOW)
public class FlowStreamUpdater extends FlowUpdater implements Updater{

    public FlowStreamUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                       CheckBox checkBoxFlowVisible, FirmwareDataConverter firmwareDataConverter) {

    }

    @Override
    public void update() {

    }

    @Override
    public void run() {
        System.err.println("Stream");
    }
}
