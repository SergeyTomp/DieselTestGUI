package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.ui.controllers.additional.tabs.FlowController;
import fi.stardex.sisu.ui.controllers.additional.tabs.SettingsController;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.version.FirmwareVersion;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.MASTER;

@Module(value = Device.MODBUS_FLOW)
public class FlowMasterUpdater extends FlowUpdater implements Updater {

    public FlowMasterUpdater(FlowController flowController, InjectorSectionController injectorSectionController,
                             SettingsController settingsController, FirmwareVersion<FlowVersions> flowFirmwareVersion, Tests tests) {

        super(flowController, injectorSectionController, settingsController, flowFirmwareVersion, tests);

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

        if (isNotInstantFlow() || isNotMeasuring()) {
            setAllLabelsAndFieldsToNull();
            return;
        }

        runOnSingleChannelMode(MASTER);

    }

}
