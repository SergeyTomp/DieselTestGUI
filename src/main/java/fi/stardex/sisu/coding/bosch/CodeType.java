package fi.stardex.sisu.coding.bosch;

public enum CodeType {

    ZERO(0, 5),
    ONE(1, 5),
    TWO(2, 5),
    THREE(3, 4),
    FOUR(4, 4),
    FIVE(5, 5),
    SIX(6, 4),
    IMA11_1(111, 5),
    IMA11_2(112, 5),
    IMA11_3(113, 5),
    IMA11_4(114, 5);

    private final int codeType;
    private final int step;

    CodeType(int codeType, int step) {

        this.codeType = codeType;
        this.step = step;
    }

    public static CodeType getCodeType(int codeType) {

        for (CodeType codeTypes : values()) {
            if (codeTypes.codeType == codeType)
                return codeTypes;
        }
        return ONE;
    }
}
