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
package me.springframework.di;

import java.util.List;
import java.util.Set;


/**
 * The interface to be implemented by a representation of an object instance.
 * 
 * @author Wilfred Springer
 * 
 */
public interface Instance extends Source {

    /**
     * The type reference by the definition of this instance. In case the
     * {@link #getFactoryInstance()} and {@link #getFactoryMethod()} are specified, this does not
     * need to be the same as the type returned by {@link #getType()}. In case only a
     * {@linkplain #getFactoryMethod() factory method} was specified, then the
     * {@linkplain #getType() type} produced is based on the return type of that static factory
     * method on the type defined by {@linkplain #getReferencedType()}.
     * 
     * @return The <em>referenced</em> type.
     */
    String getReferencedType();

    /**
     * Returns the name of the instance, in case it's a named instance.
     * 
     * @return The name of the instance, in case it's a named instance.
     */
    String getName();

    /**
     * Returns the property values set on properties defined by this instance.
     * 
     * @return A {@link Set} of {@link PropertySetter PropertySetters}, one for every property set
     *         to a certain value.
     */
    Set<? extends PropertySetter> getSetters();

    /**
     * Returns constructor arguments to be used while constructing an instance of this class.
     * Returns <code>null</code> if no constructor arguments are specified.
     * 
     * @return A {@link List} of {@link ConstructorArgument ConstructorArguments}; the position of
     *         the {@link ConstructorArgument} corresponds to the location of the argument in the
     *         actual constructor to be used to instantiate this instance.
     */
    List<? extends ConstructorArgument> getConstructorArguments();

    /**
     * Returns the name of an initialization method, if one has been defined.
     * 
     * @return The name of an initialization method, if one has been defined.
     */
    String getInitMethod();

    /**
     * Returns the name of an destroy method, if one has been defined.
     * 
     * @return The name of an destroy method, if one has been defined.
     */
    String getDestroyMethod();

    /**
     * Returns the name of the method used to create an instance, or <code>null</code> if no such
     * factory method is set.
     * 
     * @return The name of the method used to create an instance.
     */
    String getFactoryMethod();

    /**
     * Returns the name of the instance implementing the factory method.
     * 
     * @return The name of the instance implementing the factory method.
     */
    String getFactoryInstance();

    /**
     * Returns a boolean indicating if this bean should be initialized lazily.
     * 
     * @return A boolean indicating if the bean should be initialized lazily.
     */
    boolean isLazyInit();

    /**
     * Returns the scope set for this instance. (Basically, this will influence how often and when
     * an instance will be created.)
     * 
     * @return The scope of the instance.
     */
    Scope getScope();
    
    /**
     * Returns <code>true</code> if {@link #getScope()} returns {@link Scope#SINGLETON}.
     * 
     * @return <code>true</code> if {@link #getScope()} returns {@link Scope#SINGLETON}.
     */
    boolean isSingleton();
    
    /**
     * Returns <code>true</code> if {@link #getScope()} returns {@link Scope#PROTOTYPE}.
     * 
     * @return <code>true</code> if {@link #getScope()} returns {@link Scope#PROTOTYPE}.
     */
    boolean isPrototype();

    /**
     * Returns <code>true</code> if the bean implements {@link org.springframework.beans.factory.FactoryBean}.
     */
    boolean isFactoryBean();

}
