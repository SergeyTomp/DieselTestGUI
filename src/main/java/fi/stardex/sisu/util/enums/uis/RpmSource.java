package fi.stardex.sisu.util.enums.uis;

import org.springframework.core.Ordered;

public enum RpmSource implements Ordered {

    EXTERNAL(0),
    INTERNAL(1);

    int order;

    RpmSource(int order) {
        this.order = order;
    }


    @Override
    public int getOrder() {
        return order;
    }
}
