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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import me.springframework.di.Configuration;
import me.springframework.di.Instance;
import me.springframework.di.ListSource;
import me.springframework.di.Sink;
import me.springframework.di.Source;
import me.springframework.di.base.MutableConfiguration;


/**
 * A number of utility operations for creating a {@link Configuration} from a
 * list of {@link Instance}s passed in.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class ConfigurationProducer {

    /**
     * Returns a {@link Configuration} from the instances passed in.
     * 
     * @param instances
     *            The root instances for which we need a {@link Configuration}.
     * @return The {@link Configuration} from the instances passed in.
     */
    public static Configuration produce(Map<String, ? extends Instance> instances) {
        MutableConfiguration result = new MutableConfiguration();
        result.getPublicInstances().addAll(instances.values());
        for (Instance instance : result.getPublicInstances()) {
            processInstance(result, instance);
        }
        return result;
    }

    private static void processInstance(MutableConfiguration result, Instance instance) {
        Collection<Sink> sinks = getSinks(instance);
        for (Sink sink : sinks) {
            processSource(sink.getSource(), result);
        }
    }

    private static void processSource(Source source, MutableConfiguration result) {
        switch (source.getSourceType()) {
            case List:
                ListSource listSource = (ListSource) source;
                result.getListSources().add(listSource);
                for (Source element : listSource.getElementSources()) {
                    processSource(element, result);
                }
                break;
            case Instance:
                Instance instance = (Instance) source;
                result.getInstanceSources().add(instance);
                processInstance(result, instance);
        }
    }

    private static Collection<Sink> getSinks(Instance instance) {
        Collection<Sink> sinks = new ArrayList<Sink>();
        if (instance.getConstructorArguments() != null) {
            sinks.addAll(instance.getConstructorArguments());
        }
        if (instance.getSetters() != null) {
            sinks.addAll(instance.getSetters());
        }
        return sinks;
    }

}
