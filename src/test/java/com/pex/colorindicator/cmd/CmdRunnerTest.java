package com.pex.colorindicator.cmd;

import com.pex.colorindicator.SpringBootRunner;
import com.pex.colorindicator.process.ColorPicker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;


public class CmdRunnerTest extends SpringBootRunner {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private CmdRunner cmdRunner;
    @MockBean
    private ColorPicker colorPicker;

    @Before
    public void setUp() throws Exception {
        cmdRunner = new CmdRunner(colorPicker);
    }

    @Test
    public void checkFailOnNoParams() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing path argument");
        cmdRunner.run();
    }

    @Test
    public void checkPathPassing() throws Exception {
        final String path = "checkPath";
        cmdRunner.run(path);
        verify(colorPicker).processInputFile(path);
    }

    @Test
    public void checkAllFilesAreProcessed() throws Exception {
        final String path = "checkPath";
        final String failPath = "failPath";

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Some processing failed. Check the log.");

        doAnswer(invocationOnMock -> {
            if (invocationOnMock.getArgument(0).equals(failPath)) {
                throw new IllegalArgumentException("Fail");
            } else {
                return null;
            }
        }).when(colorPicker).processInputFile(anyString());

        Exception lastEx = new Exception("Run did not fail, but it should. Check the strategy");
        try {
            cmdRunner.run(failPath, path);
        } catch (Exception e) {
            // We need this so we can check the calling of coloPicker.
            // Exception will be thrown on the end of test
            lastEx = e;
        }

        verify(colorPicker).processInputFile(eq(path));
        verify(colorPicker).processInputFile(eq(failPath));

        throw lastEx;
    }
}