package fi.stardex.sisu.ui.controllers.cr.tabs.info;

import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.persistence.orm.denso_info.DensoInjectors;
import fi.stardex.sisu.persistence.repos.cr.DensoRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static fi.stardex.sisu.util.CopyUtils.copyToClipBoard;

public class DensoController {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private TableView<InfoTableLine> infoTableView;
    @FXML
    private TableColumn<InfoTableLine, String> parameterColumn;
    @FXML
    private TableColumn<InfoTableLine, String> dataColumn;
    private Stage addInfoImageStage;
    private ImageView addInfoImageView;
    private MenuItem item;
    private ContextMenu menu;
    private String selectedText;
    private ObservableList<TablePosition> selectedCells;
    private KeyCodeCombination copyKeyCodeCombination;
    private String SERIAL_NUMBER = "Serial Number :";
    private String APPLICATION = "Application :";
    private String OEM = "OEM :";
    private String NO_INFORMATION = "No information for specified injector.";

    private DensoRepository densoRepository;
    private StringBuilder sb;
    private ObservableList<InfoTableLine> infoSource;
    private Set<String> code1;
    private Set<String> code2;
    private Set<String> description;
    private Set<String> densoNumber;
    private boolean notEmpty;
    private MainSectionModel mainSectionModel;
    private Logger logger = LoggerFactory.getLogger(DensoController.class);

    public AnchorPane getRootAnchorPane() {
        return rootAnchorPane;
    }

    public void setDensoRepository(DensoRepository densoRepository) {
        this.densoRepository = densoRepository;
    }
    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }

    @PostConstruct
    private void init(){
        infoSource = FXCollections.observableArrayList();
        sb = new StringBuilder();
        infoTableView.getSelectionModel().setCellSelectionEnabled(true);
        parameterColumn.setCellValueFactory(c -> c.getValue().parameter);
        dataColumn.setCellValueFactory(c -> c.getValue().data);
        infoTableView.setPlaceholder(new Label(NO_INFORMATION));
        selectedCells = infoTableView.getSelectionModel().getSelectedCells();
        item = new MenuItem("Copy");
        menu = new ContextMenu();
        menu.getItems().add(item);
        infoTableView.setContextMenu(menu);
        copyKeyCodeCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);

        item.setOnAction(event -> copyToClipBoard(infoTableView));
        infoTableView.setOnKeyPressed(event -> {
            if (copyKeyCodeCombination.match(event) && event.getSource() instanceof TableView) {
                copyToClipBoard(infoTableView);

            }
        });
    }

    void switchTo(){
        infoSource.clear();
        code1 = new HashSet<>();
        code2 = new HashSet<>();
        description = new HashSet<>();
        notEmpty = false;

        String injectorCode = mainSectionModel.injectorProperty().get().getInjectorCode();

        searchByCode1(injectorCode);
        if(notEmpty){
            fillTableView();
        }
        else {
            searchByCode2(injectorCode);
            if(notEmpty){
                for (String aDensoNumber : densoNumber) {
                    searchByCode1(aDensoNumber);
                }
                fillTableView();
            }
        }
    }

    private void searchByCode1(String code){
        List<DensoInjectors> byCode1 = densoRepository.search(code
                .replace("#", "")
                .replace("-", "")
                .replace("HU", "")
                .trim());

        if (byCode1 != null && !byCode1.isEmpty()) {
            code1 = new HashSet<>();
            code2 = new HashSet<>();
            description = new HashSet<>();

            byCode1.forEach(c->{
                code1.add(c.getCode1());
                code2.add(c.getCode2());
                description.add(c.getDescription());
            });
            notEmpty = true;
        }
    }

    private void searchByCode2(String code){
        List<DensoInjectors> byCode2 = densoRepository.searchCode2(code
                .replace("#", "")
                .replace("-", "")
                .replace("HU", "")
                .replace("SM", "")
                .trim());
        if (byCode2 != null && !byCode2.isEmpty()) {
            densoNumber = new HashSet<>();
            byCode2.forEach(c->{
                if (c.getCode1() != null && !c.getCode1().equals("")) {
                    densoNumber.add(c.getCode1()
                            .replace("#", "")
                            .replace("-", "")
                            .replace("HU", "")
                            .replace("SM", "")
                            .trim());
                }
            });
            notEmpty = true;
        }
    }

    private void fillTableView(){
        Iterator<String> c1It = code1.iterator();
        Iterator<String> c2It = code2.iterator();
        Iterator<String> dscrIt = description.iterator();

        if(c1It.hasNext()){
            sb.append(c1It.next());
            while (c1It.hasNext()){
                sb.append("\n").append(c1It.next());
            }
        }
        infoSource.add(new InfoTableLine(SERIAL_NUMBER, sb.toString()));
        sb.setLength(0);

        if(c2It.hasNext()){
            sb.append(c2It.next());
            while (c2It.hasNext()){
                sb.append("\n").append(c2It.next());
            }
        }
        infoSource.add(new InfoTableLine(OEM, sb.toString()));
        sb.setLength(0);

        if(dscrIt.hasNext()){
            sb.append(dscrIt.next());
            while (dscrIt.hasNext()){
                sb.append("\n").append(dscrIt.next());
            }
        }
        infoSource.add(new InfoTableLine(APPLICATION, sb.toString()));
        sb.setLength(0);
        infoTableView.setItems(infoSource);
    }

    private class InfoTableLine{
        StringProperty parameter;
        StringProperty data;

        InfoTableLine(String parameter, String data) {
            this.parameter = new SimpleStringProperty(parameter);
            this.data = new SimpleStringProperty(data);
        }
    }
}
