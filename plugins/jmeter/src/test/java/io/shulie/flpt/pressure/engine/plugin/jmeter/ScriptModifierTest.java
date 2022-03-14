package io.shulie.flpt.pressure.engine.plugin.jmeter;

import java.io.File;
import java.io.FileWriter;

import org.dom4j.Document;

import org.dom4j.io.SAXReader;

import lombok.extern.slf4j.Slf4j;

import io.shulie.flpt.pressure.engine.util.FileUtils;

/**
 * @author xuyh
 */
@Slf4j
public class ScriptModifierTest {

    public static void main(String[] args) throws Exception {
        File jmxFile = new File("~/test/3/resources/nested.jmx");
        SAXReader reader = new SAXReader();
        Document document = reader.read(jmxFile);
        document.elementByID("abc").addAttribute("guiclass", "123");

        FileWriter writer = null;
        try {
            writer = new FileWriter(FileUtils.createFilePreDelete("~/test/3/resources/jpgc-final.jmx"));
            document.write(writer);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }
}
