package backend.code.challenge.n26.banking.util;

import java.io.*;

// Example from https://gist.github.com/isa/878708
public class MultiLineString {

    public static String multiLineString() {
        try {
            StackTraceElement element = new RuntimeException().getStackTrace()[1];
            String name = "src/test/java/" + element.getClassName().replace('.', '/') + ".java";
            String data = convertStreamToString(new FileInputStream(name), element.getLineNumber());
            return data.substring(data.indexOf("/*") + 2, data.indexOf("*/"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String convertStreamToString(InputStream is, int lineNum) {
        /*
        * To convert the InputStream to String we use the
        * BufferedReader.readLine() method. We iterate until the BufferedReader
        * return null which means there's no more data to read. Each line will
        * appended to a StringBuilder and returned as String.
        */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        int i = 1;
        try {
            while ((line = reader.readLine()) != null) {
                if (i++ >= lineNum) {
                    sb.append(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
