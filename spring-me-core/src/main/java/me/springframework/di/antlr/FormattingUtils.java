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
package me.springframework.di.antlr;

/**
 * A collection of operations to format Strings correctly.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class FormattingUtils {

    /**
     * Returns a camel case formatted representation of the String passed in.
     * 
     * @param in
     *            The String that needs to be reformatted.
     * @param startWithUpper
     *            A boolean indicating whether or not the String returned needs
     *            to start with an uppercase character.
     * @return The String formatted according to the settings passed in.
     */
    public static String toCamelCase(String in, boolean startWithUpper) {
        StringBuilder builder = new StringBuilder();
        int length = in.length();
        boolean nextUpper = startWithUpper;
        for (int i = 0; i < length; i++) {
            char c = in.charAt(i);
            if (c == '-' || c == '_'|| c == '#' || c == '.') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    builder.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    builder.append(c);
                }
            }
        }
        return builder.toString();
    }

    public static String toFieldName(String string) {
        if (isJavaIdentifier(string)) {
            return string;
        }
        return toCamelCase(string, false);
    }

    static boolean isJavaIdentifier(String s) {
        int length = s.length();
        if (length == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < length; i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an all-uppercase representation of the String passed in, with
     * dashes being replaced by underscores.
     * 
     * @param in
     *            The String to be reformatted.
     * @return The reformatted String.
     */
    public static String toUpperCase(String in) {
        StringBuilder builder = new StringBuilder();
        int length = in.length();
        for (int i = 0; i < length; i++) {
            char c = in.charAt(i);
            if (c == '-') {
                builder.append("_");
            } else {
                builder.append(Character.toUpperCase(c));
            }
        }
        return builder.toString();
    }

}
