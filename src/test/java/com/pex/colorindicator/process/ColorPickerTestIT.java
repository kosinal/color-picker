package com.pex.colorindicator.process;

import com.pex.colorindicator.SpringBootRunner;
import com.pex.colorindicator.config.OutputConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ColorPickerTestIT extends SpringBootRunner {

    @Autowired
    private ColorPicker picker;

    @Autowired
    private OutputConfiguration configuration;

    @Test
    public void downloadAllLinks() throws IOException {
        final String inPath = "./src/test/resources/com/pex/colorindicator/process/links101.txt";
        final String expResult = "./src/test/resources/com/pex/colorindicator/process/exp-result.csv";

        picker.processInputFile(inPath);

        List<String> results = Files.readAllLines(configuration.getPath());
        results.sort(String::compareTo);

        List<String> expResults = Files.readAllLines(Paths.get(expResult));

        Assert.assertArrayEquals(expResults.toArray(), results.toArray());
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(configuration.getPath());
    }
}