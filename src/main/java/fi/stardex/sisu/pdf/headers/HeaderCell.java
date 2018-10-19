package fi.stardex.sisu.pdf.headers;


public class HeaderCell {

    private float width;
    private String value;

    public HeaderCell(float width, String value) {
        this.width = width;
        this.value = value;
    }

    public float getWidth() {
        return width;
    }

    public String getValue() {
        return value;
    }
}
