package fi.stardex.sisu.persistence;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import org.h2.tools.RunScript;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sqlite.jdbc3.JDBC3Statement;
import org.sqlite.jdbc4.JDBC4Statement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Statement;

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
            RunScript.execute(dataSource.getConnection(), new FileReader(getClass().getResource("/db/scripts/fill_tables.sql").getPath()));
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
//        String filePath = getClass().getResource("/db/csv/manufacturers.csv").getPath();
//        sessionFactory.openSession().createQuery("INSERT INTO MANUFACTURER(ID, MANUFACTURER, DISPLAY_ORDER) SELECT manufacturer FROM CSVREADER('"+
//               filePath +"')");


    }


}
