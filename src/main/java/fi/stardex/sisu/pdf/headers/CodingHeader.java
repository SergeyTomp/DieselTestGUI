package fi.stardex.sisu.pdf.headers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodingHeader  implements Header {

    public String getMainHeader() {
        return "Coding Results";
    }

    public List<HeaderCell> getHeaderCells() {
        return new ArrayList<>(Arrays.asList(new HeaderCell(30, "Injector Number"), new HeaderCell(70, "Code")));
    }
}
