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
package me.springframework.di.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.Sink;
import me.springframework.di.Source;
import me.springframework.di.base.MutableConfiguration;
import me.springframework.di.base.MutableConstructorArgument;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.base.MutableInstanceReference;
import me.springframework.di.base.MutableListSource;
import me.springframework.di.base.MutableMapSource;
import me.springframework.di.base.MutablePropertySetter;
import me.springframework.di.base.MutableSource;
import me.springframework.di.base.MutableStringValueSource;
import me.springframework.di.gen.BeanFactoryGenerator;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.Resource;


/**
 * A class capable of loading lists of {@link Instance Instances} from a Spring application context.
 * Note that the {@link Configuration} returned by {@link #load(BeanDefinitionRegistry)} will still
 * be fairly incomplete. It relies on some other source to complete the metadata required to
 * generate source code using
 * {@link BeanFactoryGenerator#generate(me.springframework.di.gen.Destination, Configuration)} .
 * 
 * @author Wilfred Springer
 * 
 */
public class SpringConfigurationLoader {

    /**
     * Something for providing artificial ids.
     */
    private static int counter = 0;

    /**
     * The objects responsible for augmenting the model read from Spring configuration.
     */
    private Augmentation[] augmentations;

    /**
     * Constructs a new instance, accepting a number of objects to augment the partial
     * {@link Configuration} constructed from the Spring configuration files.
     * 
     * @param augmentations Objects capable of augmenting the partial model constructed from the
     *            meta data in Spring configuration files.
     */
    public SpringConfigurationLoader(Augmentation... augmentations) {
        this.augmentations = augmentations;
    }

    /**
     * Loads a Configuration from a Spring XML based application context.
     * 
     * @param resource The Spring configuration file defining the beans.
     * @return A {@link Configuration} representing the graph of wired objects.
     */
    public Configuration load(Resource resource) {
        BeanDefinitionRegistry registry = new XmlBeanFactory(resource);
        Map<String, MutableInstance> instances = load(registry);
        for (Augmentation augmentation : augmentations) {
            augmentation.augment(instances);
        }
        return createConfiguration(instances);
    }

    /**
     * Returns a {@link Map} of {@link MutableInstance MutableInstances}, indexed by name.
     * 
     * @param registry The {@link BeanDefinitionRegistry} holding the bean definitions.
     * @return A {@link Map} of {@link MutableInstance MutableInstances} representing the root beans
     *         defined by the {@link ListableBeanFactory}.
     */
    private static Map<String, MutableInstance> load(BeanDefinitionRegistry registry) {
        Map<String, MutableInstance> instances = new HashMap<String, MutableInstance>();
        for (String name : registry.getBeanDefinitionNames()) {
            BeanDefinition definition = registry.getBeanDefinition(name);
            MutableInstance instance = new MutableInstance(name);
            load(instance, definition);
            instances.put(name, instance);
        }
        return instances;
    }

    /**
     * Loads a {@link MutableInstance} from one of the {@link BeanDefinition}s provided by the
     * {@link BeanDefinitionRegistry} passed in.
     * 
     * @param instance A {@link MutableInstance} to be populated.
     * @param definition A {@link BeanDefinition}, providing the meta data.
     */
    private static void load(MutableInstance instance, BeanDefinition definition) {
        instance.setReferencedType(definition.getBeanClassName());
        instance.setPrimitive(false);
        instance.setLazyInit(definition.isLazyInit());
        instance.setId("source" + counter++);
        instance.setFactoryMethod(definition.getFactoryMethodName());
        instance.setFactoryInstance(definition.getFactoryBeanName());
        if (definition instanceof AbstractBeanDefinition) {
            instance.setInitMethod(((AbstractBeanDefinition) definition).getInitMethodName());
            instance.setDestroyMethod(((AbstractBeanDefinition) definition).getDestroyMethodName());
        }
        if (!definition.getConstructorArgumentValues().isEmpty()) {
            List<MutableConstructorArgument> arguments = new ArrayList<MutableConstructorArgument>();
            for (Object object : definition.getConstructorArgumentValues().getGenericArgumentValues()) {
                MutableConstructorArgument argument = new MutableConstructorArgument(instance);
                argument.setInstance(instance);
                ValueHolder holder = (ValueHolder) object;
                argument.setSource(loadSource(argument, holder.getValue()));
                arguments.add(argument);
            }
            instance.setConstructorArguments(arguments);
        }
        Set<MutablePropertySetter> setters = new HashSet<MutablePropertySetter>();
        for (Object object : definition.getPropertyValues().getPropertyValueList()) {
            MutablePropertySetter setter = new MutablePropertySetter(instance);
            setter.setInstance(instance);
            PropertyValue value = (PropertyValue) object;
            setter.setName(value.getName());
            setter.setSource(loadSource(setter, value.getValue()));
            setters.add(setter);
        }
        instance.setSetters(setters);
    }

    /**
     * Loads a {@link MutableSource} by examining the value of a Sink.
     * 
     * @param sink The {@link Sink} configured using a certain type of source.
     * @param value The Spring representation of that source.
     * @return A {@link MutableSource}, representing the source of the data to be injected in the
     *         {@link Sink}.
     */
    private static MutableSource loadSource(Sink sink, Object value) {
        MutableSource result = null;
        if (value instanceof RuntimeBeanReference) {
            result = load(sink, (RuntimeBeanReference) value);
        } else if (value instanceof TypedStringValue) {
            result = load(sink, (TypedStringValue) value);
        } else if (value instanceof BeanDefinitionHolder) {
            result = load(sink, (BeanDefinitionHolder) value);
        } else if (value instanceof ManagedList) {
            result = load(sink, (ManagedList) value);
        } else if (value instanceof ManagedMap) {
            result = load(sink, (ManagedMap) value);
        } else {
            System.err.println("No support for " + value.getClass().getName());
            return null;
        }
        result.setId("source" + (counter++));
        return result;
    }

