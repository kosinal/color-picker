package com.pex.colorindicator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties(prefix = "colorpicker")
@Data
public class OutputConfiguration {
    private int batchSize;
    private String outputSeparator;
    private Path path;
}
