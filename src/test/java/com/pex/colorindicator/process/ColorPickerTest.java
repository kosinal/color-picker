package com.pex.colorindicator.process;

import com.pex.colorindicator.SpringBootRunner;
import com.pex.colorindicator.config.OutputConfiguration;
import com.pex.colorindicator.domain.DecodedPicture;
import com.pex.colorindicator.image.ImageDecoder;
import com.pex.colorindicator.image.ImageDownload;
import com.pex.colorindicator.output.ResultSaver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;

public class ColorPickerTest extends SpringBootRunner {

    private final String inPath = "./src/test/resources/com/pex/colorindicator/process/links101.txt";
    @Autowired
    private ColorPicker colorPicker;
    @MockBean
    private ImageDownload imageDownload;
    @MockBean
    private ImageDecoder imageDecoder;
    @MockBean
    private ResultSaver resultSaver;
    @MockBean
    private OutputConfiguration outputConfiguration;
    @Captor
    private ArgumentCaptor<String> urlCaptor;
    @Captor
    private ArgumentCaptor<DecodedPicture> imageCaptor;

    @Before
    public void setUp() {
        Mockito.when(outputConfiguration.getBatchSize()).thenReturn(1000);
        Mockito.when(imageDownload.downloadImage(Mockito.any(), Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(invocationOnMock.getArgument(0)));
        Mockito.when(imageDecoder.decodeImage(Mockito.any())).thenAnswer(
                invocationOnMock -> invocationOnMock.getArgument(0)
        );
    }

    @Test
    public void checkAllInOneProcessing() throws IOException {
        colorPicker.processInputFile(inPath);
        Mockito.verify(resultSaver, Mockito.times(101)).add(Mockito.any());
        Mockito.verify(resultSaver).appendResultIntoFile();
    }

    @Test
    public void checkThatBatchIsProcessing() throws IOException {
        Mockito.when(outputConfiguration.getBatchSize()).thenReturn(10);
        colorPicker.processInputFile(inPath);
        Mockito.verify(resultSaver, Mockito.times(101)).add(Mockito.any());
        Mockito.verify(resultSaver, Mockito.times(11)).appendResultIntoFile();
    }

    @Test
    public void checkAllFilesDownloaded() throws IOException {
        colorPicker.processInputFile(inPath);
        Mockito.verify(imageDownload, Mockito.times(101)).downloadImage(Mockito.any(), urlCaptor.capture());

        List<String> allValues = urlCaptor.getAllValues();
        allValues.sort(String::compareTo);

        List<String> expValues = Files.readAllLines(Paths.get(inPath));
        expValues.sort(String::compareTo);

        assertArrayEquals(expValues.toArray(), allValues.toArray());
    }

    @Test
    public void checkThatAllFilesAreProcessed() throws IOException {
        colorPicker.processInputFile(inPath);
        Mockito.verify(imageDecoder, Mockito.times(101)).decodeImage(imageCaptor.capture());

        Object[] allValues = imageCaptor.getAllValues().stream().map(DecodedPicture::getUrl)
                .sorted(String::compareTo).toArray();

        List<String> expValues = Files.readAllLines(Paths.get(inPath));
        expValues.sort(String::compareTo);

        assertArrayEquals(expValues.toArray(), allValues);
    }
}