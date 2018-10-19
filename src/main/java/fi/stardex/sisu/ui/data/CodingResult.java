package fi.stardex.sisu.ui.data;

import java.util.List;

public class CodingResult implements Result {

    private String injectorNumber;
    private String generatedCode;

    public CodingResult(String injectorNumber, String generatedCode) {
        this.injectorNumber = injectorNumber;
        this.generatedCode = generatedCode;
    }

    @Override
    public String getMainColumn() {
        return injectorNumber;
    }

    @Override
    public String getSubColumn1() {
        return generatedCode;
    }

    @Override
    public String getSubColumn2() {
        return null;
    }

    @Override
    public List<String> getValueColumns() {
        return null;
    }
}
