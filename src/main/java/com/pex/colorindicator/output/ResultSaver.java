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

@Component
public class ResultSaver {
    private final OutputConfiguration outputConfiguration;
    private final BlockingQueue<DecodedPicture> queue;

    public ResultSaver(OutputConfiguration outputConfiguration) throws IOException {
        this.outputConfiguration = Validate.notNull(outputConfiguration, "Configurator cannot be null");
        this.queue = new ArrayBlockingQueue<>(outputConfiguration.getBatchSize());
        createOutputFile(outputConfiguration.getPath());
    }

    private void createOutputFile(Path path) throws IOException {
        File resFile = path.toFile();
        if (resFile.exists()) {
            FileUtils.deleteQuietly(resFile);
        }
        boolean fileCreated = resFile.createNewFile();
        if (!fileCreated) {
            throw new IllegalStateException("Unable to create result file");
        }
    }

    @SneakyThrows
    public void add(DecodedPicture decodedPicture) {
        queue.put(decodedPicture);
    }

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

    private String decodeImageToOutput(DecodedPicture decodedPicture) {
        return String.format("%1$s%2$s%3$s", decodedPicture.getUrl(), outputConfiguration.getOutputSeparator(), decodedPicture.getBestColors());
    }
}
