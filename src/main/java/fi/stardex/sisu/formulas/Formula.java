package fi.stardex.sisu.formulas;

import java.text.DecimalFormat;

public interface Formula {

    /**
     * Converts invariant value by formula
     * @param value to convert
     * @return converted value
     */
    Number calculate(Number value);

    /**
     * Converts value to invariant
     * @param value to convert
     * @return invariant value
     */
    Number convertToDefault(Number value);


    static Number round(Number number) {
        DecimalFormat oneDecimal = new DecimalFormat("####.#");
        String temp=oneDecimal.format(number);
        temp=temp.replace(',','.');
        return Double.valueOf(temp);
    }
}
