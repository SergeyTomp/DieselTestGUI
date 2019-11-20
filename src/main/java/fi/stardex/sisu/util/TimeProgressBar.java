package fi.stardex.sisu.util;

import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class TimeProgressBar {

    private ProgressBar progressBar;
    private Text text;
    private int initialTime;

    public TimeProgressBar(ProgressBar progressBar, Text text) {
        this.progressBar = progressBar;
        this.text = text;
    }

    public void setProgress(int time) {
        this.initialTime = time;
        text.setText(String.valueOf(initialTime));
        progressBar.setProgress(initialTime == 0 ? 0 : 1);
    }

    public void refreshProgress(double initial, double current) {

        progressBar.setProgress(current / initial);
        text.setText(String.valueOf((int)current));
    }

    public int tick() {

        int time = Integer.valueOf(text.getText());

        if (time > 0) {
            text.setText(String.valueOf(--time));
            progressBar.setProgress((float) time / (float) initialTime);
        }
        return time;
    }
}
