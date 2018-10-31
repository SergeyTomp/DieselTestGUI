package fi.stardex.sisu.util;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

public class CopyUtils {

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
