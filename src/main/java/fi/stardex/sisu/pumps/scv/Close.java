package fi.stardex.sisu.pumps.scv;

import fi.stardex.sisu.pumps.CurrentPumpObtainer;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;

import java.util.Collections;
import java.util.List;


public class Close extends SCVCommon {

    protected HighPressureSectionController highPressureSectionController;

    public void setHighPressureSectionController(HighPressureSectionController highPressureSectionController) {
        this.highPressureSectionController = highPressureSectionController;
    }

    @Override
    public List<String> obtainLines() {
        return Collections.singletonList(String.format("Set SCV current %.2f A", CurrentPumpObtainer.getPump().getClosedScvDefCur()));
    }

    @Override
    public void execute() {
        highPressureSectionController.getCurrentReg2Spinner().getValueFactory().setValue(CurrentPumpObtainer.getPump().getClosedScvDefCur().doubleValue());
        highPressureSectionController.getCurrentReg3Spinner().getValueFactory().setValue(0d);
    }
}
