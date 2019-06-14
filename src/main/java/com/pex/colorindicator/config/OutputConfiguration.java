package com.pex.colorindicator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Configuration file for setting the properties of application
 */
@Component
@ConfigurationProperties(prefix = "colorpicker")
@Data
public class OutputConfiguration {
    /**
     * Number of lines read in one batch
     */
    private int batchSize;
    /**
     * Separator for output file
     */
    private String outputSeparator;
    /**
     * Path to the target file
     */
    private Path path;
    /**
     * Count of most common colors to take
     */
    private int mostCommonColorCount;
}
