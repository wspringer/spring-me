package me.springframework.sample.cmdline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import me.springframework.sample.cmdline.factory.BeanFactory;

import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.thoughtworks.xstream.XStream;



public class MoviePrettyPrinter {

    private Map<String,MovieListRenderer> renderers;
    
    @Option(metaVar="TYPE", name="-t", usage="Print the file using the specified type of output.")
    private String outputType;
    
    @Option(name="-l", usage="List all types of output.")
    private boolean listTypes;
    
    @Argument(metaVar="INPUT")
    private File inputFile;
    
    public MoviePrettyPrinter(Map<String,MovieListRenderer> renderers) {
        this.renderers = renderers;
    }
    
    public static final void main(String[] args) {
        BeanFactory factory = new BeanFactory();
        MoviePrettyPrinter printer = (MoviePrettyPrinter) factory.getBean("moviePrinter");
        CmdLineParser parser = new CmdLineParser(printer);
        try {
            parser.parseArgument(args);
            if (printer.listTypes) {
                printer.listTypes();
            } else if (printer.outputType != null) {
                MovieListRenderer renderer = printer.renderers.get(printer.outputType);
                if (renderer == null) {
                    System.err.println("Output type " + printer.outputType + " is invalid.");
                    printer.listTypes();
                } else {
                    printer.printOutput(renderer);
                }
            }
            System.exit(0);
        } catch (CmdLineException ce) {
            parser.printUsage(System.out);
            System.exit(1);
        }
    }

    private void listTypes() {
        for (Entry<String, MovieListRenderer> entry : renderers.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getTypeName());
        }
    }
    
    private void printOutput(MovieListRenderer renderer) {
        XStream xstream = new XStream();
        xstream.processAnnotations(new Class[] { XStreamMovies.class, XStream.class });
        XStreamMovies movies = new XStreamMovies();
        InputStream in = null;
        try {
            if (inputFile != null) {
                in = new FileInputStream(inputFile);
            } else {
                in = System.in;
            }
            xstream.fromXML(in, movies);
            renderer.print(movies.getMovies(), System.out);
        } catch (FileNotFoundException e) {
            System.out.println("Failed to read input file.");
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    
}
