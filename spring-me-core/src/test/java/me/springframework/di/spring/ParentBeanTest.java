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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.PropertySetter;
import me.springframework.di.gen.factory.BeanFactoryGenerator;
import me.springframework.test.Paths;

import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;

public class ParentBeanTest {

    @Test
    public void beanShouldInheritTypeOfParentBeanIfNotSpecified() {
        Resource resource = new ClassPathResource("/parent.xml", getClass());
        Configuration configuration = readConfiguration(resource);
        InMemoryDestination dest = new InMemoryDestination("test");
        BeanFactoryGenerator.generate(dest, configuration, MINIMAL_JAVA_SE);

        // Abstract bean definitions should not be included in Configuration
        assertNull(configuration.get("type-person-base"));
        assertNull(configuration.get("type-teacher-base"));

        Instance type = configuration.get("type-bob");
        assertEquals(Teacher.class.getName(), type.getType());

        // Abstract bean definitions should not be included in Configuration
        assertNull(configuration.get("prop-name-base"));
        assertNull(configuration.get("prop-age-base"));

        Instance prop = configuration.get("prop-bob");
        assertEquals(Person.class.getName(), prop.getType());
        List<PropertySetter> setters = new ArrayList<PropertySetter>(prop.getSetters());
        assertEquals(2, setters.size());
    }

    @Test
    public void validateParentBeansConfigWorksInSpring() {
        ClassPathResource res = new ClassPathResource("/parent.xml");
        XmlBeanFactory beanFactory = new XmlBeanFactory(res);

        Person typePerson = (Person) beanFactory.getBean("type-bob");
        assertEquals(Teacher.class, typePerson.getClass());
        Person propPerson = (Person) beanFactory.getBean("prop-bob");
        assertEquals("Bob", propPerson.getName());
        assertEquals(-1, propPerson.getAge());
        try {
            beanFactory.getBean("type-teacher-base");
            fail("Did not expect getBean to succeed on with name of abstract bean");
        } catch (Exception ex) {
            // Expected
        }
    }

    private static Configuration readConfiguration(Resource resource) {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(Paths.getFile("src/test/java"));
        Augmentation[] augmentations = {
                new QDoxAugmentation(builder),
                new AutowiringAugmentation(builder)
        };
        SpringConfigurationLoader loader = new SpringConfigurationLoader(augmentations);
        Configuration configuration = loader.load(resource);
        return configuration;
    }

}
