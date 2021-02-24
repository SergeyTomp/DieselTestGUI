package fi.stardex.sisu.coding.delphi;

import fi.stardex.sisu.coding.Coder;
import fi.stardex.sisu.pdf.Result;

import java.util.LinkedList;
import java.util.List;

public abstract class DelphiCoder implements Coder {

    int injectorCoefficient;
    private List<Result> oldCodes;
    protected List<Integer> activeLEDs;
    List<String> previousResultList;

    DelphiCoder(List<Result> oldCodes) {
        this.oldCodes = oldCodes;
        makePreviousResultsList();
    }

    private void makePreviousResultsList() {

        previousResultList = new LinkedList<>();
        if (oldCodes.isEmpty()) {
            previousResultList.add("");
            previousResultList.add("");
            previousResultList.add("");
            previousResultList.add("");
        } else {
            previousResultList.add(oldCodes.get(0) != null ? oldCodes.get(0).getSubColumn1() : "" );
            previousResultList.add(oldCodes.get(1) != null ? oldCodes.get(1).getSubColumn1() : "" );
            previousResultList.add(oldCodes.get(2) != null ? oldCodes.get(2).getSubColumn1() : "" );
            previousResultList.add(oldCodes.get(3) != null ? oldCodes.get(3).getSubColumn1() : "" );
        }
    }
}
