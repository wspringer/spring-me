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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import me.springframework.di.MapSource;
import me.springframework.di.Source;
import me.springframework.di.base.MutableConstructorArgument;
import me.springframework.di.base.MutableContext;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.base.MutableInstanceReference;
import me.springframework.di.base.MutableListSource;
import me.springframework.di.base.MutableMapSource;
import me.springframework.di.base.MutablePropertySetter;

import org.springframework.beans.factory.support.AbstractBeanDefinition;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * 
 * @author Wojciech Mlynarczyk (wmlynar@gmail.com)
 * 
 */
public class AutowiringAugmentation implements Augmentation {

    /**
     * Something for providing artificial ids.
     */
    private static int counter = 0;

    /**
     * The object providing access to the sources.
     */
    private final JavaDocBuilder builder;

    /**
     * Constructs a new instance, accepting the {@link JavaDocBuilder} providing
     * access to the class definitions.
     * 
     * @param builder
     *            The object providing access to the metadata we need.
     */
    public AutowiringAugmentation(final JavaDocBuilder builder) {
        this.builder = builder;
    }

    /**
     * Autowires {@link MutableInstance}s in the given {@link MutableContext}.
     * (Just the {@link MutableInstance}s representing the top-level (root) beans.
     *
     * @param context The context containing beans to be autowired.
     */
    public void augment(MutableContext context) {
        Map<String, MutableInstance> allInstances = context.getInstances();
        for (final MutableInstance instance : allInstances.values()) {
            // autowire all children instances
            for (final MutablePropertySetter setter : instance.getSetters()) {
                attribute(setter.getSource(), context);
            }
            // autowire instances in constructor
            if (instance.getConstructorArguments() != null) {
                for (final MutableConstructorArgument argument : instance.getConstructorArguments()) {
                    attribute(argument.getSource(), context);
                }
            }
            // autowire the instance itself
            attribute(instance, context);
        }
    }

    private void attribute(final MutableInstance instance, final MutableContext context) {
        if (instance.getAutowireMode() == AbstractBeanDefinition.AUTOWIRE_BY_NAME
                || instance.getAutowireMode() == AbstractBeanDefinition.AUTOWIRE_BY_TYPE) {
            attributeProperties(instance, context);
        } else if (instance.getAutowireMode() == AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR) {
            attributeConstructor(instance, context);
        }
    }

    /**
     * Autowires {@link MutableInstance} passed in, accepting
     * <code>context</code> for resolving references.
     */
    private void attributeProperties(final MutableInstance instance,
            final MutableContext context) {
        final JavaClass cl = builder.getClassByName(instance.getType());
        for (final BeanProperty beanProperty : cl.getBeanProperties()) {
            // don't do autowiring by type for primitive types
            if (beanProperty.getType().isPrimitive()) {
                continue;
            }

            // check if property not set already
            if (hasSetter(instance, beanProperty.getName())) {
                continue;
            }
            final MutableInstance newInstance;
            if (instance.getAutowireMode() == AbstractBeanDefinition.AUTOWIRE_BY_NAME) {
                // try to find an instance by name
                newInstance = context.getByName(beanProperty.getName());
                if (newInstance == null || !newInstance.isAutowireCandidate()) {
                    continue;
                }
            } else {
                // try to find an instance by type
                newInstance = findInstanceByType(context, instance.getName(), beanProperty
                        .getName(), beanProperty.getType().getValue());
                if (newInstance == null || !newInstance.isAutowireCandidate()) {
                    continue;
                }
            }

            // add new setter
            final MutablePropertySetter newSetter = new MutablePropertySetter(instance);
            final MutableInstanceReference ref = new MutableInstanceReference(newSetter,
                    newInstance.getName());
            ref.setReferencedId(newInstance.getId());
            ref.setId("source_autowire" + counter++);
            newSetter.setName(beanProperty.getName());
            newSetter.setPrimitive(false);
            newSetter.setSource(ref);
            newSetter.setType(newInstance.getType());
            newSetter.setInstance(instance);
            instance.getSetters().add(newSetter);
        }

    }

    private MutableInstance findInstanceByType(final MutableContext context,
            final String beanName, final String propertyName, final String type) {
        MutableInstance ret = null;
        for (final MutableInstance instance : context.getInstances().values()) {
            if (instance.isAutowireCandidate() && type.equals(instance.getType())) {
                if (ret != null) {
                    throw new RuntimeException("Failed autowiring by type, bean " + beanName
                            + ", multiple matches for property " + propertyName);
                }
                ret = instance;
            }
        }
        return ret;
    }

    private boolean hasSetter(final MutableInstance instance, final String setterName) {
        for (final MutablePropertySetter setter : instance.getSetters()) {
            if (setter.getName().equals(setterName)) {
                return true;
            }
        }
        return false;
    }

