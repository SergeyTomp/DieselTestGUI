package fi.stardex.sisu.ui.controllers.cr.tabs.info;

import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.persistence.repos.cr.DelphiRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import javax.annotation.PostConstruct;

public class DelphiController {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private TableView<InfoTableLine> infoTableView;
    @FXML
    private TableColumn<InfoTableLine, String> parameterColumn;
    @FXML
    private TableColumn<InfoTableLine, String> dataColumn;
    private String NO_INFORMATION = "No information for specified injector.";
    private DelphiRepository delphiRepository;
    private MainSectionModel mainSectionModel;

    public AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    public void setDelphiRepository(DelphiRepository delphiRepository) {
        this.delphiRepository = delphiRepository;
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
