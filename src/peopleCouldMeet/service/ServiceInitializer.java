package peopleCouldMeet.service;

import peopleCouldMeet.util.Lucene;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Seky on 3. 10. 2014.
 * Trieda implementujuca callbacky pri spusteni serveru.
 */
public class ServiceInitializer implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Pri spusteni serveru sa nacita konfiguracia a otvori sa Lucene index
        Lucene.getInstance();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Lucene.getInstance().close();
    }
}

