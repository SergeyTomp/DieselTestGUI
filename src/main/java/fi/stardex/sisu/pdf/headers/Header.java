package fi.stardex.sisu.pdf.headers;

import java.util.List;

/**
 * Created
 * by eduard
 * on 03.04.17.
 */
public interface Header {

    String getMainHeader();
    List<HeaderCell> getHeaderCells();
}
