package fi.stardex.sisu.util.enums;

import org.springframework.core.Ordered;

public enum Theme implements Ordered {

    STANDARD("Styling.css", 0), LIGHT("Light.css", 1);

    private String file;
    private int order;

    Theme(String file, int order) {
        this.file = file;
        this.order = order;
    }

    public String getFile() {
        return file;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
