package fi.stardex.sisu.util.spinners;

public class SpinnerValueObtainer {

    private Number initialSpinnerOldValue;

    private Number oldValue;

    public SpinnerValueObtainer(Number initValue) {
        oldValue = initValue;
    }

    public Number getOldValue() {
        return oldValue;
    }

    public void setOldValue(Number oldValue) {
        this.oldValue = oldValue;
    }

    public Number getInitialSpinnerOldValue() {
        return initialSpinnerOldValue;
    }

    public void setInitialSpinnerOldValue(Number initialSpinnerOldValue) {
        this.initialSpinnerOldValue = initialSpinnerOldValue;
    }

}
