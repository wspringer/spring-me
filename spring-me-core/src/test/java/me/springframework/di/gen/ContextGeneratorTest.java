/**
 * Copyright (C) 2008 TomTom
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Spring ME; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package me.springframework.di.gen;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import junit.framework.TestCase;

import me.springframework.di.base.MutableInstance;
import me.springframework.di.gen.ContextGenerator;
import me.springframework.di.gen.Destination;
import me.springframework.di.spring.ConfigurationProducer;
import me.springframework.di.spring.QDoxAttributor;
import me.springframework.di.spring.SpringContextLoader;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;

public class ContextGeneratorTest extends TestCase {

    public void testIntegration() throws IOException {
        Resource resource = new ClassPathResource("/component.xml",
                ContextGeneratorTest.class);
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        Map<String, MutableInstance> instances = SpringContextLoader
                .load(factory);
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(getBaseDir(), "src/test/java"));
        QDoxAttributor attributor = new QDoxAttributor(builder);
        attributor.attribute(instances);
        InMemoryDestination dest = new InMemoryDestination();
        ContextGenerator.generate(dest, ConfigurationProducer.produce(instances));
        System.out.println(dest.getAsText());
    }

    private static class InMemoryDestination implements Destination {

        private StringWriter writer = new StringWriter();

        public String getClassname() {
            return "BeanFactory";
        }

        public String getPackagename() {
            return "com.tomtom.test";
        }

        public Writer getWriter() throws IOException {
            return writer;
        }

        public String getAsText() {
            return writer.toString();
        }

    }

    private File getBaseDir() {
        String basedir = System.getProperty("basedir");
        if (basedir == null) {
            basedir = System.getProperty("user.dir");
        }
        return new File(basedir);
    }

}
