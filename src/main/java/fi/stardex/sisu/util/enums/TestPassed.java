package fi.stardex.sisu.util.enums;

import javafx.scene.image.Image;

public enum TestPassed {

    PASSED("/img/apply.png"),
    FAIL("/img/cancel.png");

    private final Image pict;

    TestPassed(String url) {
        // load image in background
        pict = new Image(url);
    }

    public Image getPict() {
        return pict;
    }

}
