package fi.stardex.sisu.ui.controllers.pumps.abstracts;

import javafx.scene.control.SelectionModel;

public abstract class FxListSelection <T extends SelectionModel> implements Selection{

    private T selectionModel;

    public FxListSelection(T selectionModel) {
        this.selectionModel = selectionModel;
    }

    public T getSelectionModel() {
        return selectionModel;
    }
}
