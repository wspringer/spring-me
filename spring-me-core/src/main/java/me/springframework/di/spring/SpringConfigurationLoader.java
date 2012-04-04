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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.Scope;
import me.springframework.di.Sink;
import me.springframework.di.Source;
import me.springframework.di.base.AbstractSink;
import me.springframework.di.base.MutableCollectionSource;
import me.springframework.di.base.MutableConfiguration;
import me.springframework.di.base.MutableConstructorArgument;
import me.springframework.di.base.MutableContext;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.base.MutableInstanceReference;
import me.springframework.di.base.MutableListSource;
import me.springframework.di.base.MutableMapSource;
import me.springframework.di.base.MutableMapSource.MapSourceEntry;
import me.springframework.di.base.MutablePropertiesSource;
import me.springframework.di.base.MutablePropertySetter;
import me.springframework.di.base.MutableSetSource;
import me.springframework.di.base.MutableSource;
import me.springframework.di.base.MutableStringValueSource;
import me.springframework.di.gen.factory.BeanFactoryGenerator;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * A class capable of loading lists of {@link Instance Instances} from a Spring
 * application context. Note that the {@link Configuration} returned by
 * {@link #load(BeanDefinitionRegistry)} will still be fairly incomplete. It
 * relies on some other source to complete the metadata required to generate
 * source code using
 * {@link BeanFactoryGenerator#generate(me.springframework.di.gen.Destination, Configuration)}
 * .
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
     * The Spring context from which to load bean definitions.
     */
    private final ConfigurableListableBeanFactory factory;

    /**
     * The objects responsible for augmenting the model read from Spring
     * configuration.
     */
    private final Augmentation[] augmentations;

    /**
     * Constructs a new instance, accepting a number of objects to augment the
     * partial {@link Configuration} constructed from the Spring configuration
     * files.
     * 
     * @param augmentations
     *            Objects capable of augmenting the partial model constructed
     *            from the meta data in Spring configuration files.
     */
    public SpringConfigurationLoader(ConfigurableListableBeanFactory factory,
            Augmentation... augmentations) {
        this.factory = factory;
        this.augmentations = augmentations;
    }

    public Configuration load() {
        MutableContext context = loadBeans(factory);
        for (Augmentation augmentation : augmentations) {
            augmentation.augment(context);
        }
        return createConfiguration(context);
    }

    /**
     * Returns a {@link Map} of {@link MutableInstance MutableInstances},
     * indexed by name.
     * 
     * @param registry
     *            The {@link BeanDefinitionRegistry} holding the bean
     *            definitions.
     * @return A {@link Map} of {@link MutableInstance MutableInstances}
     *         representing the root beans defined by the
     *         {@link ListableBeanFactory}.
     */
    protected MutableContext loadBeans(ConfigurableListableBeanFactory factory) {
        MutableContext context = new MutableContext();
        for (String name : factory.getBeanDefinitionNames()) {
            for (String alias : factory.getAliases(name)) {
                context.addAlias(alias, name);
            }
        }

        for (String name : factory.getBeanDefinitionNames()) {
            BeanDefinition definition = factory.getBeanDefinition(name);
            if (!definition.isAbstract()) {
                BeanDefinition merged = factory.getMergedBeanDefinition(name);
                MutableInstance instance = new MutableInstance(name);
                load(instance, merged, context);
                context.addInstance(name, instance);
            }
        }
        return context;
    }

    /**
     * Loads a {@link MutableInstance} from one of the {@link BeanDefinition}s
     * provided by the {@link BeanDefinitionRegistry} passed in.
     * 
     * @param instance
     *            A {@link MutableInstance} to be populated.
     * @param definition
     *            A {@link BeanDefinition}, providing the meta data.
     */
    private void load(MutableInstance instance, BeanDefinition definition,
            MutableContext context) {
        instance.setReferencedType(definition.getBeanClassName());
        instance.setPrimitive(false);
        instance.setLazyInit(definition.isLazyInit());
        instance.setId("source" + counter++);
        instance.setFactoryMethod(definition.getFactoryMethodName());
        instance.setFactoryInstance(definition.getFactoryBeanName());
        if (ConfigurableBeanFactory.SCOPE_SINGLETON.equals(definition.getScope())) {
            instance.setScope(Scope.SINGLETON);
        }
        if (ConfigurableBeanFactory.SCOPE_PROTOTYPE.equals(definition.getScope())) {
            instance.setScope(Scope.PROTOTYPE);
        } 
        if (definition instanceof AbstractBeanDefinition) {
            instance.setInitMethod(((AbstractBeanDefinition) definition).getInitMethodName());
            instance.setDestroyMethod(((AbstractBeanDefinition) definition).getDestroyMethodName());
        }

        List<ValueHolder> ctrArgs = constructorArgumentValues(definition);
        if (!ctrArgs.isEmpty()) {
            List<MutableConstructorArgument> arguments = new ArrayList<MutableConstructorArgument>();
            for (ValueHolder holder : ctrArgs) {
                MutableConstructorArgument argument = new MutableConstructorArgument(instance);
                argument.setInstance(instance);
                argument.setSource(loadSource(context, argument, holder.getValue()));
                argument.setType(holder.getType());
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
            setter.setSource(loadSource(context, setter, value.getValue()));
            setters.add(setter);
        }

        instance.setSetters(setters);

        // added by woj
        instance.setAutowireCandidate(definition.isAutowireCandidate());
        if (definition instanceof AbstractBeanDefinition) {
            instance.setAutowireMode(((AbstractBeanDefinition) definition)
                    .getResolvedAutowireMode());
        } else {
            instance.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
        }
    }

    @SuppressWarnings("unchecked")
    private List<ValueHolder> constructorArgumentValues(BeanDefinition definition) {
        ConstructorArgumentValues ctrArgValues = definition.getConstructorArgumentValues();
        List<ValueHolder> ctrArgs = ctrArgValues.getGenericArgumentValues();
        if (ctrArgs.isEmpty()) {
            Map<?, ValueHolder> indexedValues = ctrArgValues.getIndexedArgumentValues();
            Map<?, ValueHolder> v = new TreeMap<Object, ValueHolder>(indexedValues);
            ctrArgs = new ArrayList<ValueHolder>(v.values());
        }
        return ctrArgs;
    }

    /**
     * Loads a {@link MutableSource} by examining the value of a Sink.
     * 
     * @param sink
     *            The {@link Sink} configured using a certain type of source.
     * @param value
     *            The Spring representation of that source.
     * @return A {@link MutableSource}, representing the source of the data to
     *         be injected in the {@link Sink}.
     */
    private MutableSource loadSource(MutableContext context, Sink sink, Object value) {
        MutableSource result = null;
        if (Types.isLiteral(value.getClass())) {
            return load(sink, String.valueOf(value));
        } else if (value instanceof RuntimeBeanReference) {
            result = load(sink, (RuntimeBeanReference) value, context);
        } else if (value instanceof TypedStringValue) {
            result = load(sink, (TypedStringValue) value);
        } else if (value instanceof BeanDefinitionHolder) {
            result = load(sink, (BeanDefinitionHolder) value, context);
        } else if (value instanceof ManagedSet) {
            result = load(sink, (ManagedSet) value, context);
        } else if (value instanceof ManagedList) {
            result = load(sink, (ManagedList) value, context);
        } else if (value instanceof ManagedMap) {
            result = load(context, sink, (ManagedMap) value);
        } else if (value instanceof ManagedProperties) {
            result = load(context, sink, (ManagedProperties) value);
        } else {
            System.err.println("No support for " + value.getClass().getName());
            return null;
        }
        result.setId("source" + (counter++));
        return result;
    }

    private MutableSource load(Sink sink, String s) {
        return new MutableStringValueSource(sink, s, "java.lang.String");
    }

    private MutableSource load(MutableContext context, Sink sink, ManagedProperties properties) {
        MutablePropertiesSource source = new MutablePropertiesSource(sink, properties);
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            MapSourceEntry entrySource = createEntry(context, source, entry);
            source.getEntries().add(entrySource);
        }
        return source;
    }

    private MutableSource load(MutableContext context, Sink sink, ManagedMap value) {
        MutableMapSource source = new MutableMapSource(sink);
        for (Object element : value.entrySet()) {
            Map.Entry<?, ?> entry = (Entry<?, ?>) element;
            MapSourceEntry created = createEntry(context, source, entry);
            source.getEntries().add(created);
        }
        return source;
    }

    private MapSourceEntry createEntry(MutableContext context,
            MutableSource source, Map.Entry<?, ?> entry) {
        MapSourceEntry created = new MapSourceEntry();
        Sink keySink = new EntrySink(EntrySink.Type.Key, source);
        Sink valueSink = new EntrySink(EntrySink.Type.Value, source);
        created.setKey(loadSource(context, keySink, entry.getKey()));
        created.setValue(loadSource(context, valueSink, entry.getValue()));
        return created;
    }

    /**
     * Returns a {@link MutableSource} from a source providing a set of values.
     *
     * @param sink
     *            The {@link Sink} that has the result of this object configured
     *            as its source.
     * @param set
     *            The Spring representation of a set of values.
     * @return The {@link Source} representation of the object producing that
     *         set of values.
     */
    private MutableSource load(Sink sink, ManagedSet set, MutableContext context) {
        MutableCollectionSource source = new MutableSetSource(sink);
        loadElements(source, set, context);
        return source;
    }

    /**
     * Returns a {@link MutableSource} from a source providing a list of values.
     * 
     * @param sink
     *            The {@link Sink} that has the result of this object configured
     *            as its source.
     * @param list
     *            The Spring representation of a list of values.
     * @return The {@link Source} representation of the object producing that
     *         list of values.
     */
    private MutableSource load(Sink sink, ManagedList list, MutableContext context) {
        MutableCollectionSource source = new MutableListSource(sink);
        loadElements(source, list, context);
        return source;
    }

    /**
     * Resolves sinks/sources for each element in a managed collection.
     * @param source The collection source (e.g. ManagedList/ManagedSet source).
     * @param values The values in the source collection.
     * @param context The bean context.
     */
    private void loadElements(MutableCollectionSource source,
            Collection<?> values, MutableContext context) {
        List<MutableSource> elements = new ArrayList<MutableSource>();
        int index = 0;
        for (Object object : values) {
            ElementSink elementSink = new ElementSink(index++, source);
            elements.add(loadSource(context, elementSink, object));
        }
        source.setElementSources(elements);
    }

    /**
     * Constructs a {@link MutableSource} from a source providing an anonymous
     * bean.
     * 
     * @param sink
     *            The {@link Sink} configured to receive the value produced by
     *            the source.
     * @param value
     *            The actually value constructed.
     * @return The {@link Source} representation of the object producing that
     *         anonymous bean.
     */
    private MutableSource load(Sink sink, BeanDefinitionHolder value, MutableContext context) {
        MutableInstance instance = new MutableInstance(sink, value.getBeanName());
        BeanDefinition merged = merged(value.getBeanDefinition());
        load(instance, merged, context);
        return instance;
    }

    /**
     * Returns a BeanDefinition for a nested bean, merged with its parent bean
     * if it is a child bean.
     * @param beanDefinition
     * @return
     */
    private BeanDefinition merged(BeanDefinition beanDefinition) {
        if (beanDefinition.getParentName() == null
                || beanDefinition instanceof RootBeanDefinition) {
            return beanDefinition;
        }

        Stack<BeanDefinition> definitions = new Stack<BeanDefinition>();
        BeanDefinition definition = beanDefinition;
        definitions.push(definition);
        while (definition.getParentName() != null) {
            definition = factory.getBeanDefinition(definition.getParentName());
            definitions.push(definition);
        }

        RootBeanDefinition merged = new RootBeanDefinition();
        for (BeanDefinition defn : definitions) {
            merged.overrideFrom(defn);
        }
        return merged;
    }

    /**
     * Constructs a {@link MutableSource} from a source based on a literal
     * representation of a value.
     * 
     * @param sink
     *            The {@link Sink} configured to receive the value produced by
     *            the source.
     * @param value
     *            Spring's representation of that value.
     * @return The {@link Source} representation of the object producing that
     *         value.
     */
    private MutableSource load(Sink sink, TypedStringValue value) {
        return new MutableStringValueSource(sink, value.getValue(), value.getTargetTypeName());
    }

    /**
     * Constructs a {@link MutableSource} from a source based on a reference to
     * a bean defined somewhere else.
     * 
     * @param sink
     *            The {@link Sink} configured to receive the value produced by
     *            the source.
     * @param value
     *            Spring's representation of the object producing the data to be
     *            injected.
     * @return The {@link Source} representation of the object producing that
     *         value.
     */
    private MutableSource load(Sink sink, RuntimeBeanReference value, MutableContext context) {
        String name = value.getBeanName();
        return new MutableInstanceReference(sink, name);
    }

    private static class EntrySink extends AbstractSink {

        private Type type;

        public enum Type {
            Key, Value;
        }

        public EntrySink(Type type, MutableSource source) {
            setSource(source);
            this.type = type;
        }

        public String getType() {
            return "java.lang.Object";
        }

        public String toString() {
            switch (type) {
                case Key:
                    return "the key of an entry of " + getSource();
                case Value:
                    return "the value of an entry of " + getSource();
                default:
                    return null; // Keep compiler happy
            }
        }

    }

    /**
     * An artificial {@link Sink} class, representing a certain element of a
     * list injected into field. Introduced later to have the ability to refer
     * that element directly, when generating a reference to a certain instance
     * defined in the configuration.
     * 
     * @author Wilfred Springer (wis)
     * 
     */
    private static class ElementSink extends AbstractSink {

        /**
         * The index of the element.
         */
        private int index;

        /**
         * Constructs a new instance.
         * 
         * @param index
         *            The index of the List element.
         * @param source
         *            The {@link Source} producing the data.
         */
        public ElementSink(int index, MutableSource source) {
            setSource(source);
            this.index = index;
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
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "the " + index + "th element of " + getSource();
        }

    }

    /**
     * Returns a {@link Configuration} from the instances passed in.
     * 
     * @param instances
     *            The root instances for which we need a {@link Configuration}.
     * @return The {@link Configuration} from the instances passed in.
     */
    protected static Configuration createConfiguration(MutableContext context) {
        Map<String, MutableInstance> instances = context.getInstances();
        return new MutableConfiguration(instances);
    }

}
