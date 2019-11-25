package fi.stardex.sisu.measurement;

import fi.stardex.sisu.util.enums.GUI_type;

public class TestManagerFactory {

    private CrTestManager crTestManager;
    private PumpTestManager pumpTestManager;
    private UisTestManager uisTestManager;

    public void setCrTestManager(CrTestManager crTestManager) {
        this.crTestManager = crTestManager;
    }
    public void setPumpTestManager(PumpTestManager pumpTestManager) {
        this.pumpTestManager = pumpTestManager;
    }
    public void setUisTestManager(UisTestManager uisTestManager) {
        this.uisTestManager = uisTestManager;
    }

    public TestManager getTestManager(GUI_type guiType) {

        TestManager testManager;

        switch (guiType) {
            case UIS:
                testManager = uisTestManager;
                break;
            case HEUI:
            case CR_Inj:
                testManager = crTestManager;
                break;
            case CR_Pump:
                testManager = pumpTestManager;
                break;
            default:
                throw new RuntimeException(" In TestManagerFactory: unknown GUI type selected !");
        }
        return testManager;
    }
}
