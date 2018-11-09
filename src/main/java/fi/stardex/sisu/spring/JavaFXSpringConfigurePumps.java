package fi.stardex.sisu.spring;

import fi.stardex.sisu.model.ManufacturerPumpModel;
import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.ui.controllers.pumps.*;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.embed.swing.JFXPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "fi.stardex.sisu")
public class JavaFXSpringConfigurePumps extends ViewLoader{

    public JavaFXSpringConfigurePumps(I18N i18N) {
        super(i18N);
        // Для работы JUnit, иначе бросается исключение toolkit not initialized
        new JFXPanel();
    }

    @Bean
    public ViewHolder mainSectionPumps(){
        return loadView("/fxml/pumps/MainSectionPumps.fxml");
    }

    @Bean
    public ViewHolder pumpsSection(){
        return loadView("/fxml/pumps/PumpSection.fxml");
    }

    @Bean public
    ViewHolder pumpTabSection(){
        return loadView("/fxml/pumps/TabSectionPumps.fxml");
    }

    @Bean
    public MainSectionPumpsController mainSectionPumpsController(){
        return (MainSectionPumpsController)mainSectionPumps().getController();
    }

    @Bean
    @Autowired
    public PumpsModelsListController pumpsModelsListController(MainSectionPumpsController mainSectionPumpsController,
                                                               PumpModel pumpModel){
        PumpsModelsListController pumpsModelsListController = mainSectionPumpsController.getPumpsModelsListController();
        pumpsModelsListController.setPumpModel(pumpModel);
        return pumpsModelsListController;
    }

    @Bean
    @Autowired
    public PumpsOEMListController pumpsOEMListController(MainSectionPumpsController mainSectionPumpsController,
                                                         ManufacturerPumpModel manufacturerPumpModel,
                                                         PumpModel pumpModel){
        PumpsOEMListController pumpsOEMListController = mainSectionPumpsController.getPumpsOEMListController();
        pumpsOEMListController.setManufacturerPumpModel(manufacturerPumpModel);
        pumpsOEMListController.setPumpModel(pumpModel);
        return pumpsOEMListController;
    }

    @Bean
    @Autowired
    public StoreResetPrintController storeResetPrintController(MainSectionPumpsController mainSectionPumpsController){
        return mainSectionPumpsController.getStoreResetPrintController();
    }

    @Bean
    @Autowired
    public TestModeController testModeController(MainSectionPumpsController mainSectionPumpsController){
        return mainSectionPumpsController.getTestModeController();
    }

    @Bean
    @Autowired
    public PumpFieldController pumpFieldController(MainSectionPumpsController mainSectionPumpsController){
        return mainSectionPumpsController.getPumpFieldController();
    }

    @Bean
    @Autowired
    public TestListController testListController(MainSectionPumpsController mainSectionPumpsController){
        return mainSectionPumpsController.getTestListController();
    }

    @Bean
    @Autowired
    public TestSpeedController testSpeedController(MainSectionPumpsController mainSectionPumpsController){
        return mainSectionPumpsController.getTestSpeedController();
    }

    @Bean
    @Autowired
    public StartButtonController startButtonController(MainSectionPumpsController mainSectionPumpsController){
        return mainSectionPumpsController.getStartButtonController();
    }

    @Bean
    public PumpSectionController pumpSectionController(){
        return (PumpSectionController)pumpsSection().getController();
    }

    @Bean
    @Autowired
    public PumpTabSectionController pumpsTabSectionController(I18N i18N){
        PumpTabSectionController pumpTabSectionController = (PumpTabSectionController) pumpTabSection().getController();
        pumpTabSectionController.setI18N(i18N);
        return pumpTabSectionController;
    }

    @Bean
    @Autowired
    public PumpFlowController pumpFlowController(PumpTabSectionController pumpTabSectionController){
        return pumpTabSectionController.getPumpFlowController();
    }

    @Bean
    @Autowired
    public PumpReportController pumpReportController(PumpTabSectionController pumpTabSectionController){
        return pumpTabSectionController.getPumpReportController();
    }

    @Bean
    @Autowired
    public  PumpInfoController pumpInfoController(PumpTabSectionController pumpTabSectionController){
        return pumpTabSectionController.getPumpInfoController();
    }
}
