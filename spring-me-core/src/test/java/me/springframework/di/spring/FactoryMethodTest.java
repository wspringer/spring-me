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
import static org.junit.Assert.assertNull;
import me.springframework.di.Configuration;
import me.springframework.di.ConstructorArgument;
import me.springframework.di.Instance;
import me.springframework.di.base.MutableStringValueSource;
import me.springframework.test.Paths;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;

public class FactoryMethodTest {

    private static Configuration configuration;

    @BeforeClass
    public static void setUp() {
        Resource resource = new ClassPathResource("/factorymethod.xml", FactoryMethodTest.class);
        configuration = readConfiguration(resource);
    }

    @Test
    public void constructor() {
        Instance ctr = configuration.get("constructor");
        assertTypes(ctr, "ctr", "01");
        assertNull(ctr.getFactoryInstance());
        assertNull(ctr.getFactoryMethod());
    }

    @Test
    public void staticFactoryMethod() {
        Instance local = configuration.get("localFactoryMethod");
        assertTypes(local, "local", "02");
        assertNull(local.getFactoryInstance());
        assertEquals("newInstance", local.getFactoryMethod());
    }

    @Test
    public void staticFactoryMethodInAnotherClass() {
        Instance nonLocal = configuration.get("nonLocalFactoryMethod");
        assertTypes(nonLocal, "nonLocal", "03");
        assertNull(nonLocal.getFactoryInstance());
        assertEquals("create", nonLocal.getFactoryMethod());
    }

    @Test
    public void instanceFactoryMethodInAnotherClass() {
        Instance nonLocalFactoryBean = configuration.get("nonLocalFactoryBean");
        assertTypes(nonLocalFactoryBean, "nonLocalFactoryBean", "04");
        assertEquals("nonLocalFactoryBeanInstance", nonLocalFactoryBean.getFactoryInstance());
        assertEquals("createObject", nonLocalFactoryBean.getFactoryMethod());
    }

    @Test
    public void instanceFactoryMethodInSameClass() {
        // Unusual example: creating a method from an instance of the same type
        Instance localFactoryBean = configuration.get("localFactoryBean");
        assertTypes(localFactoryBean, "localFactoryBean", "05");
        assertEquals("localFactoryBeanInstance", localFactoryBean.getFactoryInstance());
        assertEquals("createAnother", localFactoryBean.getFactoryMethod());
    }

    private void assertTypes(Instance instance, String name, String value){
        assertEquals(2, instance.getConstructorArguments().size());
        ConstructorArgument ctrArg1 = instance.getConstructorArguments().get(0);
        ConstructorArgument ctrArg2 = instance.getConstructorArguments().get(1);
        assertEquals("java.lang.String", ctrArg1.getType());
        assertEquals(name, ((MutableStringValueSource) ctrArg1.getSource()).getValue());
        assertEquals("int", ctrArg2.getType());
        assertEquals(value, ((MutableStringValueSource) ctrArg2.getSource()).getValue());
    }

    static Configuration readConfiguration(Resource resource) {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(Paths.getFile("src/test/java"));
        Augmentation[] augmentations = {
                new QDoxAugmentation(builder),
                new AutowiringAugmentation(builder),
                new SinkAugmentation(),
        };
        SpringConfigurationLoader loader = new SpringConfigurationLoader(augmentations);
        Configuration configuration = loader.load(resource);
        return configuration;
    }

    public static class MyObjectFactory {
        public static MyObject create(String name, int value) {
            return new MyObject(name, value);
        }
    }

    public static class MyObjectFactoryBean {
        public MyObject createObject(String name, int value) {
            return new MyObject(name, value);
        }
    }

    public static class MyObject {

        public MyObject() {
        }

        public MyObject(String name, int value) {
        }

        public static MyObject newInstance(String name, int value) {
            return new MyObject(name, value);
        }

        public MyObject createAnother(String name, int value) {
            return new MyObject(name, value);
        }
    }

}
