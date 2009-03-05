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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import me.springframework.di.Configuration;
import me.springframework.di.ConstructorArgument;
import me.springframework.di.PropertySetter;
import me.springframework.di.base.MutableInstanceReference;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.qdox.JavaDocBuilder;

public class AutowiringAttributorTest extends TestCase {

    public void testAttributionByName() throws FileNotFoundException, IOException {
        attributionPropertyTester("/autowiring-woj1.xml", 4);
    }

    public void testAttributionByType() throws FileNotFoundException, IOException {
        attributionPropertyTester("/autowiring-woj2.xml", 4);
    }

    public void testAttributionDefaultAutowire() throws FileNotFoundException, IOException {
        attributionPropertyTester("/autowiring-woj3.xml", 4);
    }

    public void testAttributionTwoTypesFail() throws FileNotFoundException, IOException {
        try {
            attributionPropertyTester("/autowiring-woj4.xml", 5);
            fail();
        } catch (final Exception e) {
        }
    }

    public void testAttributionNoCandidate() throws FileNotFoundException, IOException {
        attributionPropertyTester("/autowiring-woj5.xml", 5);
    }

    public void testAttributionConstructor() throws FileNotFoundException, IOException {
        attributionConstructorTester("/autowiring-woj6.xml", 4);
    }

    private void attributionPropertyTester(final String fileName, final int num) {
        final Resource resource = new ClassPathResource(fileName, AutowiringAttributorTest.class);
        final JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(getBaseDir(), "src/test/java"));
        final QDoxAugmentation augmentation = new QDoxAugmentation(builder);
        final AutowiringAugmentation augmentation2 = new AutowiringAugmentation(builder);
        final SpringConfigurationLoader loader = new SpringConfigurationLoader(augmentation,
                augmentation2);
        final Configuration configuration = loader.load(resource);

        assertEquals(num, configuration.getPublicInstances().size());
        assertNotNull(configuration.get("bean1"));
        assertNotNull(configuration.get("bean1").getSetters());
        assertEquals(2, configuration.get("bean1").getSetters().size());
        for (final PropertySetter setter : configuration.get("bean1").getSetters()) {
            assertNotNull(setter.getName());
            if ("bean2".equals(setter.getName())) {
                assertEquals("me.springframework.di.spring.Bean2", setter.getType());
                assertFalse(setter.isPrimitive());

                boolean checked = false;
                for (final PropertySetter setter2 : configuration.get("beanr").getSetters()) {
                    if ("bean2".equals(setter2.getName())) {
                        assertEquals(((MutableInstanceReference) setter2.getSource()).getName(),
                                ((MutableInstanceReference) setter.getSource()).getName());
                        assertEquals(((MutableInstanceReference) setter2.getSource())
                                .getReferencedId(), ((MutableInstanceReference) setter.getSource())
                                .getReferencedId());
                        checked = true;
                    }
                }
                assertTrue(checked);

            } else if ("bean3".equals(setter.getName())) {
                assertEquals("me.springframework.di.spring.Bean3", setter.getType());
                assertFalse(setter.isPrimitive());

                boolean checked = false;
                for (final PropertySetter setter2 : configuration.get("beanr").getSetters()) {
                    if ("bean3".equals(setter2.getName())) {
                        assertEquals(((MutableInstanceReference) setter2.getSource()).getName(),
                                ((MutableInstanceReference) setter.getSource()).getName());
                        assertEquals(((MutableInstanceReference) setter2.getSource())
                                .getReferencedId(), ((MutableInstanceReference) setter.getSource())
                                .getReferencedId());
                        checked = true;
                    }
                }
                assertTrue(checked);

            } else {
                fail(); // No other properties
            }
        }
        // printout
        //        final InMemoryDestination dest = new InMemoryDestination();
        //        final BeanFactoryGenerator generator = new BeanFactoryGenerator();
        //        generator.generate(dest, configuration, BeanFactoryTypes.MinimalJavaSE);
        //        System.out.println(dest.getAsText());
    }

    private void attributionConstructorTester(final String fileName, final int num) {
        final Resource resource = new ClassPathResource(fileName, AutowiringAttributorTest.class);
        final JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(getBaseDir(), "src/test/java"));
        final QDoxAugmentation augmentation = new QDoxAugmentation(builder);
        final AutowiringAugmentation augmentation2 = new AutowiringAugmentation(builder);
        final SpringConfigurationLoader loader = new SpringConfigurationLoader(augmentation,
                augmentation2);
        final Configuration configuration = loader.load(resource);

        assertEquals(num, configuration.getPublicInstances().size());
        assertNotNull(configuration.get("bean1"));
        assertNotNull(configuration.get("bean1").getConstructorArguments());
        assertEquals(4, configuration.get("bean1").getConstructorArguments().size());
        assertEquals("me.springframework.di.spring.Bean2", configuration.get("bean1")
                .getConstructorArguments().get(2).getType());
        assertEquals("me.springframework.di.spring.Bean3", configuration.get("bean1")
                .getConstructorArguments().get(3).getType());
        // check all types match
        //        for (int i = 0; i < 4; i++) {
        //            assertEquals(configuration.get("beanr").getConstructorArguments().get(i).getType(),
        //                    configuration.get("bean1").getConstructorArguments().get(i).getType());
        //        }
        // check that arguments refer to the same object
        final List<? extends ConstructorArgument> cr = configuration.get("beanr")
                .getConstructorArguments();
        final List<? extends ConstructorArgument> c1 = configuration.get("beanr")
                .getConstructorArguments();

        for (int i = 2; i < 4; i++) {
            assertEquals(((MutableInstanceReference) cr.get(i).getSource()).getName(),
                    ((MutableInstanceReference) c1.get(i).getSource()).getName());
            assertEquals(((MutableInstanceReference) cr.get(i).getSource()).getReferencedId(),
                    ((MutableInstanceReference) c1.get(i).getSource()).getReferencedId());
        }
        // printout
        //        final InMemoryDestination dest = new InMemoryDestination();
        //        final BeanFactoryGenerator generator = new BeanFactoryGenerator();
        //        generator.generate(dest, configuration, BeanFactoryTypes.MinimalJavaSE);
        //        System.out.println(dest.getAsText());
    }

    private File getBaseDir() {
        String basedir = System.getProperty("basedir");
        if (basedir == null) {
            basedir = System.getProperty("user.dir");
        }
        return new File(basedir);
    }

}
