package fi.stardex.sisu.ui.controllers.pumps.pressure;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class PumpHighPressureSectionController {
    @FXML private StackPane pumpRegulatorSectionOne;
    @FXML private StackPane pumpRegulatorSectionTwo;
    @FXML private StackPane pumpRegulatorSectionThree;
    @FXML private StackPane pumpHighPressureSectionLcd;
    @FXML private StackPane pumpHighPressureSectionPwr;
    @FXML private StackPane rootStackPane;

    @FXML private PumpRegulatorSectionOneController pumpRegulatorSectionOneController;
    @FXML private PumpRegulatorSectionTwoController pumpRegulatorSectionTwoController;
    @FXML private PumpRegulatorSectionThreeController pumpRegulatorSectionThreeController;
    @FXML private PumpHighPressureSectionLcdController pumpHighPressureSectionLcdController;
    @FXML private PumpHighPressureSectionPwrController pumpHighPressureSectionPwrController;

    public PumpRegulatorSectionOneController getPumpRegulatorSectionOneController() {
        return pumpRegulatorSectionOneController;
    }

    public PumpRegulatorSectionTwoController getPumpRegulatorSectionTwoController() {
        return pumpRegulatorSectionTwoController;
    }

    public PumpRegulatorSectionThreeController getPumpRegulatorSectionThreeController() {
        return pumpRegulatorSectionThreeController;
    }

    public PumpHighPressureSectionLcdController getPumpHighPressureSectionLcdController() {
        return pumpHighPressureSectionLcdController;
    }

    public PumpHighPressureSectionPwrController getPumpHighPressureSectionPwrController() {
        return pumpHighPressureSectionPwrController;
    }
}
