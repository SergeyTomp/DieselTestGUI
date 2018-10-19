package fi.stardex.sisu.pumps.scv;

/**
 * @author rom8
 * @since 01.04.16
 */
public interface CurrentSCVObtainer {

    public abstract SCV getSCV();

    public abstract void update(SCVEnum scvEnum);
}
