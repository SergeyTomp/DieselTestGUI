package fi.stardex.sisu.ui.controllers.pumps.pressure;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.util.GaugeCreator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

public class PumpHighPressureSectionLcdController {

    @FXML private StackPane lcdStackPane;

    private Lcd pressureLcd;
    private StackPane rootStackPane;

    public StackPane getLcdStackPane() {
        return lcdStackPane;
    }

    @PostConstruct
    public void init(){

        lcdStackPane.getChildren().add(0, GaugeCreator.createLcd());
        rootStackPane = (StackPane) lcdStackPane.getParent().getParent();
        rootStackPane.widthProperty().addListener(new StackPaneWidthListener(rootStackPane, lcdStackPane));
    }

    private class StackPaneWidthListener implements ChangeListener<Number> {

        private final StackPane rootStackPane;
        private final StackPane lcdStackPane;

        public StackPaneWidthListener(StackPane rootStackPane, StackPane lcdStackPane) {
            this.rootStackPane = rootStackPane;
            this.lcdStackPane = lcdStackPane;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double tempWidth = rootStackPane.getWidth() / 7.416;
            if (tempWidth < 192) {
                if (lcdStackPane.getWidth() > 150) {
                    lcdStackPane.setPrefWidth(tempWidth);

                } else {
                    lcdStackPane.setPrefWidth(135);
                }
            } else {
                lcdStackPane.setPrefWidth(192);
            }
        }
    }
}
