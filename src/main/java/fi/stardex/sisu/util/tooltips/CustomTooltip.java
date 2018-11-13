package fi.stardex.sisu.util.tooltips;

import javafx.scene.control.Tooltip;

public class CustomTooltip extends Tooltip {

    public CustomTooltip() {
        this("Press ENTER to commit changes");
    }

    public CustomTooltip(String message) {
        super(message);
        hackTooltipStartTiming();
    }

    private void hackTooltipStartTiming() {
        try {
//            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
//            fieldBehavior.setAccessible(true);
//            Object objBehavior = fieldBehavior.get(this);
//
//            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
//            fieldTimer.setAccessible(true);
//            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
//
//            objTimer.getKeyFrames().clear();
//            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
