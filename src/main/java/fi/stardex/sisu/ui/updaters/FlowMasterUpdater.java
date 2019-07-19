package fi.stardex.sisu.ui.updaters;

import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.model.cr.InjConfigurationModel;
import fi.stardex.sisu.states.InjectorControllersState;
import fi.stardex.sisu.states.InjectorSectionPwrState;
import fi.stardex.sisu.states.InstantFlowState;
import fi.stardex.sisu.ui.controllers.cr.tabs.FlowController;
import fi.stardex.sisu.version.FirmwareVersion;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions.MASTER;

@Module(value = Device.MODBUS_FLOW)
public class FlowMasterUpdater extends FlowUpdater implements Updater {

    public FlowMasterUpdater(FlowController flowController,
                             FirmwareVersion<FlowVersions> flowFirmwareVersion,
                             InjConfigurationModel injConfigurationModel,
                             InstantFlowState instantFlowState,
                             InjectorControllersState injectorControllersState,
                             InjectorSectionPwrState injectorSectionPwrState) {

        super(flowController,
                flowFirmwareVersion,
                injConfigurationModel,
                instantFlowState,
                injectorControllersState,
                injectorSectionPwrState);

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

        if (isNotMeasuring()) {
            setAllDeliveyLabelsAndFieldsToNull();
//            setAllLabelsAndFieldsToNull();
//            return;
        }else if(isNotInstantFlow()){
//            allFlowLabels.forEach(textField -> textField.setText(null));
            return;
        }

        runOnSingleChannelMode(MASTER);

    }

}
