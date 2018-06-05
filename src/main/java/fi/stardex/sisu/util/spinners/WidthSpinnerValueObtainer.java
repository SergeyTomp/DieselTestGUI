package fi.stardex.sisu.util.spinners;

public class WidthSpinnerValueObtainer extends SpinnerValueObtainer {

    private int generatedFakeValue;

    public WidthSpinnerValueObtainer(int initValue) {
        super(initValue);
    }

    public int getGeneratedFakeValue() {
        return generatedFakeValue;
    }

    public void setGeneratedFakeValue(int generatedFakeValue) {
        this.generatedFakeValue = generatedFakeValue;
    }
}
