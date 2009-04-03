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
package me.springframework.di.spring;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;
import me.springframework.di.Configuration;
import me.springframework.di.PropertySetter;
import me.springframework.di.Source;
import me.springframework.test.Paths;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;

public class QDoxAttributorTest extends TestCase {

    public void testAttribution() throws FileNotFoundException, IOException {
        Resource resource = new ClassPathResource("/component.xml",
                QDoxAttributorTest.class);
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(Paths.getFile("src/test/java"));
        QDoxAugmentation augmentation = new QDoxAugmentation(builder);
        SpringConfigurationLoader loader = new SpringConfigurationLoader(augmentation);
        Configuration configuration = loader.load(resource);
        assertEquals(3, configuration.getPublicInstances().size());
        assertNotNull(configuration.get("teacher1"));
        assertNotNull(configuration.get("teacher1").getSetters());
        assertEquals(2, configuration.get("teacher1").getSetters().size());
        for (PropertySetter setter : configuration.get("teacher1").getSetters()) {
            assertNotNull(setter.getName());
            if ("name".equals(setter.getName())) {
                assertEquals("java.lang.String", setter.getType());
                assertFalse(setter.isPrimitive());
            } else if ("age".equals(setter.getName())) {
                assertEquals("int", setter.getType());
                assertTrue(setter.isPrimitive());
            } else if ("courses".equals(setter.getName())) {
                assertEquals(Source.SourceType.List, setter
                        .getSource().getSourceType());
            } else {
                fail(); // Either name or age
            }
        }
    }

}
