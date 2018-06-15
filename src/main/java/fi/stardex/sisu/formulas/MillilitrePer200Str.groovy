package fi.stardex.sisu.formulas

import org.springframework.core.Ordered

class MillilitrePer200Str extends StrFormula implements Ordered {

    @Override
    int strCoefficient() {
        return 200
    }

    @Override
    int getOrder() {
        return 3
    }
}
