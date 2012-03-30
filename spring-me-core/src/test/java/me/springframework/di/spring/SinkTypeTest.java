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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import me.springframework.di.Configuration;
import me.springframework.di.ConstructorArgument;
import me.springframework.di.Instance;
import me.springframework.di.PropertySetter;
import me.springframework.test.Paths;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class SinkTypeTest {

    @Test
    public void typesOfSinksThatReferenceOtherBeansShouldBeKnown() {
        Resource resource = new ClassPathResource("/sinktypes.xml", getClass());
        Configuration configuration = readConfiguration(resource);

        Instance course = configuration.get("x");
        List<? extends ConstructorArgument> ctrArgs = course.getConstructorArguments();
        assertEquals(1, ctrArgs.size());
        assertEquals("java.util.List", ctrArgs.get(0).getType());
    }

    private static Configuration readConfiguration(Resource resource) {
        ConfigurableApplicationContext ctxt =
                new ClassPathXmlApplicationContext(resource.getFilename());
        return new ConfigurationBuilder()
            .addSourceTree(Paths.getFile("src/test/java"))
            .withContext(ctxt)
            .build();
    }

    public static class TestBean {
        public TestBean(List<Number> x) {
        }
    }


    @Test
    public void testArrayPropertyHasCorrectType() throws Exception {
        Resource resource = new ClassPathResource("/array.xml", getClass());
        Configuration configuration = new ConfigurationBuilder()
            .addSourceTree(Paths.getFile("src/test/java"))
            .withBeanFactoryOf(resource)
            .withConversions()
            .build();

        Instance stringTest = configuration.get("string-test");
        assertEquals(ArrayHolder.class.getName(), stringTest.getType());
        List<PropertySetter> setters =
                new ArrayList<PropertySetter>(stringTest.getSetters());
        assertEquals(1, setters.size());
        PropertySetter strings = setters.get(0);
        assertEquals("java.lang.String", strings.getType());
        assertTrue(strings.isArray());
    }

    static class ArrayHolder {
        void setStrings(String[] array) {
        }
    }

}
