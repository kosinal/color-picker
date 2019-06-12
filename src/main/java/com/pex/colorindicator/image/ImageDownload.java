package com.pex.colorindicator.image;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.UUID;

@Component
@Slf4j
public class ImageDownload {

    @SneakyThrows
    public Path downloadImage(Path tmpPrefix, String urlParam) {
        final String uuid = UUID.randomUUID().toString();
        final URL url = new URL(urlParam);
        final Path targetPath = tmpPrefix.resolve(uuid);
        if (saveFile(url, targetPath)) {
            return targetPath;
        } else {
            return null;
        }
    }

    private boolean saveFile(URL url, Path targetPath) {
        try {
            ImageIO.write(ImageIO.read(url), "png", targetPath.toFile());
            return true;
            // From time to time, image IO throws null pointer exception
        } catch (IOException | NullPointerException e) {
            log.error("Unable to download file {}", url.toString());
            return false;
        }
    }
}
