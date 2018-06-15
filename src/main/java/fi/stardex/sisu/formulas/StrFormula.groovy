package fi.stardex.sisu.formulas

abstract class StrFormula implements Formula {

//    @Autowired
//    CurrentInjectorTestObtainer currentInjectorTestObtainer

    @Override
    Number calculate(Number value) {
//        if (currentInjectorTestObtainer.injectorTest.motorSpeed == 0) {
//            return 0
//        }
//        return value * strCoefficient() / currentInjectorTestObtainer.injectorTest.motorSpeed
        return null
    }

    @Override
    Number convertToDefault(Number value) {
//        return value * currentInjectorTestObtainer.injectorTest.motorSpeed / strCoefficient()
        return null
    }

    abstract int strCoefficient()


    @Override
    String toString() {
        return String.format("ml/" + strCoefficient() + "str")
    }
}
