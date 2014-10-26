package peopleCouldMeet.service;

import org.apache.log4j.Logger;
import peopleCouldMeet.parser.Person;
import peopleCouldMeet.util.Lucene;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Set;

/**
 * Created by Seky on 17. 7. 2014.
 * <p/>
 * Servlet pre vyhladavanie osoby.
 */
public class Servlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(Servlet.class.getName());

    /**
     * Pomocna metoda pre vypis zoznamu ludi
     *
     * @param writer Zapisovac.
     * @param title  Nazov odseku.
     * @param people Zoznamn ludi.
     */
    private void printPeople(PrintWriter writer, String title, Set<Person> people) {
        writer.println("</br>");
        writer.println(title + "</br>");
        for (Person p : people) {
            writer.println(p.toString() + "</br>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Ziskanie parametrov
        String nameA = req.getParameter("nameA");
        String nameB = req.getParameter("nameB");

        // Overenie parametrov
        if (nameA == null || nameB == null) {
            resp.sendError(500, "Parameter nameA / nameB missing.");
            return;
        }
        nameA = URLDecoder.decode(nameA, "UTF-8");
        nameB = URLDecoder.decode(nameB, "UTF-8");

        // Nastavenie vystupu
        resp.setHeader("Content-Type", "text/html");
        PrintWriter writer = resp.getWriter();
        writer.println("<html><body><pre>");

        try {
            // Za pomoci Lucene si vyhladame mnozinu ludi, ktory vyhovuju danemu menu
            Set<Person> peopleA = Lucene.getInstance().find(nameA);
            Set<Person> peopleB = Lucene.getInstance().find(nameB);

            // Vypiseme zoznam ludi
            printPeople(writer, "People A", peopleA);
            printPeople(writer, "People B", peopleB);
            writer.println("</br>");

            // Vypiseme overenie ci dana dvojica ludi sa mohla stretnut
            writer.println("Porovnanie</br>");
            for (Person a : peopleA) {
                for (Person b : peopleB) {
                    Boolean intersect = a.checkIntersection(b);
                    writer.printf("%s - %s = %s</br>", a.getNames().get(0), b.getNames().get(0), intersect == null ? "?" : intersect.toString());
                }
            }

            writer.println("</pre></body></html>");
        } catch (Exception e) {
            LOGGER.error("error", e);
        }
    }

}
