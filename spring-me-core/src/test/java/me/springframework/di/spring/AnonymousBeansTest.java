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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;

import me.springframework.di.Configuration;
import me.springframework.di.gen.factory.BeanFactoryGenerator;
import me.springframework.test.Paths;

import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.ParseException;

public class AnonymousBeansTest {

    @Test
    public void beanFactoryChecksForAnonymousNamesGeneratedBySpring() {
        Resource resource = new ClassPathResource("/anonymous.xml", getClass());
        Configuration configuration = readConfiguration(resource);
        InMemoryDestination dest = new InMemoryDestination("test");
        BeanFactoryGenerator.generate(dest, configuration, MINIMAL_JAVA_SE);

        try {
            JavaDocBuilder builder = new JavaDocBuilder();
            builder.addSource(new StringReader(dest.getAsText()));
            JavaClass factory = builder.getClassByName("test.BeanFactory");

            Type[] args = new Type[] {new Type(String.class.getName()) };
            JavaMethod getBean = factory.getMethodBySignature("getBean", args);
            String source = getBean.getSourceCode().replaceAll("[\r\n]", " ");

            XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
            String[] beanNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                assertTrue("getBean should check for " + beanName,
                        source.contains(String.format("\"%s\".equals", beanName)));
            }
        } catch (ParseException ex) {
            reportParseFailure(dest, ex);
        }
    }

    private static Configuration readConfiguration(Resource resource) {
        return new ConfigurationBuilder()
            .addSourceTree(Paths.getFile("src/test/java"))
            .withBeanFactoryOf(resource)
            .build();
    }

    static void reportParseFailure(InMemoryDestination dest, ParseException ex) {
        String message = "Invalid java at line " + ex.getLine()
                + ", column " + ex.getColumn() + ": ";
        String error = dest.getAsText().split("\n")[ex.getLine() - 1].trim();
        fail(message + error);
    }

}
