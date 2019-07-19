package fi.stardex.sisu.persistence.orm.interfaces;

public interface Model {

    String getModelCode();
    Producer getManufacturer();
    Boolean isCustom();
}
