/**
 * Copyright (C) 2008 Original authors
 * 
 * This file is part of Spring ME.
 * 
 * Spring ME is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
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
package me.springframework.test;

import java.io.File;

/**
 * A helper class, helping to get the right paths both in Eclipse and from the
 * Maven commandline.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class Paths {

    /**
     * Returns the basedir, regardless of the environment in which the tests are
     * run.
     * 
     * @return The basedir, regardless of the environment in which the tests are
     *         executed.
     */
    private static File getBasedir() {

        // Let's see if it's set. (Normally what Surefire will do.)
        String basedir = System.getProperty("basedir");

        if (basedir == null) {

            // So, Maven didn't set it. Let's assume Eclipse.
            basedir = System.getProperty("user.dir");
        }
        return new File(basedir);
    }

    /**
     * Returns the File object representing the location of the file identified
     * by the relative path passed in.
     * 
     * @param relativePath
     *            The relative path of the file for which we need a {@link File}
     *            object. (Relative compared to the basedir.)
     * @return The File object representing the location of the file identified
     *         by the relative path passed in.
     */
    public static File getFile(String relativePath) {
        return new File(getBasedir(), relativePath);
    }

    /**
     * Returns the absolute path representing the location of the file
     * identified by the relative path passed in.
     * 
     * @param relativePath
     *            The relative path of the file for which we need a {@link File}
     *            object. (Relative compared to the basedir.)
     * @return The absolute path representing the location of the file
     *         identified by the relative path passed in.
     */
    public static String getPath(String relativePath) {
        return getFile(relativePath).getAbsolutePath();
    }

}
