package fi.stardex.sisu.pdf.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeasurementHeader implements Header {

    @Override
    public String getMainHeader() {
        return "Electrical Characteristics";
    }

    @Override
    public List<HeaderCell> getHeaderCells() {
        return new ArrayList<>(Arrays.asList(new HeaderCell(25, "Parameter Name"), new HeaderCell(25, "Measurement Units"),
                new HeaderCell(12.5f, "(1)"), new HeaderCell(12.5f, "(2)"), new HeaderCell(12.5f, "(3)"), new HeaderCell(12.5f, "(4)")));
    }
}
