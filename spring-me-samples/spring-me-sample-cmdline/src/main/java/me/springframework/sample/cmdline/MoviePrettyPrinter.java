package me.springframework.sample.cmdline;

import java.util.Map;

import me.spring.beans.factory.MinimalBeanFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;



public class MoviePrettyPrinter {

    private Map<String,MovieRenderer> renderers;
    
    public static final void main(String[] args) {
        MinimalBeanFactory factory = null;
        MoviePrettyPrinter printer = (MoviePrettyPrinter) factory.getBean("moviePrinter");
        CmdLineParser parser = new CmdLineParser(printer);
        try {
            parser.parseArgument(args);
            printer.print();
            System.exit(0);
        } catch (CmdLineException ce) {
            parser.printUsage(System.out);
            System.exit(1);
        }
    }

    private void print() {
        
    }
    
}
