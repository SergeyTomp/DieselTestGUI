package fi.stardex.sisu.pumps.scv;


public enum SCVEnum {

    NA(NA.class),
    OPEN(Open.class),
    CLOSE(Close.class),
    CLOSE_2(Close2.class),
    CLOSED(Closed.class);    //for siemens - CLOSED

    private final Class<? extends SCVCommon> scvClass;

    SCVEnum(Class<? extends SCVCommon> scvClass) {
        this.scvClass = scvClass;
    }

    public Class<? extends SCVCommon> getScvClass() {
        return scvClass;
    }
}
