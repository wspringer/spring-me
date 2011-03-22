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
import java.util.Iterator;
import java.util.List;

import me.springframework.di.MapSource;
import me.springframework.di.Source;
import me.springframework.di.base.AbstractSink;
import me.springframework.di.base.MutableConstructorArgument;
import me.springframework.di.base.MutableContext;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.base.MutableInstanceReference;
import me.springframework.di.base.MutableListSource;
import me.springframework.di.base.MutableMapSource;
import me.springframework.di.base.MutablePropertySetter;

import com.agilejava.blammo.BlammoLoggerFactory;
import com.agilejava.blammo.LoggingKitAdapter;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;


/**
 * {@linkplain Augmentation Augmentation} based on metadata gathered using QDox.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class QDoxAugmentation implements Augmentation {

    /**
     * The object providing access to the sources.
     */
    private JavaDocBuilder builder;

    /**
     * The logger, providing data on progress on completing the metadata.
     */
    private Logger logger = (Logger) BlammoLoggerFactory.create(Logger.class);

    /**
     * Constructs a new instance, accepting the {@link JavaDocBuilder} providing access to the class
     * definitions.
     * 
     * @param builder The object providing access to the metadata we need.
     */
    public QDoxAugmentation(JavaDocBuilder builder) {
        this.builder = builder;
    }

    /**
     * Provides a way to replace the {@link Logger} with something else.
     * 
     * @param adapter An adapter, adapting the {@link Logger} interface to some other, lower-level
     *            logging interface.
     */
    public void setLoggingKitAdapter(LoggingKitAdapter adapter) {
        logger = (Logger) BlammoLoggerFactory.create(Logger.class, adapter);
    }

    /**
     * Completes metadata for the {@link MutableInstance}s passed in. (Just the
     * {@link MutableInstance}s representing the top-level (root) beans.
     * 
     * @param context The context containing beans to be augmented.
     */
    public void augment(MutableContext context) {
        for (MutableInstance instance : context.getInstances().values()) {
            attribute(instance, context);
        }
    }

    /**
     * Completes metadata of the {@link MutableInstance} passed in, accepting
     * <code>allInstances</code> for resolving references.
     * 
     * @param instance The instance that must be completed.
     * @param context All beans in the context, for resolving references.
     */
    private void attribute(MutableInstance instance, MutableContext context) {
        guaranteeType(instance, context);
        guaranteeTypes(context, instance.getConstructorArguments());
        guaranteeTypes(context, instance.getSetters());

        logger.logAttributing(instance.getName());
        JavaClass cl = builder.getClassByName(instance.getType());
        if (isFactoryBean(cl)) {
            logger.logFoundFactoryBean(instance.getName());
            instance.setFactoryBean(true);
        }
        if (isInitializingBean(cl)) {
            instance.setInitMethod("afterPropertiesSet");
        }
        for (MutablePropertySetter setter : instance.getSetters()) {
            BeanProperty property = cl.getBeanProperty(setter.getName(), true);
            if (property == null) {
                logger.logNoSuchProperty(setter.getName(), cl.getName());
            } else {
                setter.setType(property.getType().getValue());
                setter.setPrimitive(property.getType().isPrimitive());
                attribute(setter.getSource(), context);
            }
        }
        if (instance.getConstructorArguments() != null && instance.getConstructorArguments().size() > 0) {
            logger.logInformConstructorArguments(instance.getName());
            for (MutableConstructorArgument argument : instance.getConstructorArguments()) {
                attribute(argument.getSource(), context);
            }
            List<MutableConstructorArgument> arguments = instance.getConstructorArguments();
            JavaMethod method = null;

            // If there is no factory method
            if (instance.getFactoryMethod() == null) {
                logger.logPlainOldConstructor(instance.getName());
                method = findConstructor(cl, arguments);
                if (method == null) {
                    logger.logNoMatchingConstructor(instance);
                }

                // If there *is* a factory method, but no factory bean
            } else if (instance.getFactoryInstance() == null) {
                logger.logFactoryMethod(instance.getName(), instance.getFactoryMethod());
                JavaClass factoryClass = builder.getClassByName(instance.getReferencedType());
                method = findMethod(factoryClass, true, instance.getFactoryMethod(), arguments);
                if (method == null) {
                    logger.logNoMatchingFactoryMethod(instance);
                }

                // If there *is* a factory method, *and* a factory bean
            } else {
                MutableInstance factoryInstance = context.getByName(instance.getFactoryInstance());
                logger.logFactoryBean(instance.getName(), factoryInstance.getName(), instance.getFactoryMethod());
                JavaClass factoryClass = builder.getClassByName(factoryInstance.getType());
                method = findMethod(factoryClass, false, instance.getFactoryMethod(), arguments);
                if (method == null) {
                    logger.logNoMatchingFactoryInstanceMethod(instance);
                }
            }
            if (method != null) {
                copyTypes(method.getParameters(), arguments);
            }
        }
    }

    private boolean isInitializingBean(JavaClass cl) {
        return cl.isA("org.springframework.beans.factory.InitializingBean");
    }

    private boolean isFactoryBean(JavaClass cl) {
        return cl.isA("org.springframework.beans.factory.FactoryBean");
    }

    /**
     * Guarantee that the type data is available.
     * 
     * @param instance The MutableInstance for which we need type data.
     * @param context All beans in the context, for resolving references.
     */
    private void guaranteeType(MutableInstance instance, MutableContext context) {
        logger.logGuaranteeingTypeKnown(instance.getId(), instance.getType() == null);
        if (instance.getType() != null) {
            return;
        }

        // In case of no factory method
        if (instance.getFactoryMethod() == null) {
            instance.setType(instance.getReferencedType());

        } else { // In case of a factory method

            // In case of a static factory method
            if (instance.getFactoryInstance() == null) {
                List<MutableConstructorArgument> arguments = instance.getConstructorArguments();
                if (arguments == null) {
                    arguments = new ArrayList<MutableConstructorArgument>();
                }
                JavaClass factoryClass = builder.getClassByName(instance.getReferencedType());
                JavaMethod method = findMethod(factoryClass, true, instance.getFactoryMethod(), arguments);
                instance.setType(method.getReturns().toString());

            } else { // If there is a bean factory

                // Find that factory
                MutableInstance factoryInstance = context.getByName(instance.getFactoryInstance());
                guaranteeType(factoryInstance, context);
                List<MutableConstructorArgument> arguments = instance.getConstructorArguments();
                if (arguments == null) {
                    arguments = new ArrayList<MutableConstructorArgument>();
                }
                JavaClass factoryClass = builder.getClassByName(factoryInstance.getReferencedType());
                JavaMethod method = findMethod(factoryClass, false, instance.getFactoryMethod(), arguments);
                logger.logFoundFactory(instance.getId(), factoryInstance.getId(), instance.getFactoryMethod(), method.getReturns().toString());
                instance.setType(method.getReturns().toString());
            }
        }
    }

    /**
     * Ensures that the type of the source is known for every sink.
     *
     * @param context The context for resolving references.
     * @param sinks The sinks for which source types should be resolved.
     */
    private void guaranteeTypes(MutableContext context, Collection<? extends AbstractSink> sinks) {
        if (sinks == null) {
           return;
        }
        for (AbstractSink sink : sinks) {
            Source source = sink.getSource();
            if (source.getType() == null) {
                guaranteeType(context, source);
            }
        }
    }

    /**
     * Ensures that the type of the given source is known.
     *
     * @param context The context for resolving references.
     * @param sinks The source that should have its type resolved.
     */
    private void guaranteeType(MutableContext context, Source source) {
        if (source instanceof MutableInstanceReference) {
            MutableInstanceReference ref = (MutableInstanceReference) source;
            MutableInstance referent = context.getByName(ref.getName());
            if (referent != null) {
                guaranteeType(referent, context);
                ref.setType(referent.getType());
                ref.setPrimitive(referent.isPrimitive());
            }
        }
    }

    /**
     * Finds a method with the given name on the class passed in, potentially static, wit the given
     * number of arguments.
     * 
     * @param cl The class that is supposed to prevent this method.
     * @param isStatic A boolean indicating if we are searching for a static operation.
     * @param name The name of the operation.
     * @param arguments The argument types.
     * @return A {@link JavaMethod} representing the method found, or <code>null</code> if no such
     *         method was found.
     */
    private JavaMethod findMethod(JavaClass cl, boolean isStatic, String name, List<MutableConstructorArgument> arguments) {
        for (JavaMethod method : cl.getMethods(true)) {
            if (name.equals(method.getName()) && method.isStatic() == isStatic && match(arguments, method.getParameters())) {
                return method;
            }
        }
        return null;
    }

    /**
     * Finds a constructor with the given type of arguments.
     * 
     * @param cl The {@link JavaClass} that should provide that constructor.
     * @param arguments The arguments of the constructor.
     * @return A {@link JavaMethod} representation of that constructor.
     */
    private JavaMethod findConstructor(JavaClass cl, List<MutableConstructorArgument> arguments) {
        for (JavaMethod method : cl.getMethods()) {
            if (method.isConstructor()) {
                logger.logCheckConstructor(method.getDeclarationSignature(true));
                if (match(arguments, method.getParameters())) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Copy type information from an array of {@link JavaParameter}s to a list of
     * {@link me.springframework.di.base.MutableConstructorArgument}s. (One by one.)
     * 
     * @param parameters The {@link JavaParameter}s.
     * @param arguments The {@link MutableConstructorArgument}s.
     */
    private void copyTypes(JavaParameter[] parameters, List<MutableConstructorArgument> arguments) {
        for (int i = 0; i < parameters.length; i++) {
            arguments.get(i).setType(parameters[i].getType().getValue());
            arguments.get(i).setPrimitive(parameters[i].getType().isPrimitive());
        }
    }

    /**
     * Checks if the arguments and parameters match.
     * 
     * @param arguments The arguments.
     * @param parameters The parameters.
     * @return A boolean indicating if they match.
     */
    private boolean match(List<MutableConstructorArgument> arguments, JavaParameter[] parameters) {
        if (arguments.size() != parameters.length) {
            logger.logNumberOfArgumentsDontMatch();
            return false;
        } else {
            for (int i = 0; i < arguments.size(); i++) {
                if (!match(arguments.get(i), parameters[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean match(MutableConstructorArgument mutableConstructorArgument, JavaParameter javaParameter) {
        String argType = mutableConstructorArgument.getType();
        if (argType == null) {
            logger.logConstructorArgumentTypeIsNull();
            return false;
        }
        if (javaParameter.getType().isResolved()) {
            if (builder.getClassByName(argType).isA(javaParameter.getType().getJavaClass())) {
                return true;
            } else {
                logger.logConstructorArgumentTypeMismatch(argType, javaParameter.getType());
                return false;
            }
        } else {
            logger.logUnresolvedConstructorArgumentType();
            return true;
        }
    }

    /**
     * Completes metadata for the source.
     * 
     * @param source The {@link Source} for which metadata needs to be completed.
     * @param context All beans in the context, for resolving references.
     */
    private void attribute(Source source, MutableContext context) {
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
     * @param source The {@link MutableListSource} that needs to be completed.
     * @param allInstances All other {@link MutableInstance}s, for resolving references.
     */
    private void attribute(MutableListSource source, MutableContext context) {
        for (Source element : source.getElementSources()) {
            attribute(element, context);
        }
    }

    /**
     * Completes metadata for the {@link MutableListSource} passed in.
     * 
     * @param source The {@link MutableListSource} that needs to be completed.
     * @param context All beans in the context, for resolving references.
     */
    private void attribute(MutableMapSource source, MutableContext context) {
        for (MapSource.Entry entry : source.getEntries()) {
            attribute(entry.getKey(), context);
            attribute(entry.getValue(), context);
        }
    }

    /**
     * Completes metadata of the {@link MutableInstanceReference} passed in.
     * 
     * @param reference The {@link MutableInstanceReference} to be completed.
     * @param context All beans in the context, for resolving references.
     */
    private void attribute(MutableInstanceReference reference, MutableContext context) {
        MutableInstance referent = context.getByName(reference.getName());
        reference.setName(referent.getName());
        reference.setReferencedId(referent.getId());
    }

    /**
     * The logger, logging conditions encountered while completing metadata.
     * 
     * @blammo.logger
     */
    public interface Logger {

        /**
         * The system is starting work to complete the model for specified bean.
         * 
         * @param name The name the instance being attributed.
         * @blammo.level debug
         * @blammo.message Starting work to complete model for bean {name}
         */
        void logAttributing(String name);

        /**
         * The system does not have enough type information to be able to judge if the constructor
         * argument referenced in the Spring configuration matches the one of the constructor it is
         * inspecting, so it will assume they match.
         * 
         * @blammo.level warn
         * @blammo.message Unresolved constructor type; assuming match
         */
        void logUnresolvedConstructorArgumentType();

        /**
         * The system detects incompatibility in two constructor arguments.
         * 
         * @param expected
         * @param actual
         * @blammo.level debug
         * @blammo.message Constructor argument was expected to be {expected}, but was {actual}
         */
        void logConstructorArgumentTypeMismatch(String expected, Type actual);

        /**
         * The system notices that a constructor argument doesn't have its type set.
         * 
         * @blammo.level error
         * @blammo.message Constructor argument type is null
         */
        void logConstructorArgumentTypeIsNull();

        /**
         * The system checked a constructor, but finds that the number of arguments doesn't match
         * the number expected.
         * 
         * @blammo.level debug
         * @blammo.message Number of arguments does not meet expectations
         */
        void logNumberOfArgumentsDontMatch();

        /**
         * The system is checking if this constructor may be the one.
         * 
         * @param callSignature The signature of the constructor.
         * @blammo.level debug
         * @blammo.message Checking constructor "{callSignature}"
         */
        void logCheckConstructor(String callSignature);

        /**
         * The system detects that the factory set for instance does not have the required factory
         * method.
         * 
         * @param instance The instance that needs to created.
         * @blammo.message No corresponding factory method on the factory defined for {instance}.
         * @blammo.level error
         */
        void logNoMatchingFactoryInstanceMethod(MutableInstance instance);

        /**
         * The system detects that there is no corresponding factory method for this instance.
         * 
         * @param instance The instance that needs to be created.
         * @blammo.message No corresponding factory method for {instance}.
         * @blammo.level error
         */
        void logNoMatchingFactoryMethod(MutableInstance instance);

        /**
         * The system detects that there is no corresponding constructor for this instance.
         * 
         * @param instance The instance for which we need a constructor.
         * @blammo.message No corresponding constructor for {instance}.
         * @blammo.level error
         */
        void logNoMatchingConstructor(MutableInstance instance);

        /**
         * The system notices that a property does not exist.
         * 
         * @param propertyName The name of the property missing.
         * @param className The class on which this property should have been defined.
         * @blammo.message Class {className} does not have a property called {propertyName}
         * @blammo.level error
         */
        void logNoSuchProperty(String propertyName, String className);

        /**
         * The system found the factory method for the given instance.
         * 
         * @param instance
         * @param factoryInstance
         * @param factoryMethod
         * @param type
         * @blammo.level debug
         * @blammo.message Creating bean {instance} using #{factoryMethod}() on instance
         *                 {factoryInstance}, returning type {type}
         */
        void logFoundFactory(String instance, String factoryInstance, String factoryMethod, String type);

        /**
         * The system verifies if the type is known for the given bean.
         * 
         * @param id The identifier of the bean.
         * @blammo.level debug
         * @blammo.message Check type known for bean {id}: {known}
         */
        void logGuaranteeingTypeKnown(String id, boolean known);

        /**
         * The system trying to find a constructor.
         * 
         * @param size The number of constructor argument defined.
         * @param match A boolean, indicating if it matches the current constructor.
         * @blammo.level debug
         * @blammo.message Checking a constructor, to see if the number of arguments is {size}:
         *                 {match}
         */
        void logComparingArguments(int size, boolean match);

        /**
         * The system is informing that the specified bean will be constructed using a factory
         * method defined on another bean.
         * 
         * @param name The name of the bean that will be constructed.
         * @param factoryInstance The name of the bean that will construct the instance.
         * @param factoryMethod The factory method on that bean.
         * @blammo.level debug
         * @blammo.message Bean {name} will be constructed using factory method {factoryMethod} on
         *                 {factoryInstance}
         */
        void logFactoryBean(String name, String factoryInstance, String factoryMethod);

        /**
         * The system is informing that the specified bean will be constructed using the factory
         * method defined on the class itself.
         * 
         * @param name The name of the bean.
         * @param factoryMethod The (static) factory method defined to construct the instance.
         * @blammo.level debug
         * @blammo.message Bean {name} will be constructed using the factory method {factoryMethod}
         */
        void logFactoryMethod(String name, String factoryMethod);

        /**
         * The system is informing that the specified bean will be constructed using a plain old
         * constructor.
         * 
         * @param name The name of the bean being created using a plain old constructor.
         * @blammo.level debug
         * @blammo.message Bean {name} will be constructed using its own constructor
         */
        void logPlainOldConstructor(String name);

        /**
         * The system is informing that the specified bean is configured by constructor arguments.
         * 
         * @param name The name of the instance currently processed.
         * @blammo.level debug
         * @blammo.message Bean {name} will be configured using constructor argument settings
         */
        void logInformConstructorArguments(String name);

        /**
         * The system is informing that the specificied bean implements FactoryBean.
         *
         * @param name The name of the bean.
         * @blammo.level debug
         * @blammo.message Bean {name} is a FactoryBean
         */
        void logFoundFactoryBean(String name);
    }

}
