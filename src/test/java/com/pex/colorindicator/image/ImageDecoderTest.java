package com.pex.colorindicator.image;

import com.pex.colorindicator.SpringBootRunner;
import com.pex.colorindicator.domain.DecodedPicture;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;
import java.util.UUID;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class ImageDecoderTest extends SpringBootRunner {

    @Autowired
    private ImageDecoder imageDecoder;

    private DecodedPicture createTestPicture(String path) {
        return new DecodedPicture(
                UUID.randomUUID().toString(),
                Paths.get(String.format("./src/test/resources/com/pex/colorindicator/image/%s", path))
        );
    }

    @Test
    public void checkSingleColorImage() {
        DecodedPicture picture = createTestPicture("single_color.png");
        imageDecoder.decodeImage(picture);
        Assert.assertEquals(
                "#3F48CC", picture.getBestColors()
        );
    }

    @Test
    public void checkTripleColorImage() {
        DecodedPicture picture = createTestPicture("triple_color.png");
        imageDecoder.decodeImage(picture);
        Assert.assertEquals(
                "#ED1C24,#A349A4,#FFAEC9", picture.getBestColors()
        );
    }

    @Test
    public void checkMoreColorImage() {
        DecodedPicture picture = createTestPicture("more_color.png");
        imageDecoder.decodeImage(picture);
        Assert.assertEquals(
                "#ED1C24,#3F48CC,#FF7F27", picture.getBestColors()
        );
    }

    @Test
    public void checkBorderColorImage() {
        DecodedPicture picture = createTestPicture("border_color.png");
        imageDecoder.decodeImage(picture);
        Assert.assertEquals(
                "#3F48CC,#ED1C24", picture.getBestColors()
        );
    }
}