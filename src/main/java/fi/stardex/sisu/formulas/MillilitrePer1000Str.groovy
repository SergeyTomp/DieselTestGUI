package fi.stardex.sisu.formulas

import org.springframework.core.Ordered

class MillilitrePer1000Str extends StrFormula implements Ordered {

    @Override
    int strCoefficient() {
        return 1000
    }

    @Override
    int getOrder() {
        return 4
    }
}