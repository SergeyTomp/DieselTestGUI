package fi.stardex.sisu.ui.controllers.cr;

import fi.stardex.sisu.states.RegulatorsQTYState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

public class InjectorHighPressureSectionController {

    @FXML
    private StackPane rootStackPane;
    @FXML
    private StackPane highPressureSectionOne;
    @FXML
    private StackPane highPressureSectionTwo;
    @FXML
    private StackPane highPressureSectionThree;
    @FXML
    private StackPane highPressureSectionLcd;
    @FXML
    private StackPane highPressureSectionPwr;

    @FXML
    private HighPressureSectionOneController highPressureSectionOneController;
    @FXML
    private HighPressureSectionTwoController highPressureSectionTwoController;
    @FXML
    private HighPressureSectionThreeController highPressureSectionThreeController;
    @FXML
    private HighPressureSectionLcdController highPressureSectionLcdController;
    @FXML
    private HighPressureSectionPwrController highPressureSectionPwrController;

    private RegulatorsQTYState regulatorsQTYState;

    public HighPressureSectionOneController getHighPressureSectionOneController() {
        return highPressureSectionOneController;
    }

    public HighPressureSectionTwoController getHighPressureSectionTwoController() {
        return highPressureSectionTwoController;
    }

    public HighPressureSectionThreeController getHighPressureSectionThreeController() {
        return highPressureSectionThreeController;
    }

    public HighPressureSectionLcdController getHighPressureSectionLcdController() {
        return highPressureSectionLcdController;
    }

    public HighPressureSectionPwrController getHighPressureSectionPwrController() {
        return highPressureSectionPwrController;
    }

    public void setRegulatorsQTYState(RegulatorsQTYState regulatorsQTYState) {
        this.regulatorsQTYState = regulatorsQTYState;
    }

    @PostConstruct
    public void init() {
        configRegulatorsInvolved(Integer.parseInt(regulatorsQTYState.regulatorsQTYStateProperty().get()));
        regulatorsQTYState.regulatorsQTYStateProperty().addListener(new RegulatorsConfigListener());

    }

    private class RegulatorsConfigListener implements ChangeListener<String> {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            switch (Integer.parseInt(newValue)) {
                case 1:
                    configRegulatorsInvolved(1);
                    break;
                case 2:
                    configRegulatorsInvolved(2);
                    break;
                case 3:
                    configRegulatorsInvolved(3);
                    break;
                default:
                    configRegulatorsInvolved(3);
                    break;
            }
        }
    }

    private void configRegulatorsInvolved(int number) {
        switch (number) {
            case 1:
                highPressureSectionTwo.getChildren().forEach(c -> c.setVisible(false));
                highPressureSectionThree.getChildren().forEach(c -> c.setVisible(false));
                break;
            case 2:
                highPressureSectionTwo.getChildren().forEach(c -> c.setVisible(true));
                highPressureSectionThree.getChildren().forEach(c -> c.setVisible(false));
                break;
            case 3:
                highPressureSectionTwo.getChildren().forEach(c -> c.setVisible(true));
                highPressureSectionThree.getChildren().forEach(c -> c.setVisible(true));
                break;
            default:
                highPressureSectionTwo.getChildren().forEach(c -> c.setVisible(true));
                highPressureSectionThree.getChildren().forEach(c -> c.setVisible(true));
                break;

        }
    }
}