    private static MutableSource load(Sink sink, ManagedMap value) {
        MutableMapSource source = new MutableMapSource(sink);
        for (Object element : value.entrySet()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) element;
            MutableMapSource.MapSourceEntry created = new MutableMapSource.MapSourceEntry();
            Sink keySink = new EntrySink(EntrySink.Type.Key, source);
            Sink valueSink = new EntrySink(EntrySink.Type.Value, source);
            created.setKey(loadSource(keySink, entry.getKey()));
            created.setValue(loadSource(valueSink, entry.getValue()));
            source.getEntries().add(created);
        }
        return source;
    }

    /**
     * Returns a {@link MutableSource} from a source providing a list of values.
     * 
     * @param sink The {@link Sink} that has the result of this object configured as its source.
     * @param list The Spring representation of a list of values.
     * @return The {@link Source} representation of the object producing that list of values.
     */
    private static MutableSource load(Sink sink, ManagedList list) {
        ArrayList<MutableSource> elements = new ArrayList<MutableSource>();
        MutableListSource source = new MutableListSource(sink, elements);
        int index = 0;
        for (Object object : list) {
            elements.add(loadSource(new ElementSink(index++, source), object));
        }
        return source;
    }

    /**
     * Constructs a {@link MutableSource} from a source providing an anonymous bean.
     * 
     * @param sink The {@link Sink} configured to receive the value produced by the source.
     * @param value The actually value constructed.
     * @return The {@link Source} representation of the object producing that anonymous bean.
     */
    private static MutableSource load(Sink sink, BeanDefinitionHolder value) {
        MutableInstance instance = new MutableInstance(sink, value.getBeanName());
        load(instance, value.getBeanDefinition());
        return instance;
    }

    /**
     * Constructs a {@link MutableSource} from a source based on a literal representation of a
     * value.
     * 
     * @param sink The {@link Sink} configured to receive the value produced by the source.
     * @param value Spring's representation of that value.
     * @return The {@link Source} representation of the object producing that value.
     */
    private static MutableSource load(Sink sink, TypedStringValue value) {
        return new MutableStringValueSource(sink, value.getValue());
    }

    /**
     * Constructs a {@link MutableSource} from a source based on a reference to a bean defined
     * somewhere else.
     * 
     * @param sink The {@link Sink} configured to receive the value produced by the source.
     * @param value Spring's representation of the object producing the data to be injected.
     * @return The {@link Source} representation of the object producing that value.
     */
    private static MutableSource load(Sink sink, RuntimeBeanReference value) {
        return new MutableInstanceReference(sink, value.getBeanName());
    }

    private static class EntrySink implements Sink {

        private MutableSource source;

        private Type type;

        public enum Type {
            Key, Value;
        }

        public EntrySink(Type type, MutableSource source) {
            this.type = type;
            this.source = source;
        }

        public Instance getInstance() {
            return null;
        }

        public Source getSource() {
            return source;
        }

        public String getType() {
            return "java.lang.Object";
        }

        public boolean isPrimitive() {
            return false;
        }

        public String toString() {
            switch (type) {
            case Key:
                return "the key of an entry of " + source.toString();
            case Value:
                return "the value of an entry of " + source.toString();
            default:
                return null; // Keep compiler happy
            }
        }

    }

    /**
     * An artificial {@link Sink} class, representing a certain element of a list injected into
     * field. Introduced later to have the ability to refer that element directly, when generating a
     * reference to a certain instance defined in the configuration.
     * 
     * @author Wilfred Springer (wis)
     * 
     */
    private static class ElementSink implements Sink {

        /**
         * The index of the element.
         */
        private int index;

        /**
         * The {@link Source} from which it will obtain its data.
         */
        private MutableSource source;

        /**
         * Constructs a new instance.
         * 
         * @param index The index of the List element.
         * @param source The {@link Source} producing the data.
         */
        public ElementSink(int index, MutableSource source) {
            this.index = index;
            this.source = source;
        }

        /*
         * (non-Javadoc)
         * 
         * @see me.springframework.di.Sink#getInstance()
         */
        public Instance getInstance() {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see me.springframework.di.Sink#getSource()
         */
        public Source getSource() {
            return source;
        }

        /*
         * (non-Javadoc)
         * 
         * @see me.springframework.di.Typed#getType()
         */
        public String getType() {
            return "java.lang.Object";
        }

        /*
         * (non-Javadoc)
         * 
         * @see me.springframework.di.Typed#isPrimitive()
         */
        public boolean isPrimitive() {
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "the " + index + "th element of " + source.toString();
        }

    }

    /**
     * Returns a {@link Configuration} from the instances passed in.
     * 
     * @param instances The root instances for which we need a {@link Configuration}.
     * @return The {@link Configuration} from the instances passed in.
     */
    private static Configuration createConfiguration(Map<String, ? extends Instance> instances) {
        return new MutableConfiguration(instances);
    }

}
