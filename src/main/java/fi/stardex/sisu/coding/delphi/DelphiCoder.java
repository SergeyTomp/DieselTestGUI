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
    final String TP01 = "Test Point 01";
    final String TP02 = "Test Point 02";
    final String TP03 = "Test Point 03";
    final String TP04 = "Test Point 04";
    final String TP05 = "Test Point 05";
    final String TP06 = "Test Point 06";
    final String TP07 = "Test Point 07";
    final String TP08 = "Test Point 08";
    final String TP09 = "Test Point 09";
    final String TP10 = "Test Point 10";
    final String TP11 = "Test Point 11";
    final String TP12 = "Test Point 12";
    final String TP13 = "Test Point 13";
    final String TP14 = "Test Point 14";
    final String TP15 = "Test Point 15";
    final String ALPHABET = "0123456789ABCDEFGHJKLMNPRSTUWXYZ";

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
