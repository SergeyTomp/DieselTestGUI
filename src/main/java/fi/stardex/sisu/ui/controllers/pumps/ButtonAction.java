package fi.stardex.sisu.ui.controllers.pumps;

import fi.stardex.sisu.ui.controllers.pumps.abstracts.FxNodeAction;
import javafx.scene.control.ToggleButton;

public class ButtonAction extends FxNodeAction<ToggleButton> {

    public ButtonAction(ToggleButton tb) {
        super(tb);
    }

    @Override
    public void act(boolean state) {

        getTb().setSelected(state);
    }
}
