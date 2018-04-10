package fi.stardex.sisu.util.view;

import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationAppearanceChanger {

    private Logger logger = LoggerFactory.getLogger(ApplicationAppearanceChanger.class);

    private Parent crSection;
    private Parent uisSection;
    private Parent additionalSection;
    private GridPane sectionLayout;

    public ApplicationAppearanceChanger(Parent crSection, Parent uisSection, Parent additionalSection, GridPane sectionLayout) {
        this.crSection = crSection;
        this.uisSection = uisSection;
        this.additionalSection = additionalSection;
        this.sectionLayout = sectionLayout;
    }



    public void changeToCRInj() {
        sectionLayout.add(crSection, 0,0);
        sectionLayout.add(additionalSection,0,1);
        logger.info("Change to CR_Inj");
    }

    public void changeToCRPump() {
        sectionLayout.add(crSection, 0,0);
        sectionLayout.add(additionalSection,0,1);
        logger.info("Change to CR_Pump");
    }

    public void changeToUIS() {
        sectionLayout.add(uisSection,0,0);
        sectionLayout.add(additionalSection,0,1);
        logger.info("Change to UIS");
    }
}
