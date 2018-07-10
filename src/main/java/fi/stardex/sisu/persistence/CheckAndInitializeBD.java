package fi.stardex.sisu.persistence;

import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;

public class CheckAndInitializeBD {

    private Logger logger = LoggerFactory.getLogger(CheckAndInitializeBD.class);
    private ManufacturerRepository manufacturerRepository;
    private DataSource dataSource;

    public CheckAndInitializeBD(ManufacturerRepository manufacturerRepository, DataSource dataSource) {
        this.manufacturerRepository = manufacturerRepository;
        this.dataSource = dataSource;
    }


    @PostConstruct
    private void checkTables() {
        System.err.println(manufacturerRepository.count());

        if (manufacturerRepository.count() == 0) {
            fillTables();
        } else {
            System.err.println("Tables filled out");
        }

    }

    private void fillTables() {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/db/scripts/fill_tables.sql")));
            RunScript.execute(dataSource.getConnection(), reader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        String filePath = getClass().getResource("/db/csv/manufacturers.csv").getPath();
//        sessionFactory.openSession().createQuery("INSERT INTO MANUFACTURER(ID, MANUFACTURER, DISPLAY_ORDER) SELECT manufacturer FROM CSVREADER('"+
//               filePath +"')");


    }

}
