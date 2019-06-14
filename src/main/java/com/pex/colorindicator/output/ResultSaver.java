package com.pex.colorindicator.output;

import com.pex.colorindicator.config.OutputConfiguration;
import com.pex.colorindicator.domain.DecodedPicture;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * Object used for holding the information about the downloaded and decoded images.
 * {@link ResultSaver#add(DecodedPicture)} is able to handle the inserts from multiple threads simultaneously. Order
 * should be the same as the order of calling, but it is not guaranteed.
 */
@Component
public class ResultSaver {
    /**
     * Configuration of the application
     */
    private final OutputConfiguration outputConfiguration;
    /**
     * Concurrent queue used for storing results
     */
    private final BlockingQueue<DecodedPicture> queue;

    public ResultSaver(OutputConfiguration outputConfiguration) throws IOException {
        this.outputConfiguration = Validate.notNull(outputConfiguration, "Configurator cannot be null");
        this.queue = new ArrayBlockingQueue<>(outputConfiguration.getBatchSize());
        createOutputFile(outputConfiguration.getPath());
    }

    /**
     * This method makes sure, that output file always exists for program and it is empty.
     *
     * @param path path for the output file
     * @throws IOException if problem with deleting or creating the file occurs
     */
    private void createOutputFile(Path path) throws IOException {
        File resFile = path.toFile();
        if (resFile.exists()) {
            FileUtils.deleteQuietly(resFile);
        }
        boolean fileCreated = resFile.createNewFile();
        if (!fileCreated) {
            throw new IOException("Unable to create result file");
        }
    }

    /**
     * The the queue used for holding. This method should not be used directly in the program, but it is for testing
     * purposes mainly.
     *
     * @return queue for holding decoded picture
     */
    BlockingQueue<DecodedPicture> getQueue() {
        return queue;
    }

    /**
     * Add new picture to the queue. Can be accessed from multiple thread simultaneously
     *
     * @param decodedPicture decoded picture to be stored
     */
    @SneakyThrows
    public void add(DecodedPicture decodedPicture) {
        queue.put(decodedPicture);
    }

    /**
     * Get all results from stream , create a list of String entries and append them to the file. After appending
     * lines, the queue is cleared.
     * This function should not be called if another thread adding new elements, because it may lead to nondeterministic
     * results.
     */
    @SneakyThrows
    public void appendResultIntoFile() {
        List<String> resultList = queue.stream().map(this::decodeImageToOutput).collect(Collectors.toList());
        queue.clear();
        Files.write(
                outputConfiguration.getPath(),
                resultList,
                StandardOpenOption.APPEND
        );
    }

    /**
     * Create a line into result file from {@link DecodedPicture}
     *
     * @param decodedPicture decoded picture
     * @return line into result file
     */
    private String decodeImageToOutput(DecodedPicture decodedPicture) {
        return String.format("%1$s%2$s%3$s", decodedPicture.getUrl(), outputConfiguration.getOutputSeparator(), decodedPicture.getBestColors());
    }
}
