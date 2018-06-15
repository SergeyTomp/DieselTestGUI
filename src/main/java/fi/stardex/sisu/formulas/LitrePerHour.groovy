package fi.stardex.sisu.formulas

import org.springframework.core.Ordered

class LitrePerHour implements Formula, Ordered {

    @Override
    Number calculate(Number value) {
        return 0.06 * value
    }

    @Override
    Number convertToDefault(Number value) {
        return value / 0.06
    }

    @Override
    String toString() {
        return "l/h"
    }

    @Override
    int getOrder() {
        return 1
    }
}
