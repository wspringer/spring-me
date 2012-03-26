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

import static me.springframework.di.gen.factory.BeanFactoryTypes.MINIMAL_JAVA_SE;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.LiteralSource;
import me.springframework.di.PropertySetter;
import me.springframework.di.gen.factory.BeanFactoryGenerator;
import me.springframework.test.Paths;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class PropertyValuesTest {

    @Test
    public void beanShouldInheritTypeOfParentBeanIfNotSpecified() {
        Resource resource = new ClassPathResource("/properties.xml", getClass());
        Configuration configuration = readConfiguration(resource);
        InMemoryDestination dest = new InMemoryDestination("test");
        BeanFactoryGenerator.generate(dest, configuration, MINIMAL_JAVA_SE);

        Instance person = configuration.get("person");
        assertEquals(Teacher.class.getName(), person.getReferencedType());
        Set<? extends PropertySetter> props = person.getSetters();
        assertEquals(1, props.size());
        PropertySetter setter = props.iterator().next();
        LiteralSource source = (LiteralSource) setter.getSource();
        assertEquals("Bob", source.getValue());
    }

    @Test
    public void validateAnonymousBeansConfigWorksInSpring() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/properties.xml");
        Person person = (Person) context.getBean("person");
        assertEquals("Bob", person.getName());
    }

    private static Configuration readConfiguration(Resource resource) {
        ConfigurableApplicationContext ctxt = new ClassPathXmlApplicationContext(resource.getFilename());
        return new ConfigurationBuilder()
            .addSourceTree(Paths.getFile("src/test/java"))
            .withContext(ctxt)
            .build();
    }

}
