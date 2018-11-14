package fi.stardex.sisu.ui.controllers.additional.tabs.info;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class InfoController {

    @FXML
    private GridPane rootInfoGridPane;
    private Parent infoDefault;
    private Parent infoBosch;
    private Parent infoSiemens;
    private Parent infoDenso;
    private Parent infoCaterpillar;
    private Parent infoDelphi;
    private Parent infoAZPI;
    private BoschController boschController;
    private SiemensController siemensController;
    private DelphiController delphiController;
    private DensoController densoController;
    private CaterpillarController caterpillarController;
    private AZPIController azpiController;

    private Logger logger = LoggerFactory.getLogger(InfoController.class);

    @PostConstruct
    private void init(){
        changeToDefault();
    }

    public void setInfoDefault(Parent infoDefault) {
        this.infoDefault = infoDefault;
    }

    public void setInfoBosch(Parent infoBosch) {
        this.infoBosch = infoBosch;
    }

    public void setInfoSiemens(Parent infoSiemens) {
        this.infoSiemens = infoSiemens;
    }

    public void setInfoDenso(Parent infoDenso) {
        this.infoDenso = infoDenso;
    }

    public void setInfoCaterpillar(Parent infoCaterpillar) {
        this.infoCaterpillar = infoCaterpillar;
    }

    public void setInfoDelphi(Parent infoDelphi) {
        this.infoDelphi = infoDelphi;
    }

    public void setInfoAZPI(Parent infoAZPI) {
        this.infoAZPI = infoAZPI;
    }

    public void setBoschController(BoschController boschController) {
        this.boschController = boschController;
    }

    public void setSiemensController(SiemensController siemensController) {
        this.siemensController = siemensController;
    }

    public void setDelphiController(DelphiController delphiController) {
        this.delphiController = delphiController;
    }

    public void setDensoController(DensoController densoController) {
        this.densoController = densoController;
    }

    public void setCaterpillarController(CaterpillarController caterpillarController) {
        this.caterpillarController = caterpillarController;
    }

    public void setAzpiController(AZPIController azpiController) {
        this.azpiController = azpiController;
    }

    public void changeToDefault(){
        clearSectionLayout();
        rootInfoGridPane.add(infoDefault, 0, 0);
        logger.info("Change to Default Label");
    }

    public void changeToBosch(){
        clearSectionLayout();
        rootInfoGridPane.add(infoBosch, 0, 0);
        boschController.switchTo();
    }

    public void changeToSiemens(){
        clearSectionLayout();
        rootInfoGridPane.add(infoSiemens, 0, 0);
        siemensController.switchTo();
    }

    public void changgeToDenso(){
        clearSectionLayout();
        rootInfoGridPane.add(infoDenso, 0, 0);
        densoController.switchTo();
        logger.info("Change to Denso Panel");
    }

    public void changeToCaterpillar(){
        clearSectionLayout();
        rootInfoGridPane.add(infoCaterpillar, 0, 0);
        logger.info("Change to Caterpillar Label");
    }

    public void changeToDelphi(){
        clearSectionLayout();
        rootInfoGridPane.add(infoDelphi, 0, 0);
        logger.info("Change to Delphi Label");
    }

    public void changeToAZPI(){
        clearSectionLayout();
        rootInfoGridPane.add(infoAZPI, 0, 0);
        logger.info("Change to AZPI Label");
    }

    private void clearSectionLayout() {
        rootInfoGridPane.getChildren().clear();
    }
}
