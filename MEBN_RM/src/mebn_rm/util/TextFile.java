/*
 * Decompiled with CFR 0_118.
 */
package mebn_rm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class TextFile {
    public static final String load(String file) {
        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                text = sb.toString();
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static final void save(String file, String sbn) {
        Writer writer = null;
        try {
            try {
                writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file), "utf-8"));
                writer.write(sbn);
            }
            catch (IOException var3_3) {
                try {
                    writer.close();
                }
                catch (Exception var5_4) {}
            }
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception var5_6) {}
        }
    }

    public static void main(String[] args) {
    }
}