    private void attributeConstructor(final MutableInstance instance, final MutableContext context) {

        final JavaClass cl = builder.getClassByName(instance.getType());
        final List<JavaMethod> matchingConstructors = findMatchingConstructors(cl, instance,
                context);
        sortConstructors(matchingConstructors);

        final List<MutableConstructorArgument> arguments = new ArrayList<MutableConstructorArgument>();

        constructorsearch: for (final JavaMethod constructor : matchingConstructors) {
            // starting from the best constructor try to match

            arguments.clear();

            int i = 0;
            int numArgs = instance.getConstructorArguments() == null ? 0 : instance
                    .getConstructorArguments().size();
            parametersearch: for (final JavaParameter parameter : constructor.getParameters()) {
                if (i < numArgs) {
                    final MutableConstructorArgument arg = instance.getConstructorArguments()
                            .get(i);
                    arg.setType(parameter.getType().getValue());
                    arg.setPrimitive(parameter.getType().isPrimitive());
                    arguments.add(arg);
                    i++;
                    continue parametersearch;
                }
                // don't do autowiring for constructors with unset primitive types
                if (parameter.getType().isPrimitive()) {
                    continue constructorsearch;
                }

                final MutableInstance newInstance;
                // try to find an instance by type
                newInstance = findInstanceByType(context, instance.getName(),
                        "constructor parameter " + parameter.getName(), parameter.getType()
                                .getValue());
                // proper bean not found
                if (newInstance == null || !newInstance.isAutowireCandidate()) {
                    continue constructorsearch;
                }

                final MutableConstructorArgument arg = new MutableConstructorArgument(instance);
                final MutableInstanceReference ref = new MutableInstanceReference(arg, newInstance
                        .getName());
                ref.setReferencedId(newInstance.getId());
                ref.setId("source_autowire" + counter++);
                arg.setPrimitive(false);
                arg.setSource(ref);
                arg.setType(newInstance.getType());
                arg.setInstance(instance);
                arguments.add(arg);
            }

            break;
        }

        if (arguments.size() > 0) {
            instance.setConstructorArguments(arguments);
        } else {
            throw new RuntimeException("could not autowire constructor in instance "
                    + instance.getName() + ", type " + instance.getType());
        }
    }

    private void sortConstructors(final List<JavaMethod> constructors) {
        Collections.sort(constructors, new Comparator<JavaMethod>() {
            public int compare(final JavaMethod c1, final JavaMethod c2) {
                final boolean p1 = c1.isPublic();
                final boolean p2 = c2.isPublic();
                if (p1 != p2) {
                    return (p1 ? -1 : 1);
                }
                final int c1pl = c1.getParameters().length;
                final int c2pl = c2.getParameters().length;
                return (new Integer(c1pl)).compareTo(new Integer(c2pl)) * -1;
            }
        });
    }

    private List<JavaMethod> findMatchingConstructors(final JavaClass cl,
            final MutableInstance instance, final MutableContext context) {

        final ArrayList<JavaMethod> constructors = new ArrayList<JavaMethod>();

        final List<MutableConstructorArgument> arguments = instance.getConstructorArguments();

        // If there is no factory method
        if (instance.getFactoryMethod() == null) {

            for (final JavaMethod method : cl.getMethods()) {
                if (method.isConstructor()
                        && (arguments == null || matches(arguments, method.getParameters()))) {
                    constructors.add(method);
                }
            }

            if (constructors.size() == 0) {
                throw new RuntimeException(
                        "Failed autowiring by constructor, no matching constructor "
                                + instance.getName());
            }

        } else {
            throw new RuntimeException("Failed, autowiring by using factory method not supported "
                    + instance.getName());
        }

        return constructors;
    }

    private boolean matches(final List<MutableConstructorArgument> arguments,
            final JavaParameter[] parameters) {
        int i;
        if (parameters.length < arguments.size()) {
            return false;
        }
        for (i = 0; i < arguments.size(); i++) {
            if (!(((parameters[i].getType().isPrimitive() || "java.lang.String"
                    .equals(parameters[i].getType().getValue())) && arguments.get(i).getType() == null) || parameters[i]
                    .getType().getValue().equals(arguments.get(i).getType()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Completes metadata for the source.
     * 
     * @param source
     *            The {@link Source} for which metadata needs to be completed.
     * @param context
     *            The context containing all beans, for resolving references.
     */
    private void attribute(final Source source, final MutableContext context) {
        switch (source.getSourceType()) {
            case Instance:
                attribute((MutableInstance) source, context);
                break;
            case List:
                attribute((MutableListSource) source, context);
                break;
            case Map:
                attribute((MutableMapSource) source, context);
                break;
            case InstanceReference:
                attribute((MutableInstanceReference) source, context);
                break;
            case StringRepresentation:
            default:
        }
    }

    /**
     * Completes metadata for the {@link MutableListSource} passed in.
     * 
     * @param source
     *            The {@link MutableListSource} that needs to be completed.
     * @param context
     *            The context containing all beans, for resolving references.
     */
    private void attribute(final MutableListSource source, final MutableContext context) {
        for (final Source element : source.getElementSources()) {
            attribute(element, context);
        }
    }

    /**
     * Completes metadata for the {@link MutableListSource} passed in.
     * 
     * @param source
     *            The {@link MutableListSource} that needs to be completed.
     * @param context
     *            The context containing all beans, for resolving references.
     */
    private void attribute(final MutableMapSource source, final MutableContext context) {
        for (final MapSource.Entry entry : source.getEntries()) {
            attribute(entry.getKey(), context);
            attribute(entry.getValue(), context);
        }
    }

    /**
     * Completes metadata of the {@link MutableInstanceReference} passed in.
     * 
     * @param reference
     *            The {@link MutableInstanceReference} to be completed.
     * @param context
     *            The context containing all beans, for resolving references.
     */
    private void attribute(final MutableInstanceReference reference,
            final MutableContext context) {
        reference.setReferencedId(context.getByName(reference.getName()).getId());
    }

}
