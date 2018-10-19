package fi.stardex.sisu.pumps.tests;

import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.ui.data.SavedPumpBeakerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RescalincgPumpTest extends ExtendedPumpTest {

    private SavedPumpBeakerData savedDataForBeakersPump;
//


    public void setSavedDataForBeakersPump(SavedPumpBeakerData savedDataForBeakersPump) {
        this.savedDataForBeakersPump = savedDataForBeakersPump;
    }

    @Override
    public void withTiming() {
//        enabler.timingCheckbox(false, false);
    }
//
//
    public void setPumpBeakersData(){


//
//        beakers.reInstallPumpBeakers();
//
//        if (savedDataForBeakersPump.getSavedDataForBeakers().containsKey(currentPumpObtainer.getPump())) {
//
//            Map<Test, ArrayList<Double>> testData = savedDataForBeakersPump.getSavedDataForBeakers().get(currentPumpObtainer.getPump());
//            if (testData.containsKey(this)) {
//                List<Double> savedData = testData.get(this);
//                additionalSectionController.getCleverCurrentValuePumpDelivText().set(savedData.get(0));
//                additionalSectionController.getCleverCurrentValuePumpBackFlowText().set(savedData.get(1));
//            }
//        }
    }

}
