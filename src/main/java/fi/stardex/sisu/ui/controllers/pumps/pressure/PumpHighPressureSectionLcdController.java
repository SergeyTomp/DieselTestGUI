package fi.stardex.sisu.ui.controllers.pumps.pressure;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.model.updateModels.HighPressureSectionUpdateModel;
import fi.stardex.sisu.util.GaugeCreator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

public class PumpHighPressureSectionLcdController {

    @FXML private GridPane grid_2;
    @FXML private GridPane grid_1;
    @FXML private StackPane lcdStackPane;

    private Lcd pressureLcd;
    private Lcd pressureLcd_2;
    private StackPane rootStackPane;

    private HighPressureSectionUpdateModel highPressureSectionUpdateModel;

    public void setHighPressureSectionUpdateModel(HighPressureSectionUpdateModel highPressureSectionUpdateModel) {
        this.highPressureSectionUpdateModel = highPressureSectionUpdateModel;
    }

    public StackPane getLcdStackPane() {
        return lcdStackPane;
    }

    @PostConstruct
    public void init(){

        pressureLcd = GaugeCreator.createLcd("bar");
        pressureLcd_2 = GaugeCreator.createLcd("bar");
        grid_2.add(pressureLcd_2, 0, 1);
        configureLayout(1);

        rootStackPane = (StackPane) lcdStackPane.getParent().getParent();
        rootStackPane.widthProperty().addListener(new StackPaneWidthListener(rootStackPane, lcdStackPane));
        pressureLcd.valueProperty().bind(highPressureSectionUpdateModel.lcdPressureProperty());
    }

    private void configureLayout(int qty) {
        lcdStackPane.getChildren().clear();
        switch (qty) {
            case 2:
                grid_2.add(pressureLcd, 0, 0);
                lcdStackPane.getChildren().add(grid_2);
                break;
            case 1:
            default:
                grid_1.add(pressureLcd, 0, 0);
                lcdStackPane.getChildren().add(grid_1);
                break;
        }
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
