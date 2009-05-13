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
import java.io.Writer;

/**
 * An abstraction of the target output.
 * 
 * @author Wilfred Springer
 * 
 */
public interface Destination {

    /**
     * Returns the class name of the Java source file generated.
     * 
     * @return The class name of the Java source file generated.
     */
    String getClassname();

    /**
     * Returns the package name of the Java source file generated.
     * 
     * @return The package name of the Java source file generated.
     */
    String getPackagename();

    /**
     * Returns the {@link Writer} to which the output should be written.
     * 
     * @return The {@link Writer} to which all output must be generated.
     * @throws IOException
     *             If - for some reason - the object fails to create the
     *             {@link Writer}.
     */
    Writer getWriter() throws IOException;

}
