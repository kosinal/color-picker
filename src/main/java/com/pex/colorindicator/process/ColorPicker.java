package com.pex.colorindicator.process;

import com.pex.colorindicator.config.OutputConfiguration;
import com.pex.colorindicator.domain.DecodedPicture;
import com.pex.colorindicator.image.ImageDecoder;
import com.pex.colorindicator.image.ImageDownload;
import com.pex.colorindicator.output.ResultSaver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("!test")
public class ColorPicker {

    private final ImageDownload imageDownload;
    private final ImageDecoder imageDecoder;
    private final ResultSaver resultSaver;
    private final OutputConfiguration outputConfiguration;

    public ColorPicker(ImageDownload imageDownload, ImageDecoder imageDecoder, ResultSaver resultSaver,
                       OutputConfiguration outputConfiguration) {
        this.imageDownload = Validate.notNull(imageDownload, "Image download cannot be null");
        this.imageDecoder = Validate.notNull(imageDecoder, "Image decoder cannot be null");
        this.resultSaver = Validate.notNull(resultSaver, "Result saver cannot be null");
        this.outputConfiguration = Validate.notNull(outputConfiguration, "Output Configuration cannot be null");
    }

    public void processInputFile(String path) throws IOException {
        final Path sourceFile = Paths.get(path);
        final Path tmpFolder = Files.createTempDirectory("color-check");
        final int batchSize = outputConfiguration.getBatchSize();
        try (FileReader fr = new FileReader(sourceFile.toFile());
             BufferedReader br = new BufferedReader(fr)) {
            List<String> buffer = new ArrayList<>(batchSize);
            String line;
            while ((line = br.readLine()) != null) {
                buffer.add(line);
                if (buffer.size() == batchSize) {
                    processFileMetadata(tmpFolder, buffer);
                }
            }
            if (!buffer.isEmpty()) {
                processFileMetadata(tmpFolder, buffer);
            }
        }
        FileUtils.deleteDirectory(tmpFolder.toFile());
    }

    private void processFileMetadata(Path tmpFolder, List<String> buffer) {
        buffer.parallelStream()
                .map(i -> new DecodedPicture(i, imageDownload.downloadImage(tmpFolder, i)))
                .filter(i -> i.getCachedFile() != null)
                .map(imageDecoder::decodeImage)
                .forEach(resultSaver::add);
        resultSaver.appendResultIntoFile();
        buffer.clear();
    }

}
