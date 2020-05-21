package fi.stardex.sisu.util.enums;

import java.util.Arrays;

public enum GUI_type {

    CR_Inj("CR INJ"), CR_Pump("CR PUMP"), UIS("UIS"), HEUI("HEUI");

    String type;

    GUI_type(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static GUI_type getType(String type){

       return Arrays.stream(values()).filter(v -> v.toString().equals(type)).findFirst().orElse(CR_Inj);
    }
}
