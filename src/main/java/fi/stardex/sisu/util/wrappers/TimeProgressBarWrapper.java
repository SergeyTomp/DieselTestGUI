package fi.stardex.sisu.util.wrappers;

import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import org.codehaus.groovy.runtime.StringGroovyMethods;

public class TimeProgressBarWrapper {

    private ProgressBar progressBar;
    private Text text;
    private Integer beginTime;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Integer getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Integer beginTime) {
        this.beginTime = beginTime;
    }

    public TimeProgressBarWrapper(ProgressBar progressBar, Text text) {
        this.progressBar = progressBar;
        this.text = text;
    }

    public void time(Integer time) {
        if (time == null) {
            beginTime = 0;
        } else {
            beginTime = time;
        }

        text.setText(String.valueOf(beginTime));
        progressBar.setProgress(beginTime == 0 ? 0 : 1);
    }

    public void refresh() {
        time(beginTime);
    }

    public Integer tick() {
        Integer time = StringGroovyMethods.toInteger(text.getText());
        if (time > 0) {
            text.setText(String.valueOf((time = --time)));
            progressBar.setProgress(time / beginTime);
        }

        return time;
    }

    public boolean isFinished() {
        return StringGroovyMethods.toInteger(text.getText()) == 0;
    }

}
