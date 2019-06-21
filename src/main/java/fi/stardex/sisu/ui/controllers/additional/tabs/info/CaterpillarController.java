package fi.stardex.sisu.ui.controllers.additional.tabs.info;

import fi.stardex.sisu.model.MainSectionModel;
import fi.stardex.sisu.persistence.repos.cr.BoschRepository;
import fi.stardex.sisu.persistence.repos.cr.CaterpillarRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;

public class CaterpillarController {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private TableView<InfoTableLine> infoTableView;
    @FXML
    private TableColumn<InfoTableLine, String> parameterColumn;
    @FXML
    private TableColumn<InfoTableLine, String> dataColumn;

    private CaterpillarRepository caterpillarRepository;

    private String NO_INFORMATION = "No information for specified injector.";
    private MainSectionModel mainSectionModel;

    public AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    public void setCaterpillarRepository(CaterpillarRepository caterpillarRepository) {
        this.caterpillarRepository = caterpillarRepository;
    }

    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }

    @PostConstruct
    private void init(){
        infoTableView.setPlaceholder(new Label(NO_INFORMATION));
    }

    void switchTo(){}

    private class InfoTableLine{
        StringProperty parameter;
        StringProperty data;

        public InfoTableLine(String parameter, String data) {
            this.parameter = new SimpleStringProperty(parameter);
            this.data = new SimpleStringProperty(data);
        }
    }
}
