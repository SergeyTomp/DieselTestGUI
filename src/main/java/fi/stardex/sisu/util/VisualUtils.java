package fi.stardex.sisu.util;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

public class VisualUtils {

    public static void showTooltipIncorrectInput(Button button) {
        showTooltipWithRedMessage(button, "Please fill in the fields correctly");
    }

    public static void showTooltipWithRedMessage(Button button, String message) {
        showTooltipWithMessage(button, message, "red");
    }

    public static void showTooltipWithMessage(Button button, String message, String color){
        Tooltip messageTooltip = new Tooltip();
        messageTooltip.setText(message);
        messageTooltip.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11pt;");
        messageTooltip.setAutoHide(true);
        Point2D p = button.localToScene(0.0, 0.0);
        messageTooltip.show(button, p.getX()
                + button.getScene().getX() + button.getScene().getWindow().getX(), p.getY()
                + button.getScene().getY() + button.getScene().getWindow().getY());
    }

    private static void progressBarStyle(ProgressBar progressBar, String style){
        progressBar.getStyleClass().clear();
        progressBar.getStyleClass().add("progress-bar");
        progressBar.getStyleClass().add(style);

    }

    public static void setPressureProgress(ProgressBar progressBar, Text text, double pressureValue){
        setProgress(3, 5, progressBar, text, pressureValue);
    }

    public void setTemperatureProgress(ProgressBar progressBar, Text text, double pressureValue){
        setProgress(40, 50, progressBar, text, pressureValue);
    }

    private static void setProgress(int left, int right, ProgressBar progressBar, Text text, double pressureValue){
        if (pressureValue <= left) {
            progressBarStyle(progressBar, "green-progress-bar");
        }
        if (pressureValue > left && pressureValue < right) {
            progressBarStyle(progressBar, "orange-progress-bar");
        }
        if (pressureValue >= right) {
            progressBarStyle(progressBar, "red-progress-bar");
        }
        text.setText(String.format("%.1f", pressureValue));
        progressBar.setProgress(pressureValue < 1 ? 1.0 : pressureValue);
    }


    public static void setVisible(boolean visible, Node... nodes){
        for (Node node: nodes) {
            if (node instanceof Toggle && !visible) {
                ((Toggle)node).setSelected(false);
            }
            node.setVisible(visible);
        }
    }

    public static void copyToClipBoard(TableView<?> table){
        ObservableList<TablePosition> positionList = table.getSelectionModel().getSelectedCells();
        for (TablePosition position : positionList){
            int row = position.getRow();
            int col = position.getColumn();
            Object cell = table.getColumns().get(col).getCellData(row);
            final ClipboardContent content = new ClipboardContent();
            content.putString(cell.toString());
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

}
