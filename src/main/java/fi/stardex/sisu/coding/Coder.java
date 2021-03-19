package fi.stardex.sisu.coding;

import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;

import java.util.List;

public interface Coder {

    List<String> buildCode();
}
