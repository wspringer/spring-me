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

import static me.springframework.test.Paths.getFile;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.LiteralSource;
import me.springframework.di.MapSource;
import me.springframework.di.PropertySetter;
import me.springframework.di.Source;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


public class SpringConfigurationLoaderTest extends TestCase {

    public void testLoadMaps() {
        Resource resource = new FileSystemResource(getFile("src/test/resources/context3.xml"));
        SpringConfigurationLoader loader = new SpringConfigurationLoader();
        Configuration configuration = loader.load(resource);
        assertNotNull(configuration.getMapSources());
        assertNotNull(configuration.getPublicInstances());
        assertEquals(1, configuration.getMapSources().size());
        assertEquals(1, configuration.getPublicInstances().size());
        MapSource source = configuration.getMapSources().iterator().next();
        assertEquals(2, source.getEntries().size());

        // Check that the keys are of the proper type
        assertTrue(source.getEntries().get(0).getKey() instanceof LiteralSource);
        assertTrue(source.getEntries().get(1).getKey() instanceof LiteralSource);

        // Check that the values are of the proper type
        assertTrue(source.getEntries().get(0).getValue() instanceof Instance);
        assertTrue(source.getEntries().get(1).getValue() instanceof Instance);

        // Check that the values are set correctly
        assertEquals("coding", ((LiteralSource) source.getEntries().get(0).getKey()).getValue());
        assertEquals("modeling", ((LiteralSource) source.getEntries().get(1).getKey()).getValue());

        // Check that the bean properties are set correctly
        assertHasStringProperty(source.getEntries().get(0).getValue(), "topic", "C++");
        assertHasStringProperty(source.getEntries().get(1).getValue(), "topic", "UML");
    }

    /**
     * Set an integer property value on a bean definition, and check that the
     * corresponding Source has the correct value. 
     */
    public void testBeanWithNonStringProperties() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        RootBeanDefinition bd = new RootBeanDefinition(PropertyPlaceholderConfigurer.class);
        bd.getPropertyValues().addPropertyValue("order", 1);
        beanFactory.registerBeanDefinition("ppc", bd);
        SpringConfigurationLoader loader = new SpringConfigurationLoader();
        Configuration configuration = loader.load(beanFactory);

        Instance instance = configuration.get("ppc");
        List<PropertySetter> setters =
                new ArrayList<PropertySetter>(instance.getSetters());
        assertEquals(1, setters.size());
        PropertySetter setter = setters.get(0);
        assertEquals("order", setter.getName());
        LiteralSource source = (LiteralSource) setter.getSource();
        assertEquals("1", String.valueOf(source.getValue()));
    }

    private static void assertHasStringProperty(Source source, String name, String value) {
        assertEquals(Source.SourceType.Instance, source.getSourceType());
        Instance instance = (Instance) source;
        for (PropertySetter setter : instance.getSetters()) {
            if (name.equals(setter.getName())) {
                assertEquals(Source.SourceType.StringRepresentation, setter.getSource().getSourceType());
                assertEquals(value, ((LiteralSource) setter.getSource()).getValue());
                return;
            }
        }
        fail("No setter named " + name);
    }

}
