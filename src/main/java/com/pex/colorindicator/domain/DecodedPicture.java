package com.pex.colorindicator.domain;

import lombok.Data;

import java.nio.file.Path;

@Data
public class DecodedPicture {
    private final String url;
    private final Path cachedFile;
    private String bestColors;

    public DecodedPicture(String url, Path cachedFile) {
        this.url = url;
        this.cachedFile = cachedFile;
    }
}
