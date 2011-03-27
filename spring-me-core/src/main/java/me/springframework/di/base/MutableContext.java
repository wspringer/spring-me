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
package me.springframework.di.base;

import java.util.HashMap;
import java.util.Map;

/**
 * The context in which a bean is defined, including other container-managed beans and
 * aliases.
 */
public class MutableContext {

    private final Map<String, MutableInstance> instances =
        new HashMap<String, MutableInstance>();

    private final Map<String, String> aliases =
        new HashMap<String, String>();

    /**
     * Adds a container-managed bean definition to this instance.
     *
     * @param name The name of the bean.
     * @param instance The instance.
     */
    public void addInstance(String name, MutableInstance instance) {
        instances.put(name, instance);
    }

    /**
     * Gets the {@link MutableInstance} for the bean with the given name or alias.
     *
     * @param name The name of the bean.
     * @return The instance for the named bean, or null if no bean with that name exists.
     */
    public MutableInstance getByName(String name) {
        MutableInstance result = instances.get(name);
        if (result == null) {
            String aliasFor = aliases.get(name);
            if (aliasFor != null) {
                result = instances.get(aliasFor);
            }
        }
        return result;
    }

    /**
     * Gets a map of all {@link MutableInstance} in this context, keyed by name.
     * @return A map of bean name to the bean's {@link MutableInstance}.
     */
    public Map<String, MutableInstance> getInstances() {
        return instances;
    }

    /**
     * Adds an alias for a bean.
     * @param from The alias to add.
     * @param to The name of the referent.
     */
    public void addAlias(String from, String to) {
        aliases.put(from, to);
    }

}
