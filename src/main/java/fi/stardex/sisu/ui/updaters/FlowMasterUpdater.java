package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.version.FlowFirmwareVersion;

import javax.annotation.PostConstruct;

@Module(value = Device.MODBUS_FLOW)
public class FlowMasterUpdater extends FlowUpdater implements Updater {

    public FlowMasterUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                             SettingsController settingsController, DataConverter firmwareDataConverter) {

        super(flowController, injectorSectionController, settingsController, firmwareDataConverter);

    }

    @PostConstruct
    private void init() {
        initListeners();
    }

    @Override
    public void update() {

    }

    /**
     * Flow master is running on a single Channel mode no matter what mode is chosen. If there is a multi-channel mode
     * selected in Settings every selected beaker shows the same flow
     */
    @Override
    public void run() {

        if ((!checkBoxFlowVisible.isSelected()) && (!injectorSectionPowerSwitch.isSelected()))
            return;

        runOnSingleChannelMode(FlowFirmwareVersion.FLOW_MASTER);

    }

}
