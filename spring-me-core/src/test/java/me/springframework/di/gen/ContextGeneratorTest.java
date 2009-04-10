/**
 * Copyright (C) 2008 TomTom
 * 
 * This file is part of Spring ME.
 * 
 * Spring ME is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
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

import junit.framework.TestCase;
import me.springframework.di.Configuration;
import me.springframework.di.gen.factory.BeanFactoryGenerator;
import me.springframework.di.gen.factory.BeanFactoryTypes;
import me.springframework.di.gen.factory.Destination;
import me.springframework.di.spring.QDoxAugmentation;
import me.springframework.di.spring.SpringConfigurationLoader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;

public class ContextGeneratorTest extends TestCase {

    public void testIntegration() throws IOException {
        Resource resource = new ClassPathResource("/context3.xml", ContextGeneratorTest.class);
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(getBaseDir(), "src/test/java"));
        SpringConfigurationLoader loader = new SpringConfigurationLoader(new QDoxAugmentation(
                builder));
        Configuration configuration = loader.load(resource);
        InMemoryDestination dest = new InMemoryDestination();
        BeanFactoryGenerator generator = new BeanFactoryGenerator();
        generator.generate(dest, configuration, BeanFactoryTypes.MINIMAL_JAVA_SE);
        System.out.println(dest.getAsText());
    }

    public static class InMemoryDestination implements Destination {

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
