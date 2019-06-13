package com.pex.colorindicator.output;

import com.pex.colorindicator.config.OutputConfiguration;
import com.pex.colorindicator.domain.DecodedPicture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ResultSaverTest {

    private final int batchSize = 1000;
    private ResultSaver resultSaver;
    private Path testResult;

    @Before
    public void setUp() throws Exception {
        OutputConfiguration outputConfiguration = Mockito.mock(OutputConfiguration.class);
        testResult = Files.createTempDirectory("resultSaverTest");
        Mockito.when(outputConfiguration.getPath()).thenReturn(testResult);
        Mockito.when(outputConfiguration.getBatchSize()).thenReturn(batchSize);
        Mockito.when(outputConfiguration.getOutputSeparator()).thenReturn(";");

        resultSaver = new ResultSaver(outputConfiguration);
    }

    private DecodedPicture createTestPicture(int index) {
        DecodedPicture picture = new DecodedPicture(String.format("http://%d", index), null);
        picture.setBestColors(Integer.toString(index));
        return picture;
    }

    @Test
    public void multiThreadAccess() {
        generateBatch(true, resultSaver::add);
        assertEquals(batchSize, resultSaver.getQueue().size());
    }

    @Test
    public void countResultFileSize() throws IOException {
        generateBatch(true, resultSaver::add);
        resultSaver.appendResultIntoFile();
        assertEquals(batchSize, Files.readAllLines(testResult).size());
    }

    @Test
    public void checkFileContent() throws IOException {
        List<DecodedPicture> decodedPictures = new ArrayList<>(batchSize);
        generateBatch(false, resultSaver::add);
        generateBatch(false, decodedPictures::add);

        Object[] expectedResult = decodedPictures.stream().map(
                i -> String.format("%s;%s", i.getUrl(), i.getBestColors())
        ).toArray();

        resultSaver.appendResultIntoFile();
        assertArrayEquals(expectedResult, Files.readAllLines(testResult).toArray());
    }

    private void generateBatch(boolean parallel, Consumer<DecodedPicture> consumer) {
        IntStream seed = IntStream.range(0, batchSize);
        if (parallel) {
            seed = seed.parallel();
        }
        seed.mapToObj(this::createTestPicture).forEach(consumer);
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(testResult);
    }
}