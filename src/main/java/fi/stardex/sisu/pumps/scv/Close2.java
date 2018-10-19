package fi.stardex.sisu.pumps.scv;

import fi.stardex.sisu.pumps.CurrentPumpObtainer;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class Close2 extends SCVCommon {

    protected HighPressureSectionController highPressureSectionController;

    public void setHighPressureSectionController(HighPressureSectionController highPressureSectionController) {
        this.highPressureSectionController = highPressureSectionController;
    }

    @Override
    public List<String> obtainLines() {
        return Arrays.asList(
                String.format("Set SCV 1 current %.2f A", CurrentPumpObtainer.getPump().getClosedScvDefCur()),
                String.format("Set SCV 2 current %.2f A", CurrentPumpObtainer.getPump().getClosedScvDefCur()));
    }

    @Override
    public void execute() {
        highPressureSectionController.getCurrentReg2Spinner().getValueFactory().setValue(CurrentPumpObtainer.getPump().getClosedScvDefCur().doubleValue());
        highPressureSectionController.getCurrentReg3Spinner().getValueFactory().setValue(CurrentPumpObtainer.getPump().getClosedScvDefCur().doubleValue());
    }
}
