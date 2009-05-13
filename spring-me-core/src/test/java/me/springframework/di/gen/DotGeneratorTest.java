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
package me.springframework.di.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.base.MutableConfiguration;
import me.springframework.di.base.MutableConstructorArgument;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.base.MutablePropertySetter;
import me.springframework.di.gen.dot.DotGenerator;
import me.springframework.di.gen.dot.DotGenerator.StyleCatalog;

import junit.framework.TestCase;


public class DotGeneratorTest extends TestCase {

    private Configuration configuration;

    public void setUp() {
        // First instance
        MutableInstance instance1 = new MutableInstance("1");
        instance1.setType("com.tomtom.foobar.Foo");
        instance1.setId("foo");

        // Second instance
        MutableInstance instance2 = new MutableInstance("2");
        instance2.setType("com.tomtom.foobar.Bar");
        instance2.setId("bar");
        Set<MutablePropertySetter> setters = new HashSet<MutablePropertySetter>();
        MutablePropertySetter setter = new MutablePropertySetter(instance2);
        setter.setName("foo");
        setter.setType("com.tomtom.foobar.Foo");
        setter.setSource(instance1);
        setters.add(setter);
        instance2.setSetters(setters);

        MutableConstructorArgument arg = new MutableConstructorArgument(instance1);
        arg.setSource(instance1);
        instance2.setConstructorArguments(new ArrayList<MutableConstructorArgument>());
        instance2.getConstructorArguments().add(arg);

        Map<String,Instance> map = new HashMap<String, Instance>();
        map.put(instance2.getName(), instance2);
        configuration = new MutableConfiguration(map);
    }

    public void testConfiguration() {
        assertNull(configuration.get("1"));
        assertNotNull(configuration.get("2"));
        assertEquals(1, configuration.getPublicInstances().size());
        assertEquals(1, configuration.getInstanceSources().size());
    }
    
    public void testGeneration() {
        StyleCatalog catalog = new StyleCatalog() {

            public String getColor(String type) {
                if (type.endsWith("Bar")) {
                    return "red";
                } else {
                    return "white";
                }
            }

        };
        System.out.println(new DotGenerator(catalog).process(configuration));
    }

}
