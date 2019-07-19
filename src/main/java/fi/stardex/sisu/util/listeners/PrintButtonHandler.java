package fi.stardex.sisu.util.listeners;

import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.cr.dialogs.PrintDialogPanelController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PrintButtonHandler implements EventHandler<ActionEvent> {

    private Stage printStage;
    private ViewHolder printDialogPanel;

    public PrintButtonHandler(Stage printStage,
                              ViewHolder printDialogPanel) {
        this.printStage = printStage;
        this.printDialogPanel = printDialogPanel;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (printStage == null) {
            printStage = new Stage();
            printStage.setTitle("PDF export");
            printStage.setResizable(false);
            printStage.initModality(Modality.APPLICATION_MODAL);
            ((PrintDialogPanelController) printDialogPanel.getController()).setStage(printStage);
        }
            /* Check is necessary due to possibility of dialog window invocation from different controllers.
                In this case we need to avoid repeated set of printDialogPanel as root of another new scene -
                IllegalArgumentException: .... is already set as root of another scene. */
        if (printDialogPanel.getView().getScene() == null) {
            printStage.setScene(new Scene(printDialogPanel.getView()));
        }else{
            printStage.setScene(printDialogPanel.getView().getScene());
        }
        printStage.show();
    }
}
