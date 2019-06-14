package com.pex.colorindicator.image;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

/**
 * Downloader of images from given URL
 */
@Component
@Slf4j
public class ImageDownload {

    /**
     * Download image from given URL and save the result to the cache directory. If the file
     * is downloaded, return Path of the file. If not, return empty {@link Optional}
     *
     * @param cacheDirectory directory used for caching
     * @param downloadURL    url for download the picture
     * @return path to the downloaded picture
     */
    @SneakyThrows
    public Optional<Path> downloadImage(Path cacheDirectory, String downloadURL) {
        final String uuid = UUID.randomUUID().toString();
        final URL url = new URL(downloadURL);
        final Path targetPath = cacheDirectory.resolve(uuid);
        if (saveFile(url, targetPath)) {
            return Optional.of(targetPath);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Save the image from given URL into file defined on target path
     *
     * @param url        url of file to be downloaded
     * @param targetPath path where file should be stored
     * @return true if file has been downloaded. Otherwise false
     */
    private boolean saveFile(URL url, Path targetPath) {
        try {
            ImageIO.write(ImageIO.read(url), "png", targetPath.toFile());
            return true;
            // From time to time, image IO throws null pointer exception
        } catch (IOException | NullPointerException e) {
            log.error("Unable to download file {}. ({})", url.toString(), e.getMessage());
            return false;
        }
    }
}
