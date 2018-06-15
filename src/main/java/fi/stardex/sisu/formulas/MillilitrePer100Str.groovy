package fi.stardex.sisu.formulas

import org.springframework.core.Ordered

class MillilitrePer100Str extends StrFormula implements Ordered {

    @Override
    int strCoefficient() {
        return 100
    }

    @Override
    int getOrder() {
        return 2
    }
}
