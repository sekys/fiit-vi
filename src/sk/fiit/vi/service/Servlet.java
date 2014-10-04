package sk.fiit.vi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import sk.fiit.vi.parser.Person;
import sk.fiit.vi.util.Lucene;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Seky on 17. 7. 2014.
 * <p/>
 * Servlet pre TImeline.
 */
public class Servlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(Servlet.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nameA = req.getParameter("first");
        String nameB = req.getParameter("second");

        resp.setHeader("Content-Type", "text/html");
        PrintWriter writer = resp.getWriter();
        writer.println("<html><body><pre>Vysledok</br>");

        try {
            List<Person> peopleA = Lucene.getInstance().find(nameA);
            List<Person> peopleB = Lucene.getInstance().find(nameB);

            for (Person a : peopleA) {
                for (Person b : peopleB) {
                    Boolean intersect = a.checkIntersection(b);
                    writer.printf("%s - %s = %s</br>", a.getNames().get(0), b.getNames().get(0));
                }
            }

            writer.println("</pre></body></html>");
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
    }

}