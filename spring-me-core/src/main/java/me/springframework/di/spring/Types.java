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
package me.springframework.di.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thoughtworks.qdox.model.Type;

/**
 * Provides utility methods for identifying whether values of a type can be
 * expressed as literal values. 
 */
class Types {

    /**
     * List of (non-primitive) classes for which there are literal values.
     */
    private static final List<Class<?>> LITERALS = Arrays.<Class<?>>asList(
            Boolean.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Double.class,
            Float.class,
            String.class);

    /**
     * Set of (non-primitive) classes for which there are literal values.
     */
    private static final Set<Class<?>> WRAPPERS_TYPES =
            new HashSet<Class<?>>(LITERALS);

    /**
     * Set of (non-primitive) fully qualified class names for classes with
     * literal values. 
     */
    private static final Set<String> WRAPPER_NAMES = classNames(LITERALS);

    /**
     * Returns true if values of the specified type can be expressed as a
     * literal value in Spring XML.
     */
    static boolean isLiteral(Type type) {
        if (type.isPrimitive()) {
            return true;
        }
        if (String.class.getName().equals(type)) {
            return true;
        }
        return WRAPPER_NAMES.contains(type.getValue());
    }


    /**
     * Returns true if instances of the specified class can be expressed as a
     * literal value in Spring XML.
     */
    static boolean isLiteral(Class<?> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        return WRAPPERS_TYPES.contains(type);
    }

    /**
     * Returns a set of fully qualified class names for the given classes.
     */
    private static Set<String> classNames(Iterable<Class<?>> classes) {
        List<String> list = new ArrayList<String>();
        for (Class<?> clazz : classes) {
            list.add(clazz.getName());
        }
        return new HashSet<String>(list);
    }

}
