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
package me.springframework.di.maven;

import java.io.File;
import java.io.IOException;

import me.springframework.di.Configuration;
import me.springframework.di.gen.factory.BeanFactoryGenerator;
import me.springframework.di.gen.factory.BeanFactoryType;
import me.springframework.di.gen.factory.BeanFactoryTypes;
import me.springframework.di.gen.factory.Destination;
import me.springframework.di.gen.factory.FileSystemDestination;
import me.springframework.di.gen.factory.GeneratorException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * A Mojo for generating Java based BeanFactories.
 * 
 * @author Wilfred Springer
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class BeanFactoryGeneratorMojo extends AbstractGeneratorMojo {

    /**
     * The directory in which the bean factory source file will be generated.
     * 
     * @parameter expression="${basedir}/target/generated-sources/spring-me"
     * @required
     */
    private File targetDirectory;

    /**
     * @parameter
     */
    private String factoryType;

    /*
     * (non-Javadoc)
     * 
     * @see
     * me.springframework.di.maven.AbstractSpringConfigMojo#process(com.tomtom.di.Configuration)
     */
    public void process(Configuration config, BeanFactory factory) throws MojoExecutionException, MojoFailureException {
        ensureTargetDirectoryExists();
        BeanFactoryType type = BeanFactoryTypes.MINIMAL_JAVA_ME;
        try {
            type = BeanFactoryTypes.valueOf(factoryType);
        } catch (NullPointerException npe) {
            getLog().warn("Falling back to minimal Java ME type of factory.");
        } catch (IllegalArgumentException iae) {
            getLog().warn("Falling back to minimal Java ME type of factory.");
        }
        Destination dest = new FileSystemDestination(factory.getClassName(), targetDirectory);
        try {
            BeanFactoryGenerator.generate(dest, config, type);
            getProject().addCompileSourceRoot(targetDirectory.getAbsolutePath());
        } catch (GeneratorException cge) {
            throw new MojoExecutionException("Failed to generate bean factory.", cge);
        }
    }

    /**
     * Makes sure the target directory exists.
     * 
     * @throws MojoExecutionException If the target directory cannot be created.
     */
    private void ensureTargetDirectoryExists() throws MojoExecutionException {
        try {
            FileUtils.forceMkdir(targetDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to create target directory", e);
        }
    }

}
