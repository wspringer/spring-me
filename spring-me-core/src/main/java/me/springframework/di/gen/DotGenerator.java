/**
 * Copyright (C) 2008 TomTom
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Spring ME; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package me.springframework.di.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import me.springframework.di.Configuration;
import me.springframework.di.antlr.InstanceAttributeRenderer;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


/**
 * A class generating a dot file reflecting the DI configuration.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class DotGenerator {

    /**
     * The template to use.
     */
    private static final String TEMPLATE = "/dot.stg";

    /**
     * The catalog providing style information for shapes.
     */
    private StyleCatalog catalog;

    /**
     * Constructs a new instance.
     * 
     * @param catalog
     *            The catalog providing shape style information.
     */
    public DotGenerator(StyleCatalog catalog) {
        super();
        this.catalog = catalog;
    }

    /**
     * Constructs a new instance. (Default styles.)
     */
    public DotGenerator() {
        this(new NullStyleCatalog());
    }

    /**
     * Generates a dot file from the {@link Configuration} passed in.
     * 
     * @param config
     *            The configuration.
     * @param targetFile
     *            The target file. (The file, nor the target directory need to
     *            exist yet.)
     * @throws GeneratorException
     *             If - for some reason - it turns out to be impossible to
     *             generate the dot file.
     */
    public final void generate(Configuration config, File targetFile) throws GeneratorException {
        Writer writer = null;
        try {
            FileUtils.forceMkdir(targetFile.getParentFile());
            writer = new FileWriter(targetFile);
            FileUtils.writeStringToFile(targetFile, process(config));
        } catch (IOException ioe) {
            throw new GeneratorException(ioe);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * Generates the text to be written.
     * 
     * @param definitions
     *            The {@link Configuration} defining the configuration of wired
     *            objects.
     * @return The text of the output file.
     */
    public String process(Configuration definitions) {
        InputStream in = ContextGenerator.class.getResourceAsStream(TEMPLATE);
        Reader reader = new InputStreamReader(in);
        StringTemplateGroup group = new StringTemplateGroup(reader);
        StringTemplate template = group.getInstanceOf("template");
        template.registerRenderer(String.class, new InstanceAttributeRenderer(catalog));
        template.setAttribute("definitions", definitions);
        return template.toString();
    }

    /**
     * The interface allowing us to map type names to style information.
     * 
     */
    public interface StyleCatalog {

        /**
         * Returns the fill color for the shape representing the bean of the
         * type passed in.
         * 
         * @param type
         *            The type name of the bean for which we need its fill
         *            color.
         * @return The fill color. (A color name.)
         */
        String getColor(String type);

    }

    /**
     * A null implementation, allowing us to avoid having to pass in catalogs
     * when we don't want to.
     * 
     * @author Wilfred Springer (wis)
     * 
     */
    public static class NullStyleCatalog implements StyleCatalog {

        /*
         * (non-Javadoc)
         * @see me.springframework.di.gen.DotGenerator.StyleCatalog#getColor(java.lang.String)
         */
        public String getColor(String type) {
            return "white";
        }

    }

}
