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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.LiteralSource;
import me.springframework.di.PropertySetter;
import me.springframework.di.Source;
import me.springframework.di.Source.SourceType;
import me.springframework.di.base.MutableSetSource;
import me.springframework.di.gen.factory.BeanFactoryGenerator;
import me.springframework.di.gen.factory.BeanFactoryTypes;
import me.springframework.test.Paths;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;

/**
 * Tests for managed sets.
 */
public class SetTest {

    @Test
    public void setValues() {
        Resource resource = new ClassPathResource("/set.xml", getClass());
        Configuration configuration = readConfiguration(resource);
        InMemoryDestination dest = new InMemoryDestination("test");
        BeanFactoryGenerator.generate(dest, configuration, BeanFactoryTypes.JAVA_SE);

        Instance bean = configuration.get("values");
        List<PropertySetter> setters =
                new ArrayList<PropertySetter>(bean.getSetters());
        assertEquals(1, setters.size());
        PropertySetter setter = setters.get(0);
        Source source = setter.getSource();
        assertEquals(SourceType.Set, source.getSourceType());

        MutableSetSource setSource = (MutableSetSource) source;
        List<Source> eltSources = new ArrayList<Source>(setSource.getElementSources());
        assertEquals(1, eltSources.size());
        LiteralSource elt1 = (LiteralSource) eltSources.get(0);
        assertEquals(String.valueOf(1), elt1.getValue());
    }

    @Test(expected=IllegalArgumentException.class)
    public void setsNotSupportedInJavaME() {
        Resource resource = new ClassPathResource("/set.xml", getClass());
        Configuration configuration = readConfiguration(resource);
        InMemoryDestination dest = new InMemoryDestination("test");
        BeanFactoryGenerator.generate(dest, configuration, BeanFactoryTypes.MINIMAL_JAVA_ME);
    }

    private static Configuration readConfiguration(Resource resource) {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(Paths.getFile("src/test/java"));
        Augmentation[] augmentations = {
                new QDoxAugmentation(builder),
                new AutowiringAugmentation(builder),
        };
        SpringConfigurationLoader loader = new SpringConfigurationLoader(augmentations);
        Configuration configuration = loader.load(resource);
        return configuration;
    }

    public static class TestClass {

        @SuppressWarnings("unused")
        private Set<?> values;

        public void setSet(Set<?> values) {
            this.values = values;
        }

    }

}
