package fi.stardex.sisu.util.enums;

public enum BeakerMode {

    RELATIVE, TESTS;

    private static BeakerMode currentMode = RELATIVE;

    public static void setCurrentMode(BeakerMode mode) {
        currentMode = mode;
    }

    public static BeakerMode getCurrentMode() {
        return currentMode;
    }

}
