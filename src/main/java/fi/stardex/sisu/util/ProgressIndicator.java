package fi.stardex.sisu.util;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProgressIndicator {

    private ProgressBar progressBar;
    private Label progressLabel;
    private FloatProperty progressValue;
    private BooleanProperty showProgress;
    private Set<Node> hideSet;

    private ChangeListener<? super Boolean> visibilityListener;
    private ChangeListener<? super  Number> progressListener;

    private ProgressIndicator(ProgressIndicatorBuilder builder) {

        this.progressBar = builder.progressBar;
        this.progressLabel = builder.progressLabel;
        this.showProgress = builder.showProgress == null ? new SimpleBooleanProperty(true) : builder.showProgress;
        this.progressValue = builder.progressValue == null ? new SimpleFloatProperty(0) : builder.progressValue;
        this.hideSet = builder.hideSet;
        setListeners();
    }

    private void setListeners() {

        visibilityListener = (ChangeListener<Boolean>) (observableValue, oldValue, newValue) -> hideSet.forEach(n -> n.setVisible(newValue));

        progressListener = (ChangeListener<Number>) (observableValue, oldValue, newValue) -> {
            if (progressBar != null) { Platform.runLater(() -> progressBar.setProgress(newValue.floatValue())); }
            if (progressLabel != null) { Platform.runLater(() -> progressLabel.setText(String.valueOf(Math.round(newValue.floatValue() * 100))));}
        };
    }

    public void startIndication() {

        showProgress.addListener(visibilityListener);
        progressValue.addListener(progressListener);
        if (showProgress.get()) {
            hideSet.forEach(n -> n.setVisible(true));
        }
    }

    public void stopIndication() {

        showProgress.removeListener(visibilityListener);
        progressValue.removeListener(progressListener);
        hideSet.forEach(n -> n.setVisible(false));
    }

    public static class ProgressIndicatorBuilder {

        private ProgressBar progressBar;
        private Label progressLabel;
        private FloatProperty progressValue;
        private BooleanProperty showProgress;
        private Set<Node> hideSet;

        ProgressIndicatorBuilder() { }

        public ProgressIndicatorBuilder progressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
            return this;
        }
        public ProgressIndicatorBuilder progressLabel(Label progressLabel) {
            this.progressLabel = progressLabel;
            return this;
        }
        public ProgressIndicatorBuilder progressValue(FloatProperty progressValue) {
            this.progressValue = progressValue;
            return this;
        }
        public ProgressIndicatorBuilder showProgress(BooleanProperty showProgress) {
            this.showProgress = showProgress;
            return this;
        }
        public ProgressIndicatorBuilder hide(Node... hide) {
            if (hideSet == null) { hideSet = new HashSet<>(); }
            hideSet.addAll(Arrays.asList(hide));
            return this;
        }

        public ProgressIndicator build() {
            return new ProgressIndicator(this);
        }
    }

    public static ProgressIndicatorBuilder create() {
        return new ProgressIndicatorBuilder();
    }
}
