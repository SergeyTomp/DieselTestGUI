package fi.stardex.sisu.util.enums.uis;

import org.springframework.core.Ordered;

public enum RpmSource implements Ordered {

    EXTERNAL(0, 1),
    INTERNAL(1, 0);

    int order;
    int sourceId;

    RpmSource(int order, int sourceId) {
        this.order = order;
        this.sourceId = sourceId;
    }

    public int getSourceId() {
        return sourceId;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
