/**
 * Copyright (C) 2009 Original Authors
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spring ME; see the file COPYING. If not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module. An independent module is a module which is not derived from or
 * based on this library. If you modify this library, you may extend this
 * exception to your version of the library, but you are not obligated to
 * do so. If you do not wish to do so, delete this exception statement
 * from your version.
 */
package me.springframework.sample.cmdline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import me.spring.beans.factory.MinimalBeanFactory;

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
        MinimalBeanFactory factory = new DynamicMinimalBeanFactory();
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
