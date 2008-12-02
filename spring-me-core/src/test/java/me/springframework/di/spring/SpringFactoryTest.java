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

import java.util.Map;

import junit.framework.TestCase;

import me.springframework.di.Instance;
import me.springframework.di.InstanceReference;
import me.springframework.di.PropertySetter;
import me.springframework.di.Source;
import me.springframework.di.StringValueSource;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.spring.SpringContextLoader;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


public class SpringFactoryTest extends TestCase {

    public void testLoading() {
        Resource resource = new ClassPathResource("/context1.xml", SpringFactoryTest.class);
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        Map<String, MutableInstance> instances = SpringContextLoader.load(factory);
        assertEquals(3, instances.size());

        assertEquals("object1", instances.get("object1").getName());
        assertEquals("com.tomtom.spring.me.A", instances.get("object1").getReferencedType());
        assertEquals(2, instances.get("object1").getSetters().size());
        for (PropertySetter setter : instances.get("object1").getSetters()) {
            assertNotNull(setter.getName());
            if ("foo".equals(setter.getName())) {
                assertEquals(Source.SourceType.StringRepresentation, setter.getSource()
                        .getSourceType());
                assertEquals("wilfred", ((StringValueSource) setter.getSource()).getValue());
            } else if ("bar".equals(setter.getName())) {
                assertEquals(Source.SourceType.StringRepresentation, setter.getSource()
                        .getSourceType());
                assertEquals("springer", ((StringValueSource) setter.getSource()).getValue());
            } else {
                fail(); // Either foo or bar
            }
        }

        assertEquals("object2", instances.get("object2").getName());
        assertEquals("com.tomtom.spring.me.B", instances.get("object2").getReferencedType());
        for (PropertySetter setter : instances.get("object2").getSetters()) {
            assertNotNull(setter.getName());
            if ("a".equals(setter.getName())) {
                assertEquals(Source.SourceType.InstanceReference, setter.getSource()
                        .getSourceType());
                assertEquals("object1", ((InstanceReference) setter.getSource()).getName());
            } else {
                fail(); // Eiter a or nothing
            }
        }

        assertEquals("object3", instances.get("object3").getName());
        assertEquals("com.tomtom.spring.me.C", instances.get("object3").getReferencedType());
        for (PropertySetter setter : instances.get("object3").getSetters()) {
            assertNotNull(setter.getName());
            if ("d".equals(setter.getName())) {
                assertEquals(Source.SourceType.Instance, setter.getSource().getSourceType());
                Instance referenced = (Instance) setter.getSource();
                assertEquals("com.tomtom.spring.me.D", referenced.getReferencedType());
            } else {
                fail(); // Either d or nothing
            }
        }

    }

    /**
     * Some lames test for testing the {@link Object#toString()} operation.
     */
    public void testToString() {
        Resource resource = new ClassPathResource("/context1.xml", SpringFactoryTest.class);
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        Map<String, MutableInstance> instances = SpringContextLoader.load(factory);
        assertEquals(3, instances.size());
        assertEquals("bean object1", instances.get("object1").toString());
        PropertySetter setter = (PropertySetter) instances.get("object1").getSetters().toArray()[0];
        if ("foo".equals(setter.getName())) {
            assertEquals("the foo property of bean object1", setter.toString());
        } else {
            assertEquals("the bar property of bean object1", setter.toString());
        }
        if ("foo".equals(setter.getName())) {
            assertEquals("the value of the foo property of bean object1", setter.getSource()
                    .toString());
        } else {
            assertEquals("the value of the bar property of bean object1", setter.getSource()
                    .toString());
        }
    }

}
