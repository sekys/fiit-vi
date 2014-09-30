package util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Seky on 30. 9. 2014.
 */
public class GZIP {
    public static BufferedWriter write(String name) throws Exception {
        GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File(name)));
        return new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
    }

    public static BufferedReader read(String name) throws Exception {
        FileInputStream fis = new FileInputStream(name);
        GZIPInputStream gzis = new GZIPInputStream(fis);
        InputStreamReader reader = new InputStreamReader(gzis);
        return new BufferedReader(reader);
    }
}
