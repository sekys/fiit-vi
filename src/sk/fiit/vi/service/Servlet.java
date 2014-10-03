package sk.fiit.vi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Seky on 17. 7. 2014.
 * <p/>
 * Servlet pre TImeline.
 */
public class Servlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(Servlet.class.getName());
    private static final int CACHE_DURATION_IN_SECOND = 0; // 60 * 60 * 24 * 2; // 2 days
    private static final ObjectMapper MAPPER = new ObjectMapper();




    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nameA = req.getParameter("first");
        String nameB = req.getParameter("second");
    }

}