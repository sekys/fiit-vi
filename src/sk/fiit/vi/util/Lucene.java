package sk.fiit.vi.util;

import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Seky on 2. 10. 2014.
 */
public class Lucene {

    private static CzechAnalyzer analyzer;
    private static Directory index = null;
    private static IndexWriterConfig config;
    private static IndexWriter w = null;
    private static int id = 0;

    public static void initLucene() {
        try {
            analyzer = new CzechAnalyzer(Version.LUCENE_46);
            index = new SimpleFSDirectory(new File("C:\\tmp\\wikiindex"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initLuceneIndexWriter() {
        try {
            config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
            w = new IndexWriter(index, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void index(String pic, String text) {
        try {
            addDoc(pic, text);
            w.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addDoc(String pic, String text) {
        Document doc = new Document();
        doc.add(new TextField("text", text, Field.Store.YES));
        doc.add(new StringField("pic", pic, Field.Store.YES));
        doc.add(new StringField("id", String.valueOf(id++), Field.Store.YES));
        try {
            w.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeLucene() {
        analyzer.close();
        try {
            index.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeLuceneIndexWriter() {
        try {
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void search(String string) {
        String querystr = string;
        Query q;
        ScoreDoc[] hits = null;
        IndexSearcher searcher = null;
        try {
            q = new QueryParser(Version.LUCENE_46, "text", analyzer).parse(querystr);
            int hitsPerPage = 10;

            IndexReader reader = IndexReader.open(index);
            searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
            searcher.search(q, collector);
            hits = collector.topDocs().scoreDocs;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        // DISPLAY RESULT
        String result = "";
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = null;
            try {
                d = searcher.doc(docId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            result += d.get("pic") + System.getProperty("line.separator");
            //System.out.println((i + 1) + ". " + d.get("pic"));

        }
        JFrame jframe = new JFrame("SearchResult: " + hits.length + " hits.");
        jframe.setSize(400, 700);
        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jframe.setLocationRelativeTo(null);
        jframe.add(new JScrollPane(new JTextArea(result)));
        jframe.show();
    }
}
