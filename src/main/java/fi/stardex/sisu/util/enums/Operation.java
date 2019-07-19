package fi.stardex.sisu.util.enums;

public enum Operation {

    NEW("New "),
    DELETE("Delete "),
    EDIT("Edit "),
    COPY("Copy ");

    private String title;

    Operation(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
