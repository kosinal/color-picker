package com.pex.colorindicator.process;

import com.pex.colorindicator.config.OutputConfiguration;
import com.pex.colorindicator.domain.DecodedPicture;
import com.pex.colorindicator.image.ImageDecoder;
import com.pex.colorindicator.image.ImageDownload;
import com.pex.colorindicator.output.ResultSaver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for the business logic for counting most common color in the picture
 */
@Component
public class ColorPicker {

    /**
     * Downloader of images from internet
     */
    private final ImageDownload imageDownload;
    /**
     * Decoder of image into most common colors
     */
    private final ImageDecoder imageDecoder;
    /**
     * Queue for holding results and writing to target file
     */
    private final ResultSaver resultSaver;
    /**
     * Configuration of application
     */
    private final OutputConfiguration outputConfiguration;

    public ColorPicker(ImageDownload imageDownload, ImageDecoder imageDecoder, ResultSaver resultSaver,
                       OutputConfiguration outputConfiguration) {
        this.imageDownload = Validate.notNull(imageDownload, "Image download cannot be null");
        this.imageDecoder = Validate.notNull(imageDecoder, "Image decoder cannot be null");
        this.resultSaver = Validate.notNull(resultSaver, "Result saver cannot be null");
        this.outputConfiguration = Validate.notNull(outputConfiguration, "Output Configuration cannot be null");
    }

    /**
     * Read all lines from path given as a input parameters and for every line in the file, compute most common
     * colors and store the result in the file.
     * Due to possible size of the file, all rows are not loaded into memory, but file is read and processed by
     * batches. The result file is also populated by batches.
     * Due to a nature of multiprocessing, the order of the rows in result file is not guaranteed.
     *
     * @param path input path of file
     * @throws IOException if eny IO operation will fail
     */
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

    /**
     * For all links in input parameter, download the file from internet and get most common color for each
     * downloaded image.
     * When all images are processed, same the result into the result file.
     * This operation is used for batch processing
     *
     * @param tmpFolder temporal folder for caching images
     * @param links     list of links to be processed
     */
    private void processFileMetadata(Path tmpFolder, List<String> links) {
        links.parallelStream()
                .map(i -> new DecodedPicture(i, imageDownload.downloadImage(tmpFolder, i)))
                .filter(i -> i.getCachedFile().isPresent())
                .map(imageDecoder::decodeImage)
                .forEach(resultSaver::add);
        resultSaver.appendResultIntoFile();
        links.clear();
    }

}
