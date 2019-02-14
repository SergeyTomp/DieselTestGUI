package fi.stardex.sisu.ui.updaters;


import fi.stardex.sisu.annotations.Module;
import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.model.FlowRangeModel;
import fi.stardex.sisu.model.PumpFlowTemperaturesModel;
import fi.stardex.sisu.model.PumpFlowValuesModel;
import fi.stardex.sisu.states.InstantFlowState;
import fi.stardex.sisu.states.PumpHighPressureSectionPwrState;
import fi.stardex.sisu.version.FirmwareVersion;

import static fi.stardex.sisu.registers.flow.ModbusMapFlow.*;
import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;

@Module(value = Device.MODBUS_FLOW)
public class PumpFlowUpdater implements Updater{

    private PumpFlowValuesModel pumpDeliveryFlowValuesModel;
    private PumpFlowValuesModel pumpBackFlowValuesModel;
    private PumpFlowTemperaturesModel pumpDeliveryFlowTemperaturesModel;
    private PumpFlowTemperaturesModel pumpBackFlowTemperaturesModel;
    private FirmwareVersion<FlowVersions> flowFirmwareVersion;
    private InstantFlowState instantFlowState;
    private PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState;
    private FlowRangeModel pumpDeliveryFlowRangeModel;
    private FlowRangeModel pumpBackFlowRangeModel;

    public void setPumpDeliveryFlowValuesModel(PumpFlowValuesModel pumpDeliveryFlowValuesModel) {
        this.pumpDeliveryFlowValuesModel = pumpDeliveryFlowValuesModel;
    }
    public void setPumpBackFlowValuesModel(PumpFlowValuesModel pumpBackFlowValuesModel) {
        this.pumpBackFlowValuesModel = pumpBackFlowValuesModel;
    }
    public void setPumpDeliveryFlowTemperaturesModel(PumpFlowTemperaturesModel pumpDeliveryFlowTemperaturesModel) {
        this.pumpDeliveryFlowTemperaturesModel = pumpDeliveryFlowTemperaturesModel;
    }
    public void setPumpBackFlowTemperaturesModel(PumpFlowTemperaturesModel pumpBackFlowTemperaturesModel) {
        this.pumpBackFlowTemperaturesModel = pumpBackFlowTemperaturesModel;
    }
    public void setFlowFirmwareVersion(FirmwareVersion<FlowVersions> flowFirmwareVersion) {
        this.flowFirmwareVersion = flowFirmwareVersion;
    }
    public void setInstantFlowState(InstantFlowState instantFlowState) {
        this.instantFlowState = instantFlowState;
    }
    public void setPumpHighPressureSectionPwrState(PumpHighPressureSectionPwrState pumpHighPressureSectionPwrState) {
        this.pumpHighPressureSectionPwrState = pumpHighPressureSectionPwrState;
    }
    public void setPumpDeliveryFlowRangeModel(FlowRangeModel pumpDeliveryFlowRangeModel) {
        this.pumpDeliveryFlowRangeModel = pumpDeliveryFlowRangeModel;
    }
    public void setPumpBackFlowRangeModel(FlowRangeModel pumpBackFlowRangeModel) {
        this.pumpBackFlowRangeModel = pumpBackFlowRangeModel;
    }

    @Override
    public void update() {

    }

    @Override
    public void run() {

        if(!instantFlowState.isInstantFlowStateProperty().get()){
//            if (pumpDeliveryFlowValuesModel.flowProperty() != null) {
//                pumpDeliveryFlowValuesModel.flowProperty().setValue(null);
//            }
            return;
        }

        String value;

        switch(flowFirmwareVersion.getVersions()){

            case MASTER:

                if((value = Channel3Level.getLastValue().toString()) != null){
                    pumpDeliveryFlowValuesModel.flowProperty().setValue(value);
                }
                if((value = Channel4Level.getLastValue().toString()) != null){
                    pumpBackFlowValuesModel.flowProperty().setValue(value);
                }
                if((value = Channel3Temperature1.getLastValue().toString()) != null){
                    pumpDeliveryFlowTemperaturesModel.temperature1FlowProperty().setValue(value);
                }
                if((value = Channel3Temperature2.getLastValue().toString()) != null){
                    pumpDeliveryFlowTemperaturesModel.temperature2FlowProperty().setValue(value);
                }
                if((value = Channel4Temperature1.getLastValue().toString()) != null){
                    pumpBackFlowTemperaturesModel.temperature1FlowProperty().setValue(value);
                }
                if((value = Channel4Temperature2.getLastValue().toString()) != null){
                    pumpBackFlowTemperaturesModel.temperature2FlowProperty().setValue(value);
                }

                break;

            case STREAM:

                if((value = Channel9Level.getLastValue().toString()) != null){
                    pumpDeliveryFlowValuesModel.flowProperty().setValue(value);
                }
                if((value = Channel10Level.getLastValue().toString()) != null){
                    pumpBackFlowValuesModel.flowProperty().setValue(value);
                }
                if((value = Channel9Temperature1.getLastValue().toString()) != null){
                    pumpDeliveryFlowTemperaturesModel.temperature1FlowProperty().setValue(value);
                }
                if((value = Channel9Temperature2.getLastValue().toString()) != null){
                    pumpDeliveryFlowTemperaturesModel.temperature2FlowProperty().setValue(value);
                }
                if((value = Channel10Temperature1.getLastValue().toString()) != null){
                    pumpBackFlowTemperaturesModel.temperature1FlowProperty().setValue(value);
                }
                if((value = Channel10Temperature2.getLastValue().toString()) != null){
                    pumpBackFlowTemperaturesModel.temperature2FlowProperty().setValue(value);
                }
                break;
        }
    }
}
