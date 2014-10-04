package sk.fiit.vi.service;

import sk.fiit.vi.util.Configuration;
import sk.fiit.vi.util.Lucene;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Seky on 3. 10. 2014.
 */
public class ServiceInitializer implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Configuration.getInstance();
        Lucene.getInstance();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Lucene.getInstance().close();
    }
}
