package com.pex.colorindicator.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Data
public class OutputConfiguration {
    private final int batchSize = 99;
    private final String outputSeparator = ",";
    private final Path path = Paths.get("./result.csv");
}
