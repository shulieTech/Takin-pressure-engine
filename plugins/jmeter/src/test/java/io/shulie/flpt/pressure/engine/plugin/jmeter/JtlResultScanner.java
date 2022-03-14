package io.shulie.flpt.pressure.engine.plugin.jmeter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author xuyh
 */
public class JtlResultScanner {
    public static void main(String[] args) {
        String logFilePathName = "/Users/johnson/fsdownload/96.0-1590378924254-result.jtl";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFilePathName)));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains(",200,OK,")) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
