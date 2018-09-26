package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.util.enums.ISAMaskType;

import javax.persistence.*;

@Entity
@Table(name = "isa_Detection")
public class ISADetection {

    public static final int ISA_CHARS_NUMBER = 16;

    @Id
    @Column(name = "injector_code")
    private String injectorCode;

    @Column(name = "mask_type")
    @Enumerated(EnumType.STRING)
    private ISAMaskType maskType;

    @Column(name = "shift_voltage")
    private Integer shiftVoltage;

    @Column
    private int char_0;

    @Column
    private int char_1;

    @Column
    private int char_2;

    @Column
    private int char_3;

    @Column
    private int char_4;

    @Column
    private int char_5;

    @Column
    private int char_6;

    @Column
    private int char_7;

    @Column
    private int char_8;

    @Column
    private int char_9;

    @Column
    private int char_10;

    @Column
    private int char_11;

    @Column
    private int char_12;

    @Column
    private int char_13;

    @Column
    private int char_14;

    @Column
    private int char_15;

    private Integer[] charArray;

    public Integer[] getCharArray() {

        if (charArray == null)
            return new Integer[]{char_0, char_1, char_2, char_3, char_4, char_5, char_6, char_7, char_8, char_9, char_10, char_11, char_12, char_13, char_14, char_15};
        return charArray;

    }

    public Integer getShiftVoltage() {
        return shiftVoltage;
    }

    public ISAMaskType getMaskType() {
        return maskType;
    }

}
