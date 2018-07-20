package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.InjectorsRepository;
import fi.stardex.sisu.persistence.repos.cr.VoltAmpereProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PreDestroy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@PropertySource("classpath:properties/app.properties")
public class CSVSUpdater {

    private ManufacturerRepository manufacturerRepository;

    private VoltAmpereProfileRepository voltAmpereProfileRepository;

    private InjectorsRepository injectorsRepository;

    private InjectorTestRepository injectorTestRepository;

    @Value("${stardex.custom_csvs.manufacturers.header}")
    private String custom_manufacturers_header;

    @Value("${stardex.custom_csvs.voltAmpereProfiles.header}")
    private String custom_voap_header;

    @Value("${stardex.custom_csvs.injectors.header}")
    private String custom_injectors_header;

    @Value("${stardex.custom_csvs.injectorTests.header}")
    private String custom_injector_tests_header;

    @Value("${stardex.custom_csvs.directory}")
    private String customCSVSDirectoryName;

    @Value("${stardex.directory}")
    private String directoryName;

    @Value("${stardex.custom_csvs.manufacturers}")
    private String customManufacturers;

    @Value("${stardex.custom_csvs.voltAmpereProfiles}")
    private String customVOAP;

    @Value("${stardex.custom_csvs.injectors}")
    private String customInjectors;

    @Value("${stardex.custom_csvs.injectorTests}")
    private String customInjectorTests;

    public CSVSUpdater(ManufacturerRepository manufacturerRepository,
                       VoltAmpereProfileRepository voltAmpereProfileRepository,
                       InjectorsRepository injectorsRepository,
                       InjectorTestRepository injectorTestRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.voltAmpereProfileRepository = voltAmpereProfileRepository;
        this.injectorsRepository = injectorsRepository;
        this.injectorTestRepository = injectorTestRepository;
    }

    @PreDestroy
    private void destroy() {

        String pathToCSVSDirectory = System.getProperty("user.home") + File.separator + directoryName + File.separator + customCSVSDirectoryName;

        EntityUpdates.getMapOfEntityUpdates().forEach(((entityClassName, updated) -> {
            if (updated) {
                switch (entityClassName) {
                    case "Manufacturer":
                        updateCustomCSV(new File(pathToCSVSDirectory, customManufacturers), custom_manufacturers_header, manufacturerRepository);
                        break;
                    case "VoltAmpereProfile":
                        updateCustomCSV(new File(pathToCSVSDirectory, customVOAP), custom_voap_header, voltAmpereProfileRepository);
                        break;
                    case "Injector":
                        updateCustomCSV(new File(pathToCSVSDirectory, customInjectors), custom_injectors_header, injectorsRepository);
                        break;
                    case "InjectorTest":
                        updateCustomCSV(new File(pathToCSVSDirectory, customInjectorTests), custom_injector_tests_header, injectorTestRepository);
                        break;
                    default:
                        break;
                }
            }
        }));

    }

    private void updateCustomCSV(File file, String header, ManufacturerRepository repository) {

        List<Manufacturer> customManufacturers = repository.findByIsCustom(true);

        System.err.println(customManufacturers.isEmpty());

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//            writer.append()
//        }

    }

    private void updateCustomCSV(File file, String header, VoltAmpereProfileRepository repository) {

    }

    private void updateCustomCSV(File file, String header, InjectorsRepository repository) {

    }

    private void updateCustomCSV(File file, String header, InjectorTestRepository repository) {

    }

}
