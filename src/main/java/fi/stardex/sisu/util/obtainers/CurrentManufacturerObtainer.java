package fi.stardex.sisu.util.obtainers;

import fi.stardex.sisu.persistence.orm.cr.inj.Manufacturer;

public class CurrentManufacturerObtainer {

    private static Manufacturer manufacturer;

    public static Manufacturer getManufacturer() {
        return manufacturer;
    }

    public static void setManufacturer(Manufacturer newManufacturer) {
        manufacturer = newManufacturer;
    }

}
