package fi.stardex.sisu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DesktopFiles {

    private static final Logger logger = LoggerFactory.getLogger(DesktopFiles.class);

    private String desktopFolderPath;

    public DesktopFiles(){
        String homePath = System.getProperty("user.home");
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(homePath + "/.config/user-dirs.dirs")) {   // TODO: for windows
            properties.load(input);
            desktopFolderPath = new String(properties.getProperty("XDG_DESKTOP_DIR").getBytes("ISO-8859-1"), "UTF-8");
            desktopFolderPath = desktopFolderPath.replace("\"", "");
            desktopFolderPath = desktopFolderPath.replace("$HOME", homePath);
        } catch (IOException e) {
            logger.warn("Cannot find desktop folder. Use default from en locale, 'Desktop'.", e);
            desktopFolderPath = homePath + File.separator + "Desktop";
        }
    }


    public String getLogFolderPath() {
        return desktopFolderPath + File.separator + "logs";
    }


    public String getReportsFolderPath() {
        return desktopFolderPath + File.separator + "Reports";
    }


    public String getUserTestPlansFolderPath() {
        return desktopFolderPath + File.separator + "UserTestPlans";
    }
}
