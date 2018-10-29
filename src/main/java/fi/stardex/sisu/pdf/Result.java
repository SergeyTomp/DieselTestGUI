package fi.stardex.sisu.pdf;

import java.util.List;

/**
 * Created
 * by eduard
 * on 05.04.17.
 */
public interface Result {

    String getMainColumn();
    String getSubColumn1();
    String getSubColumn2();
    List<String> getValueColumns();
}
