package fi.stardex.sisu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationConfigHandler {

    private Logger logger = LoggerFactory.getLogger(ApplicationConfigHandler.class);

    private Map<String, String> config = new LinkedHashMap<>();
    private String filePath;

    {
        config.put("GUI_Type", "CR");
        config.put("Language", "ENGLISH");
        config.put("UltimaIP", "192.168.10.206");
        config.put("UltimaPort", "502");
        config.put("FlowIP", "192.168.10.201");
        config.put("FlowPort", "502");
        config.put("StandIP", "192.168.10.202");
        config.put("StandPort", "502");
    }

    public Map<String, String> load() {
        return config;
    }

    public void put(String key, String value) {
        config.put(key, value);
        save();
    }

    public String get(String key) {
        return config.get(key);
    }

    private void save() {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write(getText());
            writer.flush();
        } catch (IOException e) {
            logger.error("Error while saving config. ", e);
        }
    }

    private String getText() {
        StringBuilder sb = new StringBuilder();
        config.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        return sb.toString();
    }

    @PostConstruct
    private void readConfig() {
        filePath = System.getProperty("user.home") + "/.config/";
        File file = new File(filePath);
        logger.info("Directory created {}", file.mkdirs());
        file = new File(file.getPath() + "/CRConfig.cfg");
        filePath = file.getPath();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] keyValue = line.split(": ", 2);
                    config.put(keyValue[0].trim(), keyValue[1].trim());
                }
            } catch (IOException e) {
                logger.error("Error while reading config file.", e);
            }
        } else {
            try (FileWriter writer = new FileWriter(filePath, false)) {
                writer.write(getText());
                writer.flush();
            } catch (IOException e) {
                logger.error("Exception while writing config file.", e);
            }
        }
    }
}
