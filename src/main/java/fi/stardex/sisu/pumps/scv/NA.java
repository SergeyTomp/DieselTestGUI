package fi.stardex.sisu.pumps.scv;

import java.util.Collections;
import java.util.List;


public class NA extends SCVCommon {

    @Override
    public List<String> obtainLines() {
        return Collections.emptyList();
    }

    @Override
    public void execute() {
        //do not need to execute N/A SCV
    }
}
