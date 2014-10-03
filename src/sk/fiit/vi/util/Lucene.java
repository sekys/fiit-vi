package sk.fiit.vi.util;

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
import sk.fiit.vi.parser.Person;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Seky on 2. 10. 2014.
 */
public class Lucene {
    private static final Logger LOGGER = Logger.getLogger(Lucene.class.getName());
    private static final File DATA_DIR = new File("data/lucene");
    private static final Version VERSION = Version.LUCENE_4_9;
    private static final int HITS = 5;

    private final DateTimeFormatter fmt;
    private final StandardAnalyzer textAnalyzer;
    private Directory dir;

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

    private static class LuceneHolder {
        private static final Lucene INSTANCE = new Lucene();
    }

    public void addPeople(List<Person> people) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(VERSION, textAnalyzer);
        IndexWriter writer = new IndexWriter(dir, config);
        Document doc;
        for (Person p : people) {
            for (String name : p.getNames()) {
                doc = createDoc(p.getId(), name, p.getBirth(), p.getDeath());
                writer.addDocument(doc);
            }
        }
        writer.commit();
        writer.close();
    }

    private Document createDoc(String id, String name, DateTime birth, DateTime death) throws IOException {
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

    public List<Document> find(String name) throws Exception {
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new QueryParser(VERSION, "name", textAnalyzer).parse(name);
        TopScoreDocCollector collector = TopScoreDocCollector.create(HITS, true);
        searcher.search(query, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        List<Document> outcome = new ArrayList();
        for (ScoreDoc doc : hits) {
            int documentID = doc.doc;
            outcome.add(searcher.doc(documentID));
        }
        return Collections.unmodifiableList(outcome);
    }
}
