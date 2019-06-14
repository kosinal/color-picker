package com.pex.colorindicator.cmd;

import com.pex.colorindicator.process.ColorPicker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * Component for running {@link ColorPicker} with arguments passed from command line.
 * It's only purpose is for make testing of other beans easier.
 * Do not remove @Profile from the definition of the component, otherwise Spring Boot Tests will fail.
 */
@Component
@Slf4j
@Profile("!test")
public class CmdRunner implements CommandLineRunner {

    /**
     * Main logic for picking the right color
     */
    private final ColorPicker colorPicker;

    public CmdRunner(ColorPicker colorPicker) {
        this.colorPicker = Validate.notNull(colorPicker, "Color picker cannot be null");
    }

    /**
     * Check, if there is at least one passed arguments. Than for each argument process the file using
     * {@link ColorPicker}.
     * All files in args are processed, even if one fail. But if one will fail, the result of the run
     * will throw an {@link IllegalStateException}.
     *
     * @param args input paths to files with links
     * @throws IllegalStateException thrown when any job will fail
     */
    @Override
    public void run(String... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Missing path argument");
        }
        Boolean success = Stream.of(args)
                .map(this::safeProcessInputFile)
                .reduce(true, (left, right) -> left && right);
        if (!success) {
            throw new IllegalStateException("Some processing failed. Check the log.");
        }
    }

    /**
     * Process input file by {@link ColorPicker}. If no exception is raised, than return true.
     * If there is some exception, than return false and log the exception using logger.
     *
     * @param fileName name of the file to be process
     * @return indicator, if the file was processed without errors
     */
    private boolean safeProcessInputFile(String fileName) {
        try {
            colorPicker.processInputFile(fileName);
            return true;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return false;
        }
    }
}
