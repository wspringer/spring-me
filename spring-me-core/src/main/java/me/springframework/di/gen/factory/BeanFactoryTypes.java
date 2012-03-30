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
package me.springframework.di.gen.factory;

/**
 * 
 * @author Wilfred Springer
 * 
 */
public enum BeanFactoryTypes implements BeanFactoryType {

    /**
     * A minimal bean factory, targeting Java ME.
     */
    MINIMAL_JAVA_ME("java.lang.RuntimeException",
            "java.util.Vector",
            "copyInto",
            null,
            "java.util.Hashtable",
            null,
            "addElement"),

    /**
     * A minimal bean factory, targeting Java SE.
     */
    MINIMAL_JAVA_SE("java.lang.RuntimeException",
            "java.util.ArrayList",
            "toArray",
            "java.util.LinkedHashSet",
            "java.util.HashMap",
            "java.util.Properties",
            "add"),

    /**
     * A minimal bean factory, targeting Java SE.
     */
    JAVA_SE("me.springframework.beans.BeansException",
            "java.util.ArrayList",
            "toArray",
            "java.util.LinkedHashSet",
            "java.util.HashMap",
            "java.util.Properties",
            "add",
            "me.springframework.beans.factory.MinimalBeanFactory");

    /**
     * @see BeanFactoryType#getBeansExceptionName()
     */
    private String beansExceptionName;

    /**
     * @see BeanFactoryType#getListImplementationName()
     */
    private String listImplementationName;

    private String listToArrayMethod;

    /**
     * @see BeanFactoryType#getListAppendMethodName()
     */
    private String listAppendMethodName;

    private String setImplementationName;

    /**
     * @see BeanFactoryType#getMapImplementationName()
     */
    private String mapImplementationName;

    private String propertiesImplementationName;

    /**
     * @see BeanFactoryType#getInterfaceNames()
     */
    private String[] interfaceNames;

    private BeanFactoryTypes(String beansExceptionName,
            String listImplementationName,
            String listToArrayMethod,
            String setImplementationName,
            String mapImplementationName,
            String propertiesImplementationName,
            String listAppendMethodName,
            String... interfaceNames) {
        this.beansExceptionName = beansExceptionName;
        this.listImplementationName = listImplementationName;
        this.listToArrayMethod = listToArrayMethod;
        this.setImplementationName = setImplementationName;
        this.mapImplementationName = mapImplementationName;
        this.propertiesImplementationName = propertiesImplementationName;
        this.listAppendMethodName = listAppendMethodName;
        this.interfaceNames = interfaceNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.springframework.di.gen.factory.BeanFactoryType#getBeanFactoryExceptionName()
     */
    public String getBeansExceptionName() {
        return beansExceptionName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.springframework.di.gen.factory.BeanFactoryType#getListImplementationName()
     */
    public String getListImplementationName() {
        return listImplementationName;
    }

    public String getListToArrayMethod() {
        return listToArrayMethod;
    }

    public String getSetImplementationName() {
        return setImplementationName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.springframework.di.gen.factory.BeanFactoryType#getMapImplementationName()
     */
    public String getMapImplementationName() {
        return mapImplementationName;
    }

    public String getPropertiesImplementationName() {
        return propertiesImplementationName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.springframework.di.gen.factory.BeanFactoryType#getInterfaceNames()
     */
    public String[] getInterfaceNames() {
        return interfaceNames;
    }

    public String getListAppendMethodName() {
        return listAppendMethodName;
    }

}
