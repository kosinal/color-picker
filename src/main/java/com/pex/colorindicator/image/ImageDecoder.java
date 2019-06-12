package com.pex.colorindicator.image;

import com.pex.colorindicator.config.OutputConfiguration;
import com.pex.colorindicator.domain.DecodedPicture;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ImageDecoder {
    private final OutputConfiguration outputConfiguration;

    public ImageDecoder(OutputConfiguration outputConfiguration) {
        this.outputConfiguration = Validate.notNull(outputConfiguration,"Configurator cannot be null");
    }

    @SneakyThrows
    public DecodedPicture decodeImage(DecodedPicture decodedPicture) {
        final BufferedImage image = ImageIO.read(decodedPicture.getCachedFile().toFile());
        final int width = image.getWidth();
        final int height = image.getHeight();
        final Map<Integer, Integer> resMap = new HashMap<>();

        IntStream.range(0, width).flatMap(
                x -> IntStream.range(0, height).map(
                        y -> image.getRGB(x, y)
                )
        ).forEach(
                key -> resMap.merge(key, 1, Integer::sum)
        );

        String bestColors = resMap.entrySet().stream()
                .sorted(Comparator.comparing(i -> -i.getValue()))
                .limit(3)
                .map(this::rgbToString)
                .collect(Collectors.joining(outputConfiguration.getOutputSeparator()));

        decodedPicture.setBestColors(bestColors);

        return decodedPicture;
    }

    private String rgbToString(Map.Entry<Integer, Integer> entry) {
        int color = entry.getKey();

        int blue = color & 0xff;
        int green = (color & 0xff00) >> 8;
        int red = (color & 0xff0000) >> 16;
        return String.format("#%02X%02X%02X", red, green, blue);
    }

}
