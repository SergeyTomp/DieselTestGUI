package fi.stardex.sisu.main;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

public abstract class LogoController {


    public abstract BorderPane getRootBorderPane();
    public abstract ProgressBar getProgressBar();
    public abstract Label getVersionLabel();
}
