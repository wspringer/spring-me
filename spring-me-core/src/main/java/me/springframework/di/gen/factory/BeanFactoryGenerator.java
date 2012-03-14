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
package me.springframework.di.gen.factory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import me.springframework.di.Configuration;
import me.springframework.di.antlr.JavaAttributeRenderer;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.commons.io.IOUtils;


/**
 * An object capable of generating a bean factory based on a {@linkplain Configuration
 * configuration} passed in. The output can be customized by passing an implementation of
 * {@link BeanFactoryType}. Typically, this is one of the values of {@link BeanFactoryTypes}.
 * 
 * @author Wilfred Springer
 * 
 */
public class BeanFactoryGenerator {

    /** The template used to generate code. */
    private final static String TEMPLATE = "/context.stg";

    /**
     * Generates a Java source file with everything on board to construct the application context of
     * an application.
     * 
     * @param destination An abstraction of where and how to generate the target output.
     * @param definitions The definitions of the object instances wired together.
     * @param details Some metadata on the BeanFactory to generate. (Typically, one of the values of
     *            {@link BeanFactoryTypes}.
     * @throws GeneratorException If - for some reason - the {@link BeanFactoryGenerator} fails to
     *             generate the desired output.
     */
    public static void generate(Destination destination, Configuration definitions,
            BeanFactoryType beanFactoryType) throws GeneratorException {

        if (!definitions.getSetSources().isEmpty()
                && beanFactoryType.getSetImplementationName() == null) {
            throw new IllegalArgumentException(
                    "Sets are not supported by BeanFactoryType: " + beanFactoryType);
        }
        if (!definitions.getPropertiesSources().isEmpty()
                && beanFactoryType.getPropertiesImplementationName() == null) {
            throw new IllegalArgumentException(
                    "Properties are not supported by BeanFactoryType" + beanFactoryType);
        }

        Writer writer = null;
        try {
            writer = destination.getWriter();
            IOUtils.write(process(destination, definitions, beanFactoryType), writer);
        } catch (IOException ioe) {
            throw new GeneratorException(ioe);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * Performs the actual work, generating the source code.
     * 
     * @param destination The target settings for the output.
     * @param definitions A representation of the objects that need to be wired.
     * @param beanFactoryType The {@link BeanFactoryType} guiding the generation process.
     * @return The sources for wiring the objects together.
     */
    private static String process(Destination destination, Configuration definitions, BeanFactoryType beanFactoryType) {
        InputStream in = BeanFactoryGenerator.class.getResourceAsStream(TEMPLATE);
        Reader reader = new InputStreamReader(in);
        StringTemplateGroup group = new StringTemplateGroup(reader);
        StringTemplate template = group.getInstanceOf("template");
        template.registerRenderer(String.class, new JavaAttributeRenderer());
        template.setAttribute("definitions", definitions);
        template.setAttribute("destination", destination);
        template.setAttribute("beanFactoryType", beanFactoryType);
        return template.toString();
    }

}
