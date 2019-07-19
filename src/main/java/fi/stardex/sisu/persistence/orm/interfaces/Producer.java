package fi.stardex.sisu.persistence.orm.interfaces;

public interface Producer {

    String getManufacturerName();
    boolean isCustom();
    void setCustom(boolean custom);
    void setManufacturerName(String manufacturerName);
}
