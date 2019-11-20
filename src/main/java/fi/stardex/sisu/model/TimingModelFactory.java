package fi.stardex.sisu.model;

import fi.stardex.sisu.measurement.Timing;
import fi.stardex.sisu.model.cr.CrTestTimingModel;
import fi.stardex.sisu.model.pump.PumpTestTimingModel;
import fi.stardex.sisu.model.uis.UisTestTimingModel;
import fi.stardex.sisu.util.enums.GUI_type;

public class TimingModelFactory {

    private UisTestTimingModel uisTestTimingModel;
    private PumpTestTimingModel pumpTestTimingModel;
    private CrTestTimingModel crTestTimingModel;

    public void setUisTestTimingModel(UisTestTimingModel uisTestTimingModel) {
        this.uisTestTimingModel = uisTestTimingModel;
    }
    public void setPumpTestTimingModel(PumpTestTimingModel pumpTestTimingModel) {
        this.pumpTestTimingModel = pumpTestTimingModel;
    }
    public void setCrTestTimingModel(CrTestTimingModel crTestTimingModel) {
        this.crTestTimingModel = crTestTimingModel;
    }

    public Timing getTimingModel(GUI_type guiType) {

        Timing timingModel;

        switch (guiType) {
            case CR_Inj:
            case HEUI:
                timingModel = crTestTimingModel;
                break;
            case CR_Pump:
                timingModel = pumpTestTimingModel;
                break;
            case UIS:
                timingModel = uisTestTimingModel;
                break;
                default:
                    throw new RuntimeException(" Unknown GUI type selected!");
        }
        return timingModel;
    }
}
