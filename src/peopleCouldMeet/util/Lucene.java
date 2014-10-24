package peopleCouldMeet.util;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import peopleCouldMeet.parser.ParseDump2Parts;
import peopleCouldMeet.parser.Person;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Seky on 2. 10. 2014.
 * Trieda sa stara o pracu s Apache lucene.
 */
public class Lucene {
    private static final Logger LOGGER = Logger.getLogger(Lucene.class.getName());

    // Miesto ulozsika indexu
    private static final File DATA_DIR = new File(ParseDump2Parts.DIR, "lucene");

    // Konstatny pre Lucene
    private static final Version VERSION = Version.LUCENE_4_9;
    private static final int HITS = 5;

    private final DateTimeFormatter fmt; // formatovac datumov
    private final StandardAnalyzer textAnalyzer; // analyzer textu
    private Directory dir; // ulozisko indexu

    private Lucene() {
        fmt = ISODateTimeFormat.basicDate();
        textAnalyzer = new StandardAnalyzer(VERSION);
        try {
            dir = new SimpleFSDirectory(DATA_DIR);
        } catch (IOException e) {
            LOGGER.error("Lucene could not start.", e);
        }
    }

    public static Lucene getInstance() {
        return LuceneHolder.INSTANCE;
    }

    /**
     * Metoda na pridanie mnoziny ludi do Lucene
     */
    public void addPeople(List<Person> people) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(VERSION, textAnalyzer);
        IndexWriter writer = new IndexWriter(dir, config);
        Document doc;
        for (Person p : people) {
            for (String name : p.getNames()) {
                // Pridanie jednotlivej osoby
                doc = serializePerson(p.getId(), name, p.getBirth(), p.getDeath());
                writer.addDocument(doc);
            }
        }
        LOGGER.info("Commiting.");
        writer.commit();
        writer.close();
    }

    /**
     * Metoda na konvertovanie atributov osoby na do dokumentu (lucene format).
     */
    private Document serializePerson(String id, String name, DateTime birth, DateTime death) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("name", name, Field.Store.YES));
        if (birth != null) {
            doc.add(new StringField("birth", birth.toString(fmt), Field.Store.YES));
        }
        if (death != null) {
            doc.add(new StringField("death", death.toString(fmt), Field.Store.YES));
        }
        doc.add(new StringField("id", id, Field.Store.YES));
        return doc;
    }

    /**
     * Metoda na ziskanie osoby v nasom formate zo strktury dokumentu (struktura Lucene)
     */
    private Person deserializePerson(Document doc) {
        Person p = new Person(doc.get("id"));
        String birth = doc.get("birth");
        if (birth != null) {
            p.setBirth(fmt.parseDateTime(birth));
        }
        String death = doc.get("death");
        if (death != null) {
            p.setDeath(fmt.parseDateTime(death));
        }
        p.addName(doc.get("name"));
        return p;
    }

    /**
     * Ukoncenie prace s lucene.
     */
    public void close() {
        try {
            textAnalyzer.close();
            dir.close();
        } catch (Exception e) {
            LOGGER.error("Lucene could not close.", e);
        }
    }

    public StandardAnalyzer getTextAnalyzer() {
        return textAnalyzer;
    }

    /**
     * Metoda na vyhladavanie osoby. Vyhladavanie je na zaklade mena.
     * VYsledkom hladania je struktura osoby.
     */
    public Set<Person> find(String name) throws Exception {
        // Otvorenie indexu
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Vyhladanie na zaklade mena, definujeme sposob vyhodnotenia
        Query query = new QueryParser(VERSION, "name", textAnalyzer).parse(name);
        TopScoreDocCollector collector = TopScoreDocCollector.create(HITS, true);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        // Presun struktury DOC do Person
        Set<Person> outcome = new HashSet<>();
        for (ScoreDoc doc : hits) {
            int documentID = doc.doc;
            Document d = searcher.doc(documentID);
            Person p = deserializePerson(d);
            outcome.add(p);
        }
        return Collections.unmodifiableSet(outcome);
    }

    private static class LuceneHolder {
        private static final Lucene INSTANCE = new Lucene();
    }
}
