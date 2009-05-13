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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;

/**
 * A simple {@link Destination} implementation that will write the output the
 * file system. (Obviously, the most likely candidate for generating output.) In
 * addition, it will make sure that the location is in sync with the package
 * name. (That is, it wil make sure that {@link #basedir} has a directory tree
 * reflecting the packaging structure.
 * 
 * @author Wilfred Springer
 * 
 */
public class FileSystemDestination implements Destination {

    /** The name of the class. */
    private String className;

    /** The package name of the class. */
    private String packageName;

    /**
     * The directory in which the package directory layout will be created.
     */
    private File basedir;

    /**
     * Constructs a new instance.
     * 
     * @param className
     *            The name of the class.
     * @param packageName
     *            The name of the package.
     * @param basedir
     *            The directory in which the package will be created.
     */
    public FileSystemDestination(String className, String packageName,
            File basedir) {
        this.className = className;
        this.packageName = packageName;
        this.basedir = basedir;
    }
    
    public FileSystemDestination(String fqn, File basedir) {
        int i = fqn.lastIndexOf(".");
        if (i >= 0) {
            className = fqn.substring(i + 1);
            packageName = fqn.substring(0, i);
        } else {
            className = fqn;
            packageName = "";
        }
        this.basedir = basedir;
    }

    // JavaDoc inherited
    public String getClassname() {
        return className;
    }

    /**
     * Returns the {@link File} to which all output will be written.
     * 
     * @return The {@link File} to which all output will be written.
     */
    private File getFile() {
        File targetDir = new File(basedir, packageName.replace('.',
                File.separatorChar));
        File targetFile = new File(targetDir, className + ".java");
        return targetFile;
    }

    // JavaDoc inherited
    public String getPackagename() {
        return packageName;
    }

    // JavaDoc inherited
    public Writer getWriter() throws IOException {
        try {
            File destination = getFile();
            FileUtils.forceMkdir(destination.getParentFile());
            return new FileWriter(destination);
        } catch (IOException e) {
            throw new GeneratorException(e);
        }
    }

}
