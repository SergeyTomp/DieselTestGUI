package fi.stardex.sisu.util.converters

import javafx.util.StringConverter

class StringNumberConverter<T extends Number> /*extends  StringConverter<T> */{

//    Class<? extends Number> clazz
//
//    StringNumberConverter() {
//        clazz = BigDecimal
//    }
//
//    StringNumberConverter(Class<T> clazz) {
//        this.clazz = clazz
//    }
//
//    @Override
//    String toString(T number) {
//        if (number != null && number >= 0) {
//            return Formula.round(number).toString()
//        }
//        return ''
//    }
//
//    @Override
//    T fromString(String s) {
//        if (s != null && s.isNumber()) {
//            return (T) clazz.getConstructor(String.class).newInstance(s)
//        }
//        return (T) clazz.getConstructor(String.class).newInstance('0')
//    }
}
