package com.pex.colorindicator.cmd;

import com.pex.colorindicator.process.ColorPicker;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class CmdRunner implements CommandLineRunner {

    private final ColorPicker colorPicker;

    public CmdRunner(ColorPicker colorPicker) {
        this.colorPicker = Validate.notNull(colorPicker, "Color picker cannot be null");
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Missing path argument");
        }
        colorPicker.processInputFile(args[0]);
    }
}
