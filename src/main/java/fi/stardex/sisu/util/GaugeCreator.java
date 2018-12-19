package fi.stardex.sisu.util;

import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import javafx.scene.paint.Color;

public class GaugeCreator {

    public static Lcd createLcd(String label) {
        Lcd pressureLcd = LcdBuilder.create()
                .prefWidth(130)
                .prefHeight(60)
                .styleClass(Lcd.STYLE_CLASS_BLACK_YELLOW)
                .backgroundVisible(true)
                .foregroundShadowVisible(true)
                .crystalOverlayVisible(true)
                .title("")
                .titleVisible(false)
                .batteryVisible(false)
                .signalVisible(false)
                .alarmVisible(false)
                .unit(" " + label)
                .unitVisible(true)
                .decimals(0)
                .minMeasuredValueDecimals(4)
                .minMeasuredValueVisible(false)
                .maxMeasuredValueDecimals(4)
                .maxMeasuredValueVisible(false)
                .formerValueVisible(false)
                .threshold(0)
                .thresholdVisible(false)
                .trendVisible(false)
                .trend(Lcd.Trend.RISING)
                .numberSystemVisible(false)
                .lowerRightTextVisible(true)
                .valueFont(Lcd.LcdFont.DIGITAL_BOLD)
                .animated(false)
                .build();
        pressureLcd.setMaxValue(5000.0);
        pressureLcd.setValue(0);
        return pressureLcd;
    }

    public static Gauge createPumpGauge() {

        return GaugeBuilder
                .create()
                .skinType(Gauge.SkinType.SIMPLE)
                .title("Amp")
                .titleColor(Color.YELLOW)
                .decimals(1)
                .tickLabelDecimals(1)
                .maxValue(2)
                .valueVisible(true)
                .sectionsVisible(true)
                .sections(new Section(0, 1.5, Color.GREEN), new Section(1.5, 2, Color.YELLOW))
                .animated(true)
                .animationDuration(500)
                .build();
    }

    public static Gauge createRLCGauge(){
        return GaugeBuilder.create()
                .skinType(Gauge.SkinType.SIMPLE_SECTION)
                .titleColor(Color.YELLOW)
                .title(" ")
                .minValue(0)
                .maxValue(35)
                .valueColor(Color.WHITE)
                .barColor(Color.YELLOW)
                .unitColor(Color.YELLOW)
                .animated(true)
                .decimals(2)
                .maxMeasuredValueVisible(false)
                .minMeasuredValueVisible(false)
                .oldValueVisible(false)
                .animated(false)
                .build();
    }
}
