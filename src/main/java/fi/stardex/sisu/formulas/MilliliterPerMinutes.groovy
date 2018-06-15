package fi.stardex.sisu.formulas

import org.springframework.core.Ordered

class MilliliterPerMinutes implements Formula, Ordered {

    @Override
    Number calculate(Number value) {
        return value
    }

    @Override
    Number convertToDefault(Number value) {
        return value
    }

    @Override
    String toString() {
        return "ml/min"
    }

    @Override
    int getOrder() {
        return 0
    }
}
