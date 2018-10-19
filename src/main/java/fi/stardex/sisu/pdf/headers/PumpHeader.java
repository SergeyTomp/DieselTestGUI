package fi.stardex.sisu.pdf.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PumpHeader implements Header {
    @Override
    public String getMainHeader() {
        return "Pump Flow Measurement Results";
    }

    @Override
    public List<HeaderCell> getHeaderCells() {
        return new ArrayList<>(Arrays.asList(new HeaderCell(15, "Test Name"), new HeaderCell(12, "RPM"),
                new HeaderCell(12, "Bar"), new HeaderCell(24, "Nominal Flow Range"), new HeaderCell(22, "Delivery/BackFlow"),
                new HeaderCell(15, "Value")));
    }
}
