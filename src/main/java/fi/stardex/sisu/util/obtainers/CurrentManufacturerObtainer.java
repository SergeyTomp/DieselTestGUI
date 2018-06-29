package fi.stardex.sisu.util.obtainers;

import fi.stardex.sisu.persistence.orm.Manufacturer;

public class CurrentManufacturerObtainer {
    private Manufacturer currentManufacturer;

    public Manufacturer getCurrentManufacturer() {
        return currentManufacturer;
    }

    public void setDefaultManufacturer(Manufacturer currentManufacturer) {
        this.currentManufacturer = currentManufacturer;
    }
}
