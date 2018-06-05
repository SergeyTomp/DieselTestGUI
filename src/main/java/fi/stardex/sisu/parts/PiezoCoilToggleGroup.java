package fi.stardex.sisu.parts;

import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class PiezoCoilToggleGroup {

    private ToggleGroup piezoCoilToggleGroup;

    private RadioButton piezoRadioButton;

    private RadioButton piezoDelphiRadioButton;

    private RadioButton coilRadioButton;

    public ToggleGroup getPiezoCoilToggleGroup() {
        return piezoCoilToggleGroup;
    }

    public RadioButton getPiezoRadioButton() {
        return piezoRadioButton;
    }

    public RadioButton getPiezoDelphiRadioButton() {
        return piezoDelphiRadioButton;
    }

    public RadioButton getCoilRadioButton() {
        return coilRadioButton;
    }

    public PiezoCoilToggleGroup(InjectorSectionController injectorSectionController) {
        piezoCoilToggleGroup = injectorSectionController.getPiezoCoilToggleGroup();
        piezoRadioButton = injectorSectionController.getPiezoRadioButton();
        piezoDelphiRadioButton = injectorSectionController.getPiezoDelphiRadioButton();
        coilRadioButton = injectorSectionController.getCoilRadioButton();
    }

}
