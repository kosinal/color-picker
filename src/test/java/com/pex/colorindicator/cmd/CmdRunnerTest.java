package com.pex.colorindicator.cmd;

import com.pex.colorindicator.SpringBootRunner;
import com.pex.colorindicator.process.ColorPicker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;


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
        Mockito.verify(colorPicker).processInputFile(path);
    }
}