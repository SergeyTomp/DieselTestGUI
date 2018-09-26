package fi.stardex.sisu.util.enums;

public enum ISAMaskType {

    ONE("ABCDEFGHIKLMNOPR"), TWO("ABCDIIIIIIIMNOPR");

    private final String mask;

    ISAMaskType(String mask) {
        this.mask = mask;
    }

    public String getMask() {
        return mask;
    }
}
