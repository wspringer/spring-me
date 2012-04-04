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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import me.springframework.di.Configuration;
import me.springframework.di.ConstructorArgument;
import me.springframework.di.Instance;
import me.springframework.di.InstanceReference;
import me.springframework.di.LiteralSource;
import me.springframework.test.Paths;

import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class IndexedConstructorArgTest {

    @Test
    public void objectsCreatedByBeanFactoryImplAreCastToSinkType() {
        Resource resource = new ClassPathResource("/indexed-ctr-arg.xml", getClass());
        Configuration configuration = readConfiguration(resource);

        Instance three = configuration.get("three");
        assertEquals(2, three.getConstructorArguments().size());
        List<ConstructorArgument> args =
                new ArrayList<ConstructorArgument>(three.getConstructorArguments());
        LiteralSource arg1 = (LiteralSource) args.get(0).getSource();
        assertEquals("3.1", arg1.getValue());

        InstanceReference ref = (InstanceReference) args.get(1).getSource();
        assertEquals("mc", ref.getName());
        assertEquals("java.math.MathContext", ref.getType());
    }

    static Configuration readConfiguration(Resource resource) {
        return new ConfigurationBuilder()
            .addSourceTree(Paths.getFile("src/test/java"))
            .withBeanFactoryOf(resource)
            .withConversions()
            .build();
    }

    @Test
    public void validateIndexedConstructorArgsConfigWorksInSpring() {
        ClassPathResource xml = new ClassPathResource("/indexed-ctr-arg.xml");
        XmlBeanFactory beanFactory = new XmlBeanFactory(xml);
        BigDecimal three = (BigDecimal) beanFactory.getBean("three");
        assertEquals("3", three.toString());
    }

}
