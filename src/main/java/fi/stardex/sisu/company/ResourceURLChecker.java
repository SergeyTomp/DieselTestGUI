package fi.stardex.sisu.company;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class ResourceURLChecker {

    private static final Logger logger = LoggerFactory.getLogger(ResourceURLChecker.class);

    public boolean fileExists(String url) {
        if (!StringUtils.isEmpty(url)) {
            try {
                return new File(new URL(url).toURI()).exists();
            } catch (URISyntaxException | MalformedURLException e) {
                logger.error("Cannot check file existence. ", e);
                return false;
            }
        }
        logger.error("Empty url argument");
        return false;
    }
}
