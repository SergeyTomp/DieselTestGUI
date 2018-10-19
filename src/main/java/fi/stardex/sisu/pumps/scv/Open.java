package fi.stardex.sisu.pumps.scv;

import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;

import java.util.Collections;
import java.util.List;


public class Open extends SCVCommon {

    protected HighPressureSectionController highPressureSectionController;

    public void setHighPressureSectionController(HighPressureSectionController highPressureSectionController) {
        this.highPressureSectionController = highPressureSectionController;
    }

    @Override
    public List<String> obtainLines() {
        return Collections.singletonList("Set SCV current 0 A");
    }

    @Override
    public void execute() {
        highPressureSectionController.getCurrentReg2Spinner().getValueFactory().setValue(0d);
        highPressureSectionController.getCurrentReg3Spinner().getValueFactory().setValue(0d);
    }
}
