package fi.stardex.sisu.ui.controllers.pumps.abstracts;

import javafx.scene.control.Control;

public abstract class FxNodeAction<T extends Control> implements Action{

    private T tb;

    public FxNodeAction(T tb) {
        this.tb = tb;
    }

    public T getTb() {
        return tb;
    }
}
