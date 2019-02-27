package fi.stardex.sisu.ui.controllers.pumps;

import fi.stardex.sisu.ui.controllers.pumps.abstracts.FxListSelection;
import javafx.scene.control.MultipleSelectionModel;

public class ListSelection extends FxListSelection<MultipleSelectionModel> {

    public ListSelection(MultipleSelectionModel sm) {
        super(sm);
    }

    @Override
    public void select(int index) {

        getSelectionModel().select(index);
    }
}
