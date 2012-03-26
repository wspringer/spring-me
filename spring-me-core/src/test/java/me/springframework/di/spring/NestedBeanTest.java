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
import java.util.Collection;
import java.util.List;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.LiteralSource;
import me.springframework.di.PropertySetter;
import me.springframework.di.Source;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.gen.factory.BeanFactoryGenerator;
import me.springframework.di.gen.factory.BeanFactoryTypes;
import me.springframework.test.Paths;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class NestedBeanTest {

    @Test
    public void nestedBeansInheritPropertiesFromParent() {
        Resource resource = new ClassPathResource("/nestedbean.xml", getClass());
        Configuration configuration = readConfiguration(resource);
        InMemoryDestination dest = new InMemoryDestination("test");
        BeanFactoryGenerator.generate(dest, configuration, BeanFactoryTypes.JAVA_SE);

        Instance holder = configuration.get("holder");
        List<PropertySetter> holderSetters =
                new ArrayList<PropertySetter>(holder.getSetters());
        assertEquals(1, holderSetters.size());
        PropertySetter beanSetter = holderSetters.get(0);
        MutableInstance source = (MutableInstance) beanSetter.getSource();

        List<PropertySetter> beanSetters =
                new ArrayList<PropertySetter>(source.getSetters());
        assertEquals(2, beanSetters.size());
        LiteralSource name = (LiteralSource) sourceOf(beanSetters, "name");
        LiteralSource number = (LiteralSource) sourceOf(beanSetters, "number");
        assertEquals("Martin", name.getValue());
        assertEquals(String.valueOf(1), number.getValue());
    }

    /**
     * Returns the source of the PropertySetter with the given name.
     */
    private Source sourceOf(Collection<PropertySetter> setters, String name) {
        for (PropertySetter setter : setters) {
            if (name.equals(setter.getName())) {
                return setter.getSource();
            }
        }
        return null;
    }

    private static Configuration readConfiguration(Resource resource) {
        return new ConfigurationBuilder()
            .addSourceTree(Paths.getFile("src/test/java"))
            .withBeanFactoryOf(resource)
            .build();
    }

    @SuppressWarnings("unused")
    public static class Bean {

        private int number;

        private String name;

        public void setNumber(int number) {
            this.number = number;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @SuppressWarnings("unused")
    public static class Holder {

        private Bean bean;

        public void setBean(Bean bean) {
            this.bean = bean;
        }

    }

}
