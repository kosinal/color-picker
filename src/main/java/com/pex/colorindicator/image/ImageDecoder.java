package com.pex.colorindicator.image;

import com.pex.colorindicator.config.OutputConfiguration;
import com.pex.colorindicator.domain.DecodedPicture;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Decoder for counting occurrence of colors in the given image stored on disk.
 */
@Component
public class ImageDecoder {
    /**
     * Configuration of application
     */
    private final OutputConfiguration outputConfiguration;

    public ImageDecoder(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = Validate.notNull(outputConfiguration, "Configurator cannot be null");
    }

    /**
     * Load picture saved in cached file on disk and count the number of most common colors in the picture.
     * The number of colors counted is determined by {@link OutputConfiguration#getMostCommonColorCount()}. The
     * result is save into input parameter {@link DecodedPicture} as a string that should be present in
     * result file
     *
     * @param decodedPicture picture to be decoded
     * @return the same instance of input picture, with filled {@link DecodedPicture#getBestColors()}
     */
    @SneakyThrows
    public DecodedPicture decodeImage(DecodedPicture decodedPicture) {
        Optional<Path> cachedFile = decodedPicture.getCachedFile();
        if (!cachedFile.isPresent()) {
            throw new IllegalStateException("Got image without cached file");
        }
        final BufferedImage image = ImageIO.read(cachedFile.get().toFile());
        final Map<Integer, Integer> resMap = new HashMap<>();

        scanImage(image, i -> resMap.merge(i, 1, Integer::sum));
        String bestColors = getMostCommonColor(resMap);
        decodedPicture.setBestColors(bestColors);

        return decodedPicture;
    }

    /**
     * Get the most common colors in the picture, decode them into string and use separator from
     * {@link OutputConfiguration#getOutputSeparator()} for creating result.
     *
     * @param resMap map with color in key and occurrence in value
     * @return most common color in the string
     */
    private String getMostCommonColor(Map<Integer, Integer> resMap) {
        return resMap.entrySet().stream()
                .sorted(Comparator.comparing(i -> -i.getValue()))
                .limit(outputConfiguration.getMostCommonColorCount())
                .map(this::rgbToString)
                .collect(Collectors.joining(outputConfiguration.getOutputSeparator()));
    }

    /**
     * Scan whole image pixel by pixel and get the RGB color for it. The result {@link BufferedImage#getRGB(int, int)}
     * from is stripped for the Alpha channel, because we are looking only for RGB part(in other words, only first 3 bytes
     * from the result is taken in consideration). Fixed key is then passed to imageProcessor.
     *
     * @param image          image to be scanned
     * @param imageProcessor processor for fixed keys
     */
    private void scanImage(BufferedImage image,
                           IntConsumer imageProcessor) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        IntStream.range(0, width).flatMap(
                x -> IntStream.range(0, height).map(
                        y -> image.getRGB(x, y)
                )
        ).forEach(
                key -> {
                    final int fixedKey = key & 0xffffff;
                    imageProcessor.accept(fixedKey);
                }
        );
    }

    /**
     * Convert key in the input entry into hexadecimal RGB value
     *
     * @param entry entry with color in key and occurrence in value (occurrence not used)
     * @return hexadecimal representation
     */
    private String rgbToString(Map.Entry<Integer, Integer> entry) {
        int color = entry.getKey();

        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        return String.format("#%02X%02X%02X", red, green, blue);
    }

}
