package fi.stardex.sisu.annotations;

import fi.stardex.sisu.devices.Device;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    Device[] value();
}
