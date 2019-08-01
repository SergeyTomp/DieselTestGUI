package fi.stardex.sisu.persistence.orm.interfaces;

@FunctionalInterface
public interface Model {

    String getModelCode();
    default Producer getManufacturer(){return null;}
    default Boolean isCustom(){return null;}
    default VAP getVAP(){return null;}
    default Integer getCodetype(){return null;}
    default void setVAP(VAP vap){}
}
