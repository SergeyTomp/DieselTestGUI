package fi.stardex.sisu.pdf.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlowHeader implements Header {

    @Override
    public String getMainHeader() {
        return "Flow Measurement Results";
    }

    @Override
    public List<HeaderCell> getHeaderCells() {
        return new ArrayList<>(Arrays.asList(new HeaderCell(19, "Test Name"), new HeaderCell(21, "Delivery/BackFlow"),
                new HeaderCell(24, "Nominal Flow Range"), new HeaderCell(9, "(1)"), new HeaderCell(9, "(2)"),
                new HeaderCell(9, "(3)"), new HeaderCell(9, "(4)")));
    }
}
