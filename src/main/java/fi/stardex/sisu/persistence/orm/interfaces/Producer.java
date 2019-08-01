package fi.stardex.sisu.persistence.orm.interfaces;

public interface Producer {

    String getManufacturerName();
    boolean isCustom();
    int getDisplayOrder();
    void setCustom(boolean custom);
    void setManufacturerName(String manufacturerName);
}
