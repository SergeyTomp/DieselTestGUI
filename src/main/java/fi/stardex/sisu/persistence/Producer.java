package fi.stardex.sisu.persistence;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;

import java.util.List;

public interface Producer {

    void onPostPersist();
    void onPostRemove();
    List<Injector> getInjectors();
    void setManufacturerName(String manufacturerName);
    String getManufacturerName();
    String toString();
    boolean isCustom();
    void setCustom(boolean custom);
}
