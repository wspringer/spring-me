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
import me.springframework.di.Configuration;
import me.springframework.di.ConstructorArgument;
import me.springframework.di.Instance;
import me.springframework.di.LiteralSource;
import me.springframework.di.Source;
import me.springframework.di.base.MutableConstructorArgument;
import me.springframework.di.base.MutableInstance;
import me.springframework.test.Paths;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Tests implicit conversion of string literals to Resource instances.
 */
public class ResourceTest {

    private Resource resource;

    @Before
    public void setUp() {
        this.resource = new ClassPathResource("/resources.xml", getClass());
    }

    /**
     * Tests that a sink of type {@link Resource} with a literal string value
     * will have a {@link ClassPathResource} for the resource named by the
     * string literal.
     */
    @Test
    public void stringLiteralSourceForResourceSinkIsImplicitlyConverted() {
        Configuration config = new ConfigurationBuilder()
            .addSourceTree(Paths.getFile("src/test/java"))
            .withBeanFactoryOf(resource)
            .withConversions()
            .build();

        Instance holder = config.get("resourceHolder");
        assertEquals(1, holder.getConstructorArguments().size());
        ConstructorArgument argument = holder.getConstructorArguments().get(0);
        assertEquals(Resource.class.getName(), argument.getType());

        Source source = argument.getSource();
        assertEquals(ClassPathResource.class.getName(), source.getType());
        MutableInstance resource = (MutableInstance) source;
        assertEquals(1, resource.getConstructorArguments().size());
        MutableConstructorArgument ctrArg = resource.getConstructorArguments().get(0);
        LiteralSource fileArg = (LiteralSource) ctrArg.getSource();
        assertEquals("set.xml", fileArg.getValue());
    }

    /**
     * Checks that Spring will inject a {@link ClassPathResource} for a sink of
     * type {@link Resource}.
     */
    @Test
    public void validateSpringInjectsClassPathResourceForStringLiteral() {
        XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
        ResourceHolder bean = (ResourceHolder) beanFactory.getBean("resourceHolder");
        Resource r = bean.getResource();
        assertTrue(r instanceof ClassPathResource);
        assertTrue(r.isReadable());
        assertEquals("set.xml", r.getFilename());
    }

    static class ResourceHolder {
        private final Resource resource;
        public ResourceHolder(Resource resource) {
            this.resource = resource;
        }
        public Resource getResource() {
            return resource;
        }
    }

}
