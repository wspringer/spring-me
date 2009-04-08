package me.springframework.sample.cmdline;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class MovieRendererOption extends OptionHandler<MovieRenderer> {

    protected MovieRendererOption(CmdLineParser parser, OptionDef option, Setter<? super MovieRenderer> setter) {
        super(parser, option, setter);
    }

    @Override
    public String getDefaultMetaVariable() {
        return "FORMAT";
    }

    @Override
    public int parseArguments(Parameters parameters) throws CmdLineException {
        return 1;
    }

}
