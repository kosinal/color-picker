package com.pex.colorindicator.image;

import com.pex.colorindicator.SpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class ImageDownloadTest extends SpringBootRunner {

    @Autowired
    private ImageDownload imageDownload;

    private Path testPath;

    @Before
    public void setUp() throws Exception {
        testPath = Files.createTempDirectory("testDownload");
    }

    @Test
    public void downloadFromFakeUrl() {
        assertNull(imageDownload.downloadImage(testPath, "https://fakeurl.eu/"));
    }

    @Test
    public void downloadTestFile() throws IOException {
        Path path = imageDownload.downloadImage(testPath, "http://i.imgur.com/TKLs9lo.jpg/");
        assertImagesEquals(Paths.get("./src/test/resources/com/pex/colorindicator/image/TKLs9lo.jpg"), path);
    }

    private void assertImagesEquals(Path expectedImagePath, Path realImagePath) throws IOException {
        BufferedImage expectedImg = ImageIO.read(expectedImagePath.toFile());
        BufferedImage realImg = ImageIO.read(realImagePath.toFile());
        assertEquals(expectedImg.getHeight(), realImg.getHeight());
        assertEquals(expectedImg.getWidth(), realImg.getWidth());

        for (int x = 0; x < expectedImg.getWidth(); x++) {
            for (int y = 0; y < expectedImg.getHeight(); y++) {
                assertEquals(expectedImg.getRGB(x, y), realImg.getRGB(x, y));
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(testPath.toFile());
    }
}