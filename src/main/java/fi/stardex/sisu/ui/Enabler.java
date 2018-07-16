package fi.stardex.sisu.ui;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Tests;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;

public class Enabler {

    private MainSectionController mainSectionController;

    private RadioButton manualTestRadioButton;

    private RadioButton testPlanTestRadioButton;

    private RadioButton autoTestRadioButton;

    private RadioButton codingTestRadioButton;

    private ListView<InjectorTest> testListView;

    public Enabler(MainSectionController mainSectionController) {
        this.mainSectionController = mainSectionController;
        manualTestRadioButton = mainSectionController.getManualTestRadioButton();
        testPlanTestRadioButton = mainSectionController.getTestPlanTestRadioButton();
        autoTestRadioButton = mainSectionController.getAutoTestRadioButton();
        codingTestRadioButton = mainSectionController.getCodingTestRadioButton();
        testListView = mainSectionController.getTestListView();
    }

    public Enabler selectInjector(boolean selected) {

        if (selected) {
            testPlanTestRadioButton.setDisable(false);
            autoTestRadioButton.setDisable(false);
            codingTestRadioButton.setDisable(false);
            if (!manualTestRadioButton.isSelected())
                mainSectionController.fillTestListView();
        } else {
            testPlanTestRadioButton.setDisable(true);
            autoTestRadioButton.setDisable(true);
            codingTestRadioButton.setDisable(true);
            manualTestRadioButton.setSelected(true);
            testListView.getItems().clear();
        }

        return this;
    }

    public Enabler selectTest(Tests test) {
        switch (test) {
            case MANUAL:
                testListView.setDisable(false);
                testListView.getItems().clear();
                mainSectionController.getCurrentInjectorTestsObtainer().setInjectorTests(null);
                break;
            case TESTPLAN:
                testListView.setDisable(false);
                mainSectionController.refreshTestListView();
                break;
            case AUTO:
                testListView.setDisable(true);
                mainSectionController.refreshTestListView();
                break;
            case CODING:
                testListView.setDisable(true);
                mainSectionController.refreshTestListView();
                break;
            default:
                break;
        }
        return this;
    }

}
