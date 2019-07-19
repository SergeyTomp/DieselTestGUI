package fi.stardex.sisu.util;

import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import eu.hansolo.medusa.*;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

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

    public static Gauge createPumpGauge(double maxValue) {

        double greenLimit = maxValue * 0.6;
        double yellowLimit = maxValue * 0.8;

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
                .sections(new Section(0, greenLimit, Color.GREEN), new Section(greenLimit, yellowLimit, Color.YELLOW), new Section(yellowLimit, maxValue, Color.RED))
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

    public static Gauge createPiezoGauge() {

        double MIN = 0;
        double MAX = 4000;

        return GaugeBuilder.create()
        .skinType(Gauge.SkinType.GAUGE)

        // Related to Foreground Elements
        .foregroundBaseColor(Color.BLACK)  // Defines a color foreground elements

        // Related to Title Text
        .title("Title") // Set the text for the title
        .titleColor(Color.YELLOW) // Define the color for the title text

        // Related to Sub Title Text
        .subTitle("SubTitle") // Set the text for the subtitle
        .subTitleColor(Color.YELLOW) // Define the color for the subtitle text

        // Related to Unit Text
        .unit("Unit") // Set the text for the unit
        .unitColor(Color.YELLOW) // Define the color for the unit

        // Related to Value Text
        .valueColor(Color.YELLOW) // Define the color for the value text
        .decimals(0) // Set the number of decimals for the value/lcd text

        // Related to LCD
                .lcdVisible(true) // Display a LCD instead of the plain value text
                .lcdDesign(LcdDesign.STANDARD) // Set the design for the LCD
                .lcdFont(LcdFont.DIGITAL_BOLD) // Set the font for the LCD

        // Related to scale
        .scaleDirection(Gauge.ScaleDirection.CLOCKWISE) // CLOCKWISE, COUNTER_CLOCKWISE
        .minValue(MIN) // Set the start value of the scale
        .maxValue(MAX) // Set the end value of the scale
        .startAngle(320) // Start angle of your scale (bottom -> 0, direction -> CCW)
        .angleRange(280) // Angle range of your scale starting from the start angle

        // Related to Tick Labels
        .tickLabelDecimals(0) // Number of decimals for the tick labels
        .tickLabelLocation(TickLabelLocation.INSIDE) // Tick labels in- or outside the scale
        .tickLabelOrientation(TickLabelOrientation.HORIZONTAL) // ORTHOGONAL, TANGENT
        .onlyFirstAndLastTickLabelVisible(false) // Show only first and last tick label
        .tickLabelColor(Color.YELLOW) // Color for tick labels
        .tickLabelSectionsVisible(false) // Sections for tick labels should be visible
        .tickLabelSections(new Section(0.4 * MAX, 0.7 * MAX, Color.RED))// Sections to color tick labels

        // Related to Tick Marks - деления шкалы
        .tickMarkSectionsVisible(false) // Sections for tick marks should be visible
        .tickMarkSections(new Section(0.2 * MAX, 0.8 * MAX, Color.PURPLE)) // Sections to color tick marks

        // Related to Major Tick Marks
        .majorTickMarksVisible(true) // Major tick marks should be visible
        .majorTickMarkType(TickMarkType.PILL) // LINE, DOT, TRIANGLE, TICK_LABEL
        .majorTickMarkColor(Color.YELLOW) // Color for the major tick marks

        // Related to Medium Tick Marks
        .mediumTickMarksVisible(true) // Medium tick marks should be visible
        .mediumTickMarkType(TickMarkType.LINE) // LINE, DOT, TRIANGLE
        .mediumTickMarkColor(Color.YELLOW) // Color for the medium tick marks

        // Related to Minor Tick Marks
        .minorTickMarksVisible(true) // Minor tick marks should be visible
        .minorTickMarkType(TickMarkType.DOT) // LINE, DOT, TRIANGLE
        .minorTickMarkColor(Color.YELLOW) // Color for minor tick marks

        // Related to LED
        .ledVisible(false) // LED should be visible
        .ledType(Gauge.LedType.STANDARD) // STANDARD, FLAT
        .ledColor(Color.rgb(255, 200, 0)) // Color of the LED
        .ledBlinking(false) // LED should blink

        // Related to Needle
        .needleShape(Gauge.NeedleShape.ANGLED) // ANGLED, ROUND, FLAT
        .needleSize(Gauge.NeedleSize.STANDARD) // THIN, STANDARD, THICK
        .needleColor(Color.CRIMSON) // Color of the needle

        // Related to Needle behavior
        .startFromZero(false) // Needle should start from the 0 value
        .returnToZero(false) // Needle should always return to the 0 value

        // Related to Knob
        .knobType(Gauge.KnobType.STANDARD) // STANDARD, METAL, PLAIN, FLAT
        .knobColor(Color.LIGHTGRAY) // Color that should be used for the center knob
        .interactive(false) // Should be possible to press the center knob
//        .onButtonPressed(buttonEvent -> System.out.println("Knob pressed"))
//        .onButtonReleased(buttonEvent -> System.out.println("Knob released"))

        // Related to Threshold
        .thresholdVisible(true) // Threshold indicator should be visible
        .threshold(0.5 * MAX) // Value for the threshold
        .thresholdColor(Color.RED) // Color for the threshold
        .checkThreshold(false) // Check each value against threshold
//        .onThresholdExceeded(thresholdEvent -> System.out.println("Threshold exceeded"))
//        .onThresholdUnderrun(thresholdEvent -> System.out.println("Threshold underrun"))

        // Related to Gradient Bar
        .gradientBarEnabled(false)
        .gradientBarStops(new Stop(0.0, Color.BLUE), // Gradient for gradient bar
                                new Stop(0.25, Color.CYAN),
                                new Stop(0.5, Color.LIME),
                                new Stop(0.75, Color.YELLOW),
                                new Stop(1.0, Color.RED))

        // Related to Sections
        .sectionsVisible(false)  // Sections will be visible
        .sections(new Section(0.5 * MAX, 0.75 * MAX, Color.ORANGE)) // Sections that will be drawn
        .checkSectionsForValue(false) // Check current value against each section

        // Related to Areas
        .areasVisible(false) // Areas will be visible
        .areas(new Section(0.75 * MAX, MAX, Color.RED))  // Areas that will be drawn

        // Related to Markers
        .markersVisible(false) // Markers will be visible
        .markers(new Marker(0.75 * MAX, "Marker 1", Color.HOTPINK)) // Markers that will be drawn

                        // Related to Value
        .animated(false) // Needle will be animated
        .animationDuration(500)  // Speed of the needle in milliseconds (10 - 10000 ms)
//        .onValueChanged(o -> System.out.println(((DoubleProperty) o).get()))
        .build();

    }
}
