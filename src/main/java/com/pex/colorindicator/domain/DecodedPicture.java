package com.pex.colorindicator.domain;

import lombok.Data;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Domain object used for holding counting most common color in picture
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Data
public class DecodedPicture {
    /**
     * Original url of picture
     */
    private final String url;
    /**
     * Path where cached downloaded file is on disk
     */
    private final Optional<Path> cachedFile;
    /**
     * Calculated best colors in a format, which should be reported in target file
     */
    private String bestColors;

    public DecodedPicture(String url, Optional<Path> cachedFile) {
        this.url = url;
        this.cachedFile = cachedFile;
    }
}
