package fi.stardex.sisu.model.pump;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FlowRangeModel {

//    private StringProperty flowRangeProperty = new SimpleStringProperty();
    private StringProperty flowRangeLabelProperty = new SimpleStringProperty(); //just the label text above beaker, no real flow range information for calculations.
    private double scaledLowLimit;
    private double scaledTopLimit; // if flow top limit is absent this value is used for beaker scaling only
    private boolean noLowLimit;
    private boolean noTopLimit;

//    public StringProperty flowRangeProperty() {
//        return flowRangeProperty;
//    }
    public StringProperty flowRangeLabelProperty() {
        return flowRangeLabelProperty;
    }

    public double getScaledLowLimit() {
        return scaledLowLimit;
    }
    public void setScaledLowLimit(double scaledLowLimit) {
        this.scaledLowLimit = scaledLowLimit;
    }
    public double getScaledTopLimit() {
        return scaledTopLimit;
    }
    public void setScaledTopLimit(double scaledTopLimit) {
        this.scaledTopLimit = scaledTopLimit;
    }
    public boolean isNoLowLimit() {
        return noLowLimit;
    }
    public void setNoLowLimit(boolean noLowLimit) {
        this.noLowLimit = noLowLimit;
    }
    public boolean isNoTopLimit() {
        return noTopLimit;
    }
    public void setNoTopLimit(boolean noTopLimit) {
        this.noTopLimit = noTopLimit;
    }
}
