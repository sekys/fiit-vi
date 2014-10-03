package sk.fiit.vi.util;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;


@XmlRootElement
public final class Configuration implements Serializable {

    public static final String CONFIG_DIR;
    private static final long serialVersionUID = -1848584185011896784L;
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    private static final String FILENAME = "configuration.xml";
    private static final String LOGGING_PROPERTIES_FILE = "log4j.properties";
    private static JAXBContext context;

    static {
        // Load conf dir
        //noinspection AccessOfSystemProperties
        String defaultDir = System.getProperty("user.dir") + File.separator + "conf";
        CONFIG_DIR = System.getProperty("config.dir", defaultDir);

        // Prepare log4j
        String log4jLoggingPropFile = new File(CONFIG_DIR, LOGGING_PROPERTIES_FILE).getAbsolutePath();
        PropertyConfigurator.configure(log4jLoggingPropFile);

        try {
            context = JAXBContext.newInstance(Configuration.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.ERROR, null, ex);
        }
    }

    private Configuration() {
    }

    public static Configuration getInstance() {
        return ConfigurationHolder.INSTANCE;
    }

    private synchronized static Configuration read() {
        try {
            File file = new File(CONFIG_DIR, FILENAME);
            LOGGER.info("Configuration file: " + file.getAbsolutePath());
            return (Configuration) context.createUnmarshaller().unmarshal(file);
        } catch (Exception e) {
            LOGGER.error("Configuration not loaded", e);
            throw new RuntimeException("Configuration not loaded", e);
        }
    }

    private static Marshaller getMarshaller() throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        return marshaller;
    }

    @Override
    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            getMarshaller().marshal(this, writer);
            return writer.getBuffer().toString();
        } catch (Exception e) {
            LOGGER.error("Configuration can not be marshalled", e);
        }
        return ToStringBuilder.reflectionToString(this);
    }

    private static class ConfigurationHolder {
        private static final Configuration INSTANCE = read();
    }

}
