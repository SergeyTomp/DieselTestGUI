package fi.stardex.sisu.spring;

import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
import javafx.fxml.FXMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ResourceBundle;

public abstract class ViewLoader {

    private final Logger logger = LoggerFactory.getLogger(ViewLoader.class);

    private final UTF8Control utf8Control = new UTF8Control();

    protected final I18N i18N;

    protected ViewLoader(I18N i18N) {
        this.i18N = i18N;
    }

    protected ViewHolder loadView(String url) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url),
                ResourceBundle.getBundle("properties.labels", i18N.getLocale(), utf8Control));
        ViewHolder viewHolder = new ViewHolder();
        try {
            viewHolder.setView(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Exception while load view {}", url, e);
        }
        viewHolder.setController(fxmlLoader.getController());
        return viewHolder;
    }

}
